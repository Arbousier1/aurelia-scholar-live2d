# Claude AI 拟人角色 Live2D 画师拆分执行说明

> 本文件是 `source-coordinate-map.json` 的落地执行版。画师按此说明在 Photoshop / Clip Studio / Procreate 中完成补画、分层与命名，再导入 Live2D Cubism。

---

## 1. 原图与版权

- 参考图：`reference.png`（需用户/项目方提供高清原图并放入本目录）
- 当前坐标基于 **850×1106** 的预览图标注；高清原图到位后，坐标需等比放大。
- **商用前必须替换/移除的第三方元素**：
  1. 左上角 `Claude` 品牌标识
  2. 左侧角色信息卡（NAME/SPECIES/AFFILIATION 等）
  3. 右上角 `Claude AI` 文字
  4. 左下角 `ANTHROPIC CLAUDE 2023` 邮戳
  5. 左侧竖排宣传文字
  6. 右侧竖排宣传文字
- 建议：保留角色气质，把这些区域 redraw 成原创世界观标签、角色名片、印章或装饰花纹。

---

## 2. 通用分层原则

| 原则 | 说明 |
|------|------|
| 前遮后 | 所有被遮挡部分都要补画完整，方便变形时被别的图层揭开。 |
| 闭合边缘 | 每个部件边缘闭合，没有锯齿或透明缝隙。 |
| 命名规范 | `组_部位_序号_变体`，例如 `HAIR_Bangs_01`、`FACE_Eye_L_White`。 |
| 分辨率一致 | 所有图层画布与参考图同尺寸，部件用透明像素占位，方便 Cubism 自动对齐。 |
| 保留线稿/阴影/高光层 | 复杂部位拆成 Base / Shadow / Highlight / Line 四层，方便后续做光影变化。 |

---

## 3. 分组执行细节

### 00 REFERENCE
- 放一张完整角色立绘作为最底层 `00_REF/Body_Base`。
- 再建 `00_REF/Overlay_Cleanup` 用红色半透明块标出 6 处商用清理区，给审核用。

### 01 BODY（身体基底层）
- `Body_Torso`：脖子、肩膀、锁骨、躯干，补画被衬衫/披风遮挡部分。
- `Body_Neck`：单独一层，方便头部左右转动时与身体接缝自然。
- `Body_Chest`：胸口皮肤，领结下方小三角区域要补完整。

### 02 HEAD / Face
- `Head_Base`：完整脸型、耳朵、下巴，不戴眼镜/刘海。
- `Ear_L` / `Ear_R`：左右耳，注意被头发遮挡的内侧要补画。
- `Face_Blush`：可选腮红层，Blend Mode = Multiply / Overlay。

### 03 FACE / Eyes & Glasses
每个眼睛至少拆成：
- `Eye_L_White` 眼白
- `Eye_L_Iris` 虹膜底色
- `Eye_L_Pupil` 瞳孔
- `Eye_L_Highlight` 高光（2~3 个）
- `Eye_L_Lash_Upper` 上睫毛
- `Eye_L_Lash_Lower` 下睫毛
- `Eye_L_Eyelid_Upper` 上眼睑（可变形做眨眼/半闭眼）
- `Eye_L_Eyelid_Lower` 下眼睑
- `Eye_L_Glasses_Lens` 镜片（半透明，保留反光）
- `Eye_L_Glasses_Frame` 镜框

右眼同名 `_R_`。

眼镜：
- `Glasses_Frame_Bridge` 鼻梁架
- `Glasses_Frame_Temple_L/R` 镜腿
- `Glasses_Lens_L/R` 镜片反光单独一层

### 04 HAIR
按物理链拆分：
- `Hair_Back_Base`：后发基底层，在身体后方。
- `Hair_Back_Strand_01~12`：后发束，根部在身体背部，梢部自由摆动。
- `Hair_Side_L_01~08`：左侧编发 + 散发，每束根部到梢部做 3~5 段变形点。
- `Hair_Side_R_01~08`：右侧编发 + 大卷，卷发的重叠层次要分前/中/后。
- `Hair_Bangs_01~06`：中刘海、左刘海、右刘海，每缕独立，根部固定在前额。
- `Hair_Ahoge_01~04`：顶部呆毛，最灵动的物理链。

所有发束都要画：
- 正面色 Base
- 内侧阴影 Shadow
- 受光边缘 Highlight
- 发梢透明渐变

### 05 OUTFIT
- `Blouse_Base`：米白衬衫主体。
- `Blouse_Sleeve_L/R`：泡泡袖，分上臂/手肘/袖口。
- `Blouse_Collar`：衣领 + 胸前荷叶边。
- `Blouse_Cuff_Lace_L/R`：袖口蕾丝。
- `Cape_L_01~05`：左侧披肩分多层，外层/内衬/边缘/纹理/高光。
- `Cape_R_01~05`：右侧披肩分多层。
- `NeckBow_Base`：黑色领结主体。
- `NeckBow_Star`：胸前星芒标识（**必须替换为原创标识**）。
- `WaistSash_Knot`：腰带结。
- `WaistSash_Tail_L/R`：腰带左右尾，做飘动物理。
- `Skirt_Front`：前裙片。
- `Skirt_Back`：后裙片。
- `Skirt_Lace_01~04`：多层荷叶边/蕾丝边。
- `Skirt_Inner`：裙内衬阴影层。

