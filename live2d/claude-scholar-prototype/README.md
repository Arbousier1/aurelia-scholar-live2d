# Claude Scholar Live2D Prototype

这是一个可直接打开的浏览器 Live2D 风格原型，用分层 SVG、CSS 动画和少量 JavaScript 实现参考图中的橙白系学者 AI 少女形象。

## 使用方式

```bash
python3 -m http.server 8080 --directory live2d/claude-scholar-prototype
```

然后打开：

```text
http://localhost:8080
```

也可以直接用浏览器打开 `index.html`。

## 已实现内容

- 分层角色：后发、侧发、前发、脸、五官、眼镜、披肩、裙装、手臂、书本、怀表、纸张特效。
- 物理感动画：呼吸、头发摆动、披肩摆动、胸前蝴蝶结、怀表链条、漂浮纸张。
- 表情切换：默认、开心、害羞、委屈、推理、AI 故障。
- 动作触发：挥手、鞠躬、歪头、翻书。
- 直播感面板：右侧按钮可实时驱动表情与动作。
- 预览截图：`screenshot.png`。
- Cubism 风格运行时包：`runtime/claude-scholar.model3.json`、`expressions/*.exp3.json`、`motions/*.motion3.json`、`physics.json`、`pose3.json`。
- 迁移映射：`layer-map.json` 将前端 SVG 层映射到 PSD 分组、Cubism Part 与参数名。
- QA 清单：`QA_CHECKLIST.md` 覆盖浏览器原型、Cubism 迁移与生产交付检查项。

## 说明

该原型不是 Cubism Editor 导出的 `.cmo3` 工程，而是可运行的前端 Live2D 风格演示。它已经把角色拆成可映射的视觉层与动作参数，后续可将这些层导入 PSD/Cubism 并按 `docs/live2d_claude_premium_spec.md` 的生产规格继续精修。
