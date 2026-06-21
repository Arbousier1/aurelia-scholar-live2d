# Claude AI 拟人角色 Live2D 拆解工作台

这是为 Claude AI 全身立绘新建的专业 Live2D 拆分工单，结构与 `/` 下的 Aurelia Scholar 项目保持一致。

## 入口

本地启动 HTTP 服务后访问：

```bash
python3 -m http.server 8080 --directory /workspace
# 打开 http://localhost:8080/claude-ai/
```

## 文件说明

- `decomposition-plan.json`：拆分组、部件、参数与生产注意事项。
- `source-coordinate-map.json`：基于 850×1106 参考图的第一轮部位坐标与商用清理区域。
- `layer-inventory.csv`：PSD/Cubism 图层清单（约 75 行核心图层）。
- `parameter-binding.json`：Cubism 参数到图层/部件的绑定表。
- `layer-map.json`：前端层级 → PSD 分组 → Cubism Part → 参数的对照表。
- `QA_CHECKLIST.md`：版权、分层、参数、物理、表情动作、运行时交付检查项。
- `artist-instructions.md`：画师落地执行说明（命名规范、表情/动作目标、分层细节、交付 checklist）。
- `scripts/generate_part_crops.py`：读取 `reference.png` 自动生成 `parts/*.png` 粗切与 `parts/layered-preview.html`。
- `runtime/`：Cubism 风格运行时占位包（`model3.json`、表情、动作、物理、姿态）。

## 生产目标

- 200+ PSD 图层
- 16+ 表情
- 10+ 动作
- 10 组物理（长发双麻花辫、披肩、裙摆、腰带、怀表链、呆毛等）

## 商用提醒

参考图包含 Claude/Anthropic 品牌 Logo、角色信息卡与文字标语，商业化前必须替换为原创设计。本工单已在 `source-coordinate-map.json` 的 `commercialCleanup` 中列出需处理区域。
