#!/usr/bin/env python3
"""Create first-pass Photoshop/Live2D split PNG layers from the clean reference image.

The input is the user-supplied 1024x1536 clean character reference. This script
uses the hand-authored coordinate map and produces real transparent PNG layer
assets: tight crops, full-canvas layers, alpha masks, a contact sheet and a
machine-readable manifest. It intentionally does not claim to replace final
artist cleanup: hair strands, lace, chains and occluded redraws still need
manual PSD refinement.
"""
from __future__ import annotations

import json
import math
import shutil
import zipfile
from collections import deque
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter

ROOT = Path(__file__).resolve().parents[1]
PACKAGE = ROOT / "live2d" / "aurelia-scholar-live2d"
SOURCE = PACKAGE / "source" / "clean-reference.jpg"
COORDS = PACKAGE / "split" / "clean-reference-coordinate-map.json"
OUT = PACKAGE / "split" / "output-clean-reference"

WHITE_DISTANCE = 36
EDGE_SCAN = 2
EDGE_EXPAND_STEPS = 2


def slug(name: str) -> str:
    return name.lower().replace("_", "-").replace(" ", "-")


def is_bg(px: tuple[int, int, int]) -> bool:
    r, g, b = px
    # White/off-white paper background with slight JPEG warmth.
    return abs(255 - r) + abs(255 - g) + abs(255 - b) <= WHITE_DISTANCE and max(px) >= 232


def edge_connected_bg_mask(crop: Image.Image) -> Image.Image:
    rgb = crop.convert("RGB")
    w, h = rgb.size
    pix = rgb.load()
    seen = [[False] * h for _ in range(w)]
    alpha = Image.new("L", (w, h), 255)
    ap = alpha.load()
    q: deque[tuple[int, int]] = deque()

    def enqueue(x: int, y: int) -> None:
        if 0 <= x < w and 0 <= y < h and not seen[x][y] and is_bg(pix[x, y]):
            seen[x][y] = True
            q.append((x, y))

    for x in range(w):
        for y in range(min(EDGE_SCAN, h)):
            enqueue(x, y)
            enqueue(x, h - 1 - y)
    for y in range(h):
        for x in range(min(EDGE_SCAN, w)):
            enqueue(x, y)
            enqueue(w - 1 - x, y)

    while q:
        x, y = q.popleft()
        ap[x, y] = 0
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            enqueue(nx, ny)

    # Softly remove 1-2 px more off-white edge fringe without erasing interior white clothing.
    transparent = alpha.point(lambda a: 255 if a == 0 else 0)
    for _ in range(EDGE_EXPAND_STEPS):
        transparent = transparent.filter(ImageFilter.MaxFilter(3)) if False else transparent
    return alpha.filter(ImageFilter.GaussianBlur(0.35))


def trim_alpha(img: Image.Image, padding: int = 8) -> tuple[Image.Image, tuple[int, int, int, int]]:
    alpha = img.getchannel("A")
    bbox = alpha.getbbox()
    if not bbox:
        return img, (0, 0, img.width, img.height)
    l, t, r, b = bbox
    l = max(0, l - padding)
    t = max(0, t - padding)
    r = min(img.width, r + padding)
    b = min(img.height, b + padding)
    return img.crop((l, t, r, b)), (l, t, r, b)


