# Clean Reference Direct Split Output

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
