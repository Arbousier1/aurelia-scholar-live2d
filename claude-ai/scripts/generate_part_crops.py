#!/usr/bin/env python3
"""
基于 source-coordinate-map.json 的 bbox 对参考图做粗切，
生成带透明边距的 PNG 与一份 SVG 叠加预览。
输出供画师在 Cubism/PSD 中精修，不是最终蒙版。
"""
import json
import os
import re
from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REF_PATH = os.path.join(ROOT, "reference.png")
COORD_PATH = os.path.join(ROOT, "source-coordinate-map.json")
OUT_DIR = os.path.join(ROOT, "parts")
PREVIEW_PATH = os.path.join(OUT_DIR, "layered-preview.html")
PADDING = 20


def safe_name(text: str) -> str:
    return re.sub(r"[^a-zA-Z0-9_\-]", "_", text).strip("_")[:64]


def draw_label(img: Image.Image, text: str) -> Image.Image:
    """在左上角画一个半透明标签，方便看图。"""
    draw = ImageDraw.Draw(img)
    try:
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 14)
    except Exception:
        font = ImageFont.load_default()
    bbox = draw.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    margin = 4
    draw.rectangle([0, 0, tw + margin * 2, th + margin * 2], fill=(255, 200, 150, 180))
    draw.text((margin, margin), text, fill=(60, 30, 10, 255), font=font)
    return img


def main():
    with open(COORD_PATH, "r", encoding="utf-8") as f:
        coord = json.load(f)

    ref = Image.open(REF_PATH).convert("RGBA")
    w, h = ref.size

    # 商用清理区域画成红色半透明遮罩，生成一张警告图
    cleanup = ref.copy()
    draw = ImageDraw.Draw(cleanup)
    for item in coord["commercialCleanup"]:
        x1, y1, x2, y2 = item["bbox"]
        draw.rectangle([x1, y1, x2, y2], outline=(255, 0, 0, 255), width=3)
        draw.rectangle([x1, y1, x2, y2], fill=(255, 0, 0, 80))
    cleanup_path = os.path.join(OUT_DIR, "_commercial_cleanup_overlay.png")
    cleanup.save(cleanup_path)

    os.makedirs(OUT_DIR, exist_ok=True)
    html_parts = []

    for r in coord["regions"]:
        rid = r["id"]
        group = r["group"]
        x1, y1, x2, y2 = r["bbox"]
        # 裁剪
        crop = ref.crop((max(0, x1 - PADDING), max(0, y1 - PADDING),
                         min(w, x2 + PADDING), min(h, y2 + PADDING)))
        # 画边框和标签
        draw = ImageDraw.Draw(crop)
        draw.rectangle([PADDING, PADDING, crop.width - PADDING, crop.height - PADDING],
                       outline=(255, 128, 0, 255), width=2)
        labeled = draw_label(crop.copy(), f"{rid} ({group})")
        fname = f"{safe_name(rid)}.png"
        labeled.save(os.path.join(OUT_DIR, fname))

        html_parts.append({
            "id": rid,
            "group": group,
            "file": fname,
            "bbox": r["bbox"],
            "notes": r.get("notes", ""),
            "priority": r.get("priority", "C"),
        })

    # 生成 HTML 叠加预览：所有 bbox 画在原图上
    preview_html = f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<title>Claude AI - 粗切叠加预览</title>
<style>
body {{ font-family: system-ui, sans-serif; background: #1a1a1a; color: #eee; margin: 0; }}
.container {{ display: flex; height: 100vh; }}
.sidebar {{ width: 320px; overflow-y: auto; padding: 16px; background: #222; border-right: 1px solid #444; }}
.sidebar h2 {{ margin-top: 0; font-size: 16px; }}
.sidebar .part {{ padding: 8px; margin-bottom: 6px; border-radius: 6px; cursor: pointer; font-size: 13px; border: 1px solid #444; }}
.sidebar .part:hover {{ background: #333; }}
.sidebar .priority-S {{ border-left: 4px solid #ff6b6b; }}
.sidebar .priority-A {{ border-left: 4px solid #feca57; }}
.sidebar .priority-B {{ border-left: 4px solid #48dbfb; }}
.stage {{ flex: 1; display: flex; align-items: center; justify-content: center; overflow: auto; background: #111; }}
.stage img {{ max-width: 95%; max-height: 95%; box-shadow: 0 0 20px rgba(0,0,0,0.5); }}
.stage .overlay {{ position: absolute; border: 2px solid rgba(255,128,0,0.85); background: rgba(255,128,0,0.12); pointer-events: none; }}
.stage .label {{ position: absolute; background: rgba(255,200,150,0.9); color: #222; padding: 2px 6px; font-size: 11px; border-radius: 3px; pointer-events: none; }}
.warn {{ color: #ff6b6b; font-size: 12px; margin-top: 8px; }}
</style>
</head>
<body>
<div class="container">
<div class="sidebar">
<h2>Claude AI 粗切部位 ({len(html_parts)})</h2>
<p style="font-size:12px;color:#aaa;">点击列表可在图上定位。红色框为需商用清理的第三方文字/Logo。</p>
"""
    for p in html_parts:
        cls = f"priority-{p['priority']}"
        preview_html += f"""<div class="part {cls}" data-id="{p['id']}"><b>{p['id']}</b> · {p['group']}<br><span style="color:#aaa">{p['notes'][:60]}{'...' if len(p['notes'])>60 else ''}</span></div>\n"""

    preview_html += """<div class="warn">警告：商用前必须替换参考图中所有 Anthropic/Claude 品牌元素与第三方文字。</div></div>
<div class="stage" id="stage">
"""
    # 以原图为底
    preview_html += f'<img src="../reference.png" id="refImg" style="position:relative;">\n'
    # 叠加 bbox
    for p in html_parts:
        x1, y1, x2, y2 = p["bbox"]
        preview_html += f'<div class="overlay" data-id="{p["id"]}" style="left:{x1}px;top:{y1}px;width:{x2-x1}px;height:{y2-y1}px;"></div>\n'
        preview_html += f'<div class="label" data-id="{p["id"]}" style="left:{x1}px;top:{y1}px;">{p["id"]}</div>\n'
    # 商用清理区域
    for item in coord["commercialCleanup"]:
        x1, y1, x2, y2 = item["bbox"]
        preview_html += f'<div class="overlay" style="left:{x1}px;top:{y1}px;width:{x2-x1}px;height:{y2-y1}px;border-color:#ff3333;background:rgba(255,0,0,0.25);"></div>\n'

    preview_html += """
</div>
</div>
<script>
const parts = document.querySelectorAll('.part');
const overlays = document.querySelectorAll('.overlay[data-id], .label[data-id]');
parts.forEach(p => p.addEventListener('click', () => {
  const id = p.dataset.id;
  overlays.forEach(o => o.style.borderColor = o.dataset.id === id ? '#00ff88' : '');
  overlays.forEach(o => {
    if (o.dataset.id === id && o.classList.contains('overlay')) {
      o.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'center' });
    }
  });
}));
</script>
</body>
</html>
"""
    with open(PREVIEW_PATH, "w", encoding="utf-8") as f:
        f.write(preview_html)

    print(f"Generated {len(html_parts)} parts in {OUT_DIR}")
    print(f"Overlay preview: {PREVIEW_PATH}")


if __name__ == "__main__":
    main()