def make_contact(entries: list[dict], thumb_w: int = 180) -> Image.Image:
    font = ImageFont.load_default()
    cols = 4
    cell_w, cell_h = 260, 260
    rows = math.ceil(len(entries) / cols)
    sheet = Image.new("RGB", (cols * cell_w, rows * cell_h), "#f8efe8")
    draw = ImageDraw.Draw(sheet)
    for i, entry in enumerate(entries):
        x = (i % cols) * cell_w
        y = (i // cols) * cell_h
        img = Image.open(entry["cropPng"]).convert("RGBA")
        scale = min((thumb_w) / img.width, 170 / img.height, 1)
        resized = img.resize((max(1, int(img.width * scale)), max(1, int(img.height * scale))), Image.Resampling.LANCZOS)
        px = x + (cell_w - resized.width) // 2
        py = y + 18
        bg = Image.new("RGBA", resized.size, (255, 255, 255, 255))
        checker = Image.new("RGBA", resized.size, (255, 255, 255, 0))
        cd = ImageDraw.Draw(checker)
        for yy in range(0, resized.height, 12):
            for xx in range(0, resized.width, 12):
                if (xx // 12 + yy // 12) % 2:
                    cd.rectangle([xx, yy, xx + 11, yy + 11], fill=(225, 210, 200, 255))
        bg.alpha_composite(checker)
        bg.alpha_composite(resized)
        sheet.paste(bg.convert("RGB"), (px, py))
        draw.rectangle([x + 8, y + 8, x + cell_w - 8, y + cell_h - 8], outline="#d8a083", width=1)
        draw.text((x + 12, y + 205), entry["id"][:36], fill="#6e3b27", font=font)
        draw.text((x + 12, y + 224), entry["group"][:36], fill="#8a614d", font=font)
        draw.text((x + 12, y + 242), str(entry["bbox"]), fill="#8a614d", font=font)
    return sheet


def main() -> None:
    if not SOURCE.exists():
        raise SystemExit(f"Missing source image: {SOURCE}")
    data = json.loads(COORDS.read_text(encoding="utf-8"))
    src = Image.open(SOURCE).convert("RGB")
    expected = (data["sourceImage"]["width"], data["sourceImage"]["height"])
    if src.size != expected:
        raise SystemExit(f"Source image size {src.size} does not match coordinate map {expected}")

    if OUT.exists():
        shutil.rmtree(OUT)
    crop_dir = OUT / "layers-crop-png"
    canvas_dir = OUT / "layers-canvas-png"
    mask_dir = OUT / "masks-alpha-png"
    for d in (crop_dir, canvas_dir, mask_dir):
        d.mkdir(parents=True, exist_ok=True)

    entries: list[dict] = []
    psd_groups: dict[str, object] = {}
    try:
        from psd_tools import PSDImage
        from psd_tools.api.layers import Group, PixelLayer
        psd = PSDImage.new("RGBA", src.size, (255, 255, 255, 0))
        ref_group = Group.new(psd, "00_REFERENCE")
        psd.append(ref_group)
        ref_layer = PixelLayer.frompil(src.convert("RGBA"), ref_group, "clean-reference__locked-source", top=0, left=0)
        ref_group.append(ref_layer)
        for region in data["regions"]:
            group_name = region["group"]
            if group_name not in psd_groups:
                g = Group.new(psd, group_name)
                psd.append(g)
                psd_groups[group_name] = g
    except Exception as exc:
        psd = None
        Group = PixelLayer = None
        print(f"PSD export disabled: {exc}")

    for idx, region in enumerate(data["regions"], 1):
        l, t, r, b = region["bbox"]
        raw = src.crop((l, t, r, b)).convert("RGBA")
        alpha = edge_connected_bg_mask(raw)
        raw.putalpha(alpha)
        tight, trim = trim_alpha(raw)
        name = f"{idx:02d}_{slug(region['group'])}__{slug(region['id'])}"
        crop_path = crop_dir / f"{name}.png"
        canvas_path = canvas_dir / f"{name}__canvas.png"
        mask_path = mask_dir / f"{name}__alpha.png"
        tight.save(crop_path)
        alpha.save(mask_path)
        canvas = Image.new("RGBA", src.size, (0, 0, 0, 0))
        canvas.alpha_composite(raw, (l, t))
        canvas.save(canvas_path)
        if psd is not None and PixelLayer is not None:
            layer = PixelLayer.frompil(raw, psd_groups[region["group"]], f"{idx:02d}_{region['id']}__FIRST_PASS", top=t, left=l)
            psd_groups[region["group"]].append(layer)

        entries.append({
            "index": idx,
            "id": region["id"],
            "group": region["group"],
            "bbox": region["bbox"],
            "trimWithinBbox": list(trim),
            "cropPng": str(crop_path.relative_to(ROOT)),
            "canvasPng": str(canvas_path.relative_to(ROOT)),
            "alphaMaskPng": str(mask_path.relative_to(ROOT)),
            "notes": region.get("notes", ""),
            "status": "first-pass-transparent-layer-needs-artist-edge-refine"
        })

    manifest = {
        "source": str(SOURCE.relative_to(ROOT)),
        "sourceSize": list(src.size),
        "layerCount": len(entries),
        "deliverableType": "first-pass-real-png-split-from-user-reference",
        "warning": "These are actual transparent PNG split layers generated from the supplied image. Final commercial PSD still needs manual mask cleanup, hidden-area redraw, and detailed sub-splitting for hair/lace/chains/fingers.",
        "psd": str((OUT / "aurelia_clean_reference_first_pass_split.psd").relative_to(ROOT)),
        "layers": entries,
    }
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / "manifest.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    make_contact(entries).save(OUT / "contact_sheet.png")
    readme = OUT / "README.md"
    readme.write_text("""# Clean Reference Direct Split Output

这里是从用户提供的 1024×1536 干净角色图直接生成的第一版可用拆分文件，不是只写方案。

## 交付文件

- `aurelia_clean_reference_first_pass_split.psd`：可在 Photoshop 中打开的分层 PSD。包含源图层和按 Live2D 组归类的 23 个第一版透明拆层。
- `layers-crop-png/`：每个部件的透明紧裁 PNG，适合画师逐个精修。
- `layers-canvas-png/`：每个部件保持 1024×1536 画布坐标的透明 PNG，适合直接叠回原位检查。
- `masks-alpha-png/`：每个区域的 alpha 蒙版。
- `contact_sheet.png`：所有拆层的快速预览图。
- `manifest.json`：每个部件的坐标、输出路径和状态。

## 质量说明

这些文件已经是从原图像素直接抠出的真实拆层资产；由于输入是一张合成后的 JPG/PNG 图，不可能自动恢复被遮挡的完整手臂、身体、发丝背面、蕾丝下层和链条背面。商用 Live2D 最终版仍需要在这个 PSD 基础上继续人工精修边缘、补画遮挡区域，并把发丝、蕾丝、手指、怀表链条继续拆成更细图层。
""", encoding="utf-8")
    psd_path = OUT / "aurelia_clean_reference_first_pass_split.psd"
    if psd is not None:
        psd.save(psd_path)
    zip_path = OUT / "aurelia_clean_reference_first_pass_split_assets.zip"
    with zipfile.ZipFile(zip_path, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=6) as zf:
        for item in [psd_path, OUT / "manifest.json", OUT / "contact_sheet.png", readme]:
            if item.exists():
                zf.write(item, item.relative_to(OUT))
        for folder in [crop_dir, canvas_dir, mask_dir]:
            for item in sorted(folder.rglob("*.png")):
                zf.write(item, item.relative_to(OUT))
    print(f"Wrote {len(entries)} split layers to {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
