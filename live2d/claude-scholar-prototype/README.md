# Claude Scholar Live2D Prototype

这是一个浏览器 Live2D 专业拆解工作台：它不再用简化 SVG 小人冒充成品，而是按参考图列出真实 PSD/Cubism 拆分组、部件、参数、物理与生产注意事项。

## 使用方式

```bash
python3 -m http.server 8080 --directory live2d/claude-scholar-prototype
```

然后打开：

```text
http://localhost:8080
```

建议通过本地 HTTP 服务打开 `index.html`，因为页面会加载 `decomposition-plan.json`。

## 已实现内容

- 参考图生产拆解：头脸、头发、上衣、披肩裙摆、手臂书本、腿鞋怀表、特效背景。
- 每组列出建议 PSD 图层、Cubism 参数、物理/绑定注意事项。
- 明确提示单张压缩图不能自动变成合格 PSD，需要高清原图或重绘补画。
- 预览页改为拆解工作台，避免展示低质量简化角色绘制。
- 预览截图：`screenshot.png`。
- Cubism 风格运行时包：`runtime/claude-scholar.model3.json`、`expressions/*.exp3.json`、`motions/*.motion3.json`、`physics.json`、`pose3.json`。
- 迁移映射：`layer-map.json` 将前端 SVG 层映射到 PSD 分组、Cubism Part 与参数名。
- QA 清单：`QA_CHECKLIST.md` 覆盖浏览器原型、Cubism 迁移与生产交付检查项。

## 说明

该工作台不是 Cubism Editor 导出的 `.cmo3` 工程，也不是最终绘制成品；它是给画师和建模师使用的拆解与重绘/建模工单。后续应使用高清原图或重绘稿按 `docs/live2d_claude_premium_spec.md` 继续制作 PSD 与 Cubism 工程。
