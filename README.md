# Aurelia Scholar Live2D

这是一个只包含 Live2D 交付物的公开预览仓库，已剔除原 MineChess/Minecraft 插件代码与构建产物。

## 内容

- `docs/aurelia_scholar_live2d_premium_spec.md`：豪华级 Live2D PSD/Cubism 拆分与验收规格。
- `live2d/aurelia-scholar-live2d/`：可运行的浏览器 Live2D 专业拆解工作台。
- `live2d/aurelia-scholar-live2d/runtime/`：Cubism 风格 model3、表情、动作、物理与姿势配置示例。
- `live2d/aurelia-scholar-live2d/layer-map.json`：浏览器原型层到 PSD/Cubism 层的迁移映射。
- `live2d/aurelia-scholar-live2d/QA_CHECKLIST.md`：原型、迁移与生产交付 QA 清单。
- `live2d/aurelia-scholar-live2d/split/`：基于参考图的第一阶段坐标拆分、完整 466 行图层清单、参数绑定和最终切割顺序。
- `live2d/aurelia-scholar-live2d/photoshop/aurelia_live2d_split_bootstrap.jsx`：用于 Photoshop 的初始拆层脚本，按干净图坐标创建组和区域图层。
- `live2d/aurelia-scholar-live2d/split/output-clean-reference/`：已经从干净角色图直接生成的 PSD、透明 PNG 拆层、alpha 蒙版、预览 contact sheet 和打包 zip。

## 在线预览

GitHub Pages 已配置为从 `gh-pages` 分支发布，在线预览地址：

```text
https://arbousier1.github.io/aurelia-scholar-live2d/
```

## 运行原型

```bash
python3 -m http.server 8080 --directory live2d/aurelia-scholar-live2d
```

然后打开：

```text
http://localhost:8080
```