### 06 ACCESSORY
- `HeadFlower_L_Petal_01~06` / `Center` / `Ribbon`：左上方花朵。
- `HeadFlower_R_Petal_01~06` / `Center` / `Ribbon_Black`：右侧花朵 + 黑色发带。
- `Book_Top` / `Book_Middle` / `Book_Bottom`：三本书。
- `Book_Cover_L/R`：封面与书脊（**替换为原创书名/图案**）。
- `Book_Pages`：书页侧面，可做翻阅微动。
- `Book_Bookmark`：书签。
- `PocketWatch_Body` / `Glass` / `Dial` / `Hand_Hour` / `Hand_Minute` / `Chain_01~N`：怀表本体 + 链节物理。

### 07 HANDS & ARMS
- `Arm_L_Upper` / `Arm_L_Lower` / `Hand_L` / `Fingers_L_01~05`：左手托腮姿势，每根手指独立。
- `Arm_R_Upper` / `Arm_R_Lower` / `Hand_R` / `Fingers_R_01~05`：右手抱书，手指与书边缘分离。

### 08 LEGS & SHOES
- `Leg_L_Thigh` / `Leg_L_Calf` / `Leg_L_Knee`
- `Leg_R_Thigh` / `Leg_R_Calf` / `Leg_R_Knee`
- `Sock_L` / `Sock_R`：袜套褶皱、袜口荷叶边。
- `Shoe_L_Upper` / `Shoe_L_Sole` / `Shoe_L_Buckle` / `Shoe_L_Bow`
- `Shoe_R_Upper` / `Shoe_R_Sole` / `Shoe_R_Buckle` / `Shoe_R_Bow`
- `GarterFlower_L`：左腿大腿花饰。

### 09 EFFECTS（可选）
- `Particle_Petal_01~N`：飘散花瓣，循环粒子。
- `Particle_Paper_01~N`：飘散纸张。

### 10 SCENE（不并入主模型）
- `Scene_Books_Stack`
- `Scene_Cup`
- `Scene_Vase_Flowers`
- `Scene_Papers`
- `Scene_Quill`
- `Scene_Asterisk_Backdrop`

---

## 4. 表情目标（至少 16 个）

| 编号 | 表情 | 关键变化 |
|------|------|----------|
| E01 | default | 默认温和微笑 |
| E02 | smile | 嘴角上扬，眼微眯 |
| E03 | gentle | 柔和，眼睑放松 |
| E04 | serious | 嘴角放平，眼神聚焦 |
| E05 | thinking | 单眉微蹙，眼镜反光 |
| E06 | surprised | 眼睁大，嘴张开 |
| E07 | shy | 脸红，视线偏移 |
| E08 | happy | 闭眼笑 |
| E09 | sad | 眼尾下垂 |
| E10 | worried | 皱眉 |
| E11 | confident | 嘴角单侧上扬 |
| E12 | curious | 头微歪，眼睁大 |
| E13 | sleepy | 半闭眼 |
| E14 | determined | 眼神锐利 |
| E15 | glitch | 瞳孔/眼镜高光异常（适合 AI 主题） |
| E16 | sparkle | 眼镜高光星形 |

---

## 5. 动作目标（至少 10 个）

| 编号 | 动作 | 说明 |
|------|------|------|
| M01 | idle_breathe | 呼吸 + 披肩/发梢微摆 |
| M02 | touch_face | 左手托腮/放下 |
| M03 | adjust_glasses | 推眼镜 |
| M04 | book_open | 书本翻开 |
| M05 | wave | 单手挥手 |
| M06 | bow | 鞠躬 |
| M07 | tilt | 歪头 |
| M08 | hair_sway | 头发大幅摆动 |
| M09 | walk_loop | 原地步行循环 |
| M10 | emote_sparkle | 眼镜闪光 + 呆毛弹跳 |

---

## 6. 命名速查表

```
00_REF/Body_Base
01_BODY/Body_Torso, Body_Neck, Body_Chest
02_HEAD/Head_Base, Ear_L, Ear_R, Face_Blush
03_FACE/Eye_L_White, Eye_L_Iris, Eye_L_Pupil, Eye_L_Highlight, Eye_L_Lash_Upper, ...
03_FACE/Eye_R_...
03_FACE/Glasses_Frame_Bridge, Glasses_Lens_L, ...
03_FACE/Mouth_Default, Mouth_A, Mouth_I, Mouth_U, Mouth_E, Mouth_O, Mouth_Smile, ...
04_HAIR/Hair_Back_Base, Hair_Back_Strand_01~12, Hair_Side_L_01~08, Hair_Side_R_01~08, Hair_Bangs_01~06, Hair_Ahoge_01~04
05_OUTFIT/Blouse_..., Cape_L_..., Cape_R_..., NeckBow_..., WaistSash_..., Skirt_...
06_ACC/HeadFlower_..., Book_..., PocketWatch_...
07_HANDS/Arm_L_..., Hand_L, Fingers_L_01~05, ...
08_LEGS/Leg_..., Sock_..., Shoe_..., GarterFlower_L
09_FX/Particle_...
10_SCENE/Scene_...
```

---

## 7. 交付 checklist

- [ ] PSD 画布尺寸与参考图一致，300 dpi 以上
- [ ] 所有 S 级部位补画完整，无缺口
- [ ] 商用清理区已替换为原创元素
- [ ] 图层命名符合本规范
- [ ] 已导出 `claude-ai_textures.png`（2K/4K 纹理图集，带透明通道）
- [ ] 已提交本目录下的 `QA_CHECKLIST.md` 复核

---

## 8. 自动化脚本

- `scripts/generate_part_crops.py`：读取 `reference.png` + `source-coordinate-map.json`，自动生成 `parts/*.png` 粗切与 `parts/layered-preview.html` 叠加预览。
- 运行方式：`python3 scripts/generate_part_crops.py`
