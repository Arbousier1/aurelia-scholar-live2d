# Claude Scholar Live2D

这是一个只包含 Live2D 交付物的私有仓库版本，已剔除原 MineChess/Minecraft 插件代码与构建产物。

## 内容

- `docs/live2d_claude_premium_spec.md`：豪华级 Live2D PSD/Cubism 拆分与验收规格。
- `live2d/claude-scholar-prototype/`：可运行的浏览器 Live2D 风格原型。
- `live2d/claude-scholar-prototype/runtime/`：Cubism 风格 model3、表情、动作、物理与姿势配置示例。
- `live2d/claude-scholar-prototype/layer-map.json`：浏览器原型层到 PSD/Cubism 层的迁移映射。
- `live2d/claude-scholar-prototype/QA_CHECKLIST.md`：原型、迁移与生产交付 QA 清单。

## 运行原型

```bash
python3 -m http.server 8080 --directory live2d/claude-scholar-prototype
```

然后打开：

```text
http://localhost:8080
```
