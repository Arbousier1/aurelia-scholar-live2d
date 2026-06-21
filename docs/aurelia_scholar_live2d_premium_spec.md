# Aurelia Scholar 原创商用 Live2D 豪华级形象完整拆分规格书

> 目的：基于用户提供的橙白系学者少女参考图，制作可交付给原画拆分、Live2D 建模、动画、表情绑定与后期 QA 的「市面豪华级」拆分标准。本文不是直接生成 `.psd`/`.cmo3` 文件，而是定义可照此生产的完整分层、参数、表情、动作与验收规范。

## 1. 角色定位与视觉关键词

- **角色定位**：温和、聪明、善于陪伴的 AI 学者少女；具有“书写助手 / 思考伙伴 / 知识引路人”的气质。
- **主色调**：暖橙、奶白、浅棕、纸张米色、少量深棕与金属金点缀。
- **核心符号**：放射状星花标识、眼镜、书本、蝴蝶结、花朵、怀表、纸张、羽毛/花瓣、知识卡片。
- **风格目标**：二次元精修立绘、半透明衣料层次、长发高流动性、细碎饰品可动、表情丰富但不过度夸张。

## 2. 画布与工程标准

- **建议画布**：6000 × 9000 px，300 DPI，角色全身居中，四周预留 800 px 动作安全区。
- **PSD 色彩**：sRGB，16-bit 优先；最终交付可转 8-bit。
- **拆分精度**：头发丝、蕾丝、披肩边缘、手指、书角、眼镜腿、花瓣、挂饰链条均需独立补画遮挡区域。
- **命名规范**：`部位_方向_层级_状态`，例如 `Hair_Front_L_03`, `Eye_R_Highlight_A`, `Cape_Back_R_Inner`。
- **Live2D 版本**：Cubism 5.x；模型文件建议 `AureliaScholar_Commercial.model3.json`。
- **目标面捕**：兼容 VTube Studio、nizima LIVE、PRPRLive、OBS 透明背景推流。

## 3. PSD 顶层分组结构

```text
AureliaScholar_Commercial.psd
├─ 00_GUIDE_不可导出
├─ 01_BODY_身体
├─ 02_HEAD_头部
├─ 03_FACE_五官
├─ 04_HAIR_头发
├─ 05_OUTFIT_服装
├─ 06_ACCESSORY_饰品道具
├─ 07_HANDS_ARMS_手臂手部
├─ 08_LEGS_SHOES_腿袜鞋
├─ 09_EFFECTS_特效
├─ 10_EMOTION_PARTS_表情替换件
└─ 99_EXPORT_MASKS_遮罩与补画
```

## 4. 身体拆分

### 4.1 躯干与脖颈

- `Body_Torso_Base`：胸腹基础体，需补画被衣服遮挡区域。
- `Body_Neck_Base`：脖颈完整补画，支持头部大角度转动。
- `Body_Shoulder_L/R`：左右肩独立，肩线随呼吸与摆臂轻微变化。
- `Body_Chest_Breath_Shadow`：胸口呼吸阴影，透明度随呼吸参数变化。
- `Body_Waist_Hip`：腰胯基础，支持站姿重心切换。

### 4.2 骨骼建议

- 躯干三段：胸、腰、胯。
- 角度范围：`BodyAngleX ±18`, `BodyAngleY ±14`, `BodyAngleZ ±10`。
- 呼吸：胸口、肩部、披肩、发尾同步但幅度递减。

## 5. 头部与五官拆分

### 5.1 脸部

- `Face_Base`：完整脸型，含被头发遮挡处补画。
- `Face_Blush_Soft`：普通脸红。
- `Face_Blush_Strong`：害羞强脸红。
- `Face_Shadow_Embarrassed`：尴尬/困惑阴影。
- `Face_Angry_Mark`：轻怒符号，可开关。
- `Face_Sweat`：汗滴，支持位置轻微摆动。
- `Face_Tear_L/R`：泪滴，支持滑落动画。

### 5.2 眼睛

每只眼至少拆为：

- `Eye_L/R_White`
- `Eye_L/R_Iris_Base`
- `Eye_L/R_Iris_Shadow`
- `Eye_L/R_Pupil`
- `Eye_L/R_Highlight_Main`
- `Eye_L/R_Highlight_Sub`
- `Eye_L/R_UpperLid`
- `Eye_L/R_LowerLid`
- `Eye_L/R_Lash_Upper`
- `Eye_L/R_Lash_Lower`
- `Eye_L/R_SmileLine`
- `Eye_L/R_TearLine`
- `Eye_L/R_StarSparkle`

豪华级眼部要求：

- 支持眨眼、半眯眼、笑眼、惊讶睁大、哭眼、困倦眼、认真凝视。
- 高光需跟随眼球 XY 移动并带轻微延迟。
- 虹膜需有 2.5D 形变，避免单纯平移。

### 5.3 眉毛

- `Brow_L/R_Base`
- `Brow_L/R_Soft`
- `Brow_L/R_Angry`
- `Brow_L/R_Sad`
- `Brow_L/R_Surprise`

眉毛需支持上下、左右、旋转、弯曲形变，配合情绪参数自动混合。

### 5.4 嘴巴

- `Mouth_Inner`
- `Mouth_UpperLip`
- `Mouth_LowerLip`
- `Mouth_Teeth_Upper`
- `Mouth_Tongue`
- `Mouth_Corner_L/R`
- `Mouth_Line_Smile`
- `Mouth_Line_Frown`
- `Mouth_O_Shape`
- `Mouth_A_Shape`
- `Mouth_E_Shape`

口型需支持：A/I/U/E/O、闭嘴微笑、张口笑、惊讶 O、委屈扁嘴、鼓脸、吐舌、咬唇思考。

## 6. 头发豪华拆分

### 6.1 前发

- `Hair_Bang_Center_01~06`
- `Hair_Bang_L_01~08`
- `Hair_Bang_R_01~08`
- `Hair_Side_L_Front_01~06`
- `Hair_Side_R_Front_01~06`
- `Hair_Ahoge_01~03`

### 6.2 后发与长发束

- `Hair_Back_Base`
- `Hair_Back_Layer_Inner_L/R`
- `Hair_Back_Layer_Mid_L/R`
- `Hair_Back_Layer_Outer_L/R`
- `Hair_LongStrand_L_01~12`
- `Hair_LongStrand_R_01~12`
- `Hair_Tip_L/R_01~10`

### 6.3 物理要求

- 前发短摆：轻、快、回弹明显。
- 侧发中摆：中等延迟，随头部 Z 角明显摆动。
- 后发长摆：慢、柔、带二次回弹。
- 呆毛：独立高弹性，支持惊讶弹起与困惑左右晃。

## 7. 服装拆分

### 7.1 上身

- `Blouse_Base`
- `Blouse_Collar_L/R`
- `Blouse_Sleeve_L/R_Upper`
- `Blouse_Sleeve_L/R_Lower`
- `Blouse_Ruffle_Chest_01~08`
- `Blouse_Button_01~06`
- `Ribbon_Neck_Base`
- `Ribbon_Neck_Loop_L/R`
- `Ribbon_Neck_Tail_L/R`
- `Emblem_Chest_StarFlower`

### 7.2 披肩与外套

- `Cape_Back_Base`
- `Cape_Back_InnerPattern`
- `Cape_L_Outer`
- `Cape_R_Outer`
- `Cape_L_Inner`
- `Cape_R_Inner`
- `Cape_Edge_Ruffle_01~20`
- `Cape_TransparentVeil_L/R`
- `Cape_FloatingTip_L/R_01~06`

半透明披肩需要将纹理、阴影、高光、边缘分开，避免 Live2D 变形时糊成一片。

### 7.3 裙装

- `Skirt_Base_Back`
- `Skirt_Base_Front`
- `Skirt_Layer_Lace_01~16`
- `Skirt_Frill_01~24`
- `Skirt_Shadow_Inner`
- `Skirt_FlowerGarter`
- `Skirt_Ribbon_Tail_L/R`

裙摆建议至少 5 条物理链，左右不对称，随身体重心与腿部动作产生层次摆动。

## 8. 手臂、手部与道具

### 8.1 左右手臂

- `Arm_L_Upper`, `Arm_L_Fore`, `Hand_L_Base`, `Finger_L_Thumb/Index/Middle/Ring/Pinky`
- `Arm_R_Upper`, `Arm_R_Fore`, `Hand_R_Base`, `Finger_R_Thumb/Index/Middle/Ring/Pinky`
- 每根手指至少分根部与指尖两段，用于书本抓握、思考托腮、挥手动作。

### 8.2 书本

- `Book_Cover_Front`
- `Book_Cover_Back`
- `Book_Spine`
- `Book_Page_Block`
- `Book_Page_Flip_01~05`
- `Book_RibbonBookmark`
- `Book_Corner_Highlight`
- `Book_Clasp_Metal`

书本需支持：轻微开合、页面抖动、翻页动作、抱书随胸口呼吸微动。

### 8.3 怀表与链条

- `PocketWatch_Body`
- `PocketWatch_Glass`
- `PocketWatch_Hand_Hour`
- `PocketWatch_Hand_Minute`
- `PocketWatch_Chain_Link_01~16`
- `PocketWatch_Tassel_01~05`

链条建议每 2~3 节合并为一个网格组，既保精度也控制性能。

## 9. 腿、袜、鞋

- `Leg_L/R_Base`
- `Knee_L/R_Shadow`
- `Sock_L/R_Base`
- `Sock_L/R_Ruffle_01~08`
- `Sock_L/R_Emblem`
- `Shoe_L/R_Base`
- `Shoe_L/R_Strap`
- `Shoe_L/R_Buckle`
- `Shoe_L/R_Highlight`
- `Shoe_L/R_Bow`

腿部需补画膝盖、袜口与鞋跟被遮挡处，以支持站姿微摆、踮脚、轻跳、走路循环。

## 10. 饰品与环境小件

角色本体可动饰品：

- 眼镜：镜框、镜片、镜腿、反光、滑落状态。
- 头花：花瓣 8~12 片独立，花心独立。
- 蝴蝶结：左右结环、飘带、阴影、高光。
- 胸前星花：花瓣/中心/发光层。
- 纸张与花瓣：若作为可开关特效，需要独立粒子序列。

可选直播场景道具：

- 书堆、茶杯、花瓶、信纸、笔、背景罗盘星图。
- 建议做成独立前景/背景 PNG 或 Live2D scene item，不并入主模型以降低主模型复杂度。

## 11. 表情系统

### 11.1 基础表情

1. `Exp_Default`：温柔默认。
2. `Exp_Smile`：微笑陪伴。
3. `Exp_BigSmile`：开心大笑。
4. `Exp_Thinking`：托腮沉思，眼睛轻眯。
5. `Exp_Question`：疑问，问号特效。
6. `Exp_Surprised`：惊讶，眼睛放大，呆毛弹起。
7. `Exp_Shocked`：震惊，瞳孔缩小，脸部阴影。
8. `Exp_Shy`：害羞，强脸红，视线偏移。
9. `Exp_Embarrassed`：尴尬汗滴。
10. `Exp_Sad`：委屈，泪光。
11. `Exp_Cry`：流泪。
12. `Exp_AngrySoft`：轻怒但可爱。
13. `Exp_Serious`：认真讲解。
14. `Exp_Sleepy`：困倦半眯。
15. `Exp_Proud`：自信得意。
16. `Exp_GlitchAI`：AI 轻微故障，眼部星光/像素特效。

### 11.2 豪华附加表情

- 眼镜反光推理模式。
- 星星眼赞赏模式。
- 黑脸沉默模式。
- 豆豆眼卖萌模式。
- 闭眼点头模式。
- 书本遮脸害羞模式。
- 花瓣环绕感谢模式。
- 生气鼓脸模式。

## 12. 动作系统

### 12.1 常驻待机

- `Idle_Breathe_Soft`：基础呼吸。
- `Idle_HairSway`：头发随机微摆。
- `Idle_EyeBlinkRandom`：自然眨眼。
- `Idle_ReadBook`：低头看书，轻翻页。
- `Idle_ThinkWithPen`：思考小动作。
- `Idle_AdjustGlasses`：推眼镜。

### 12.2 互动动作

- `Motion_GreetWave`：挥手问候。
- `Motion_BowThanks`：轻鞠躬感谢。
- `Motion_PointExplain`：指向侧边讲解。
- `Motion_HugBook`：抱紧书本。
- `Motion_PageFlip`：翻书展示。
- `Motion_QuestionTilt`：歪头疑问。
- `Motion_Cheer`：开心鼓励。
- `Motion_SurpriseJump`：惊讶轻跳。
- `Motion_SleepyNod`：困倦点头。
- `Motion_GlitchRecover`：AI 故障后恢复。

### 12.3 高级动作

- 半身大幅转向：适配直播时“看向弹幕 / 看向侧边内容”。
- 书本打开投影：胸前星花轻亮，浮现纸张/符号特效。
- 罗盘星图召唤：背景星图淡入，头发和披肩受“魔法风”影响。
- 走路循环：腿部小幅交替，裙摆与披肩跟随。

## 13. Live2D 参数建议

| 参数 | 范围 | 用途 |
| --- | ---: | --- |
| `ParamAngleX` | -30 ~ 30 | 头部左右转 |
| `ParamAngleY` | -30 ~ 30 | 头部上下转 |
| `ParamAngleZ` | -30 ~ 30 | 头部倾斜 |
| `ParamBodyAngleX` | -15 ~ 15 | 身体左右 |
| `ParamBodyAngleY` | -10 ~ 10 | 身体上下 |
| `ParamBodyAngleZ` | -10 ~ 10 | 身体倾斜 |
| `ParamEyeLOpen/ROpen` | 0 ~ 1.8 | 眨眼与睁大 |
| `ParamEyeBallX/Y` | -1 ~ 1 | 眼球追踪 |
| `ParamBrowLY/RY` | -1 ~ 1 | 眉毛上下 |
| `ParamBrowLForm/RForm` | -1 ~ 1 | 眉形变化 |
| `ParamMouthOpenY` | 0 ~ 1 | 嘴巴开合 |
| `ParamMouthForm` | -1 ~ 1 | 笑/哭嘴 |
| `ParamBreath` | 0 ~ 1 | 呼吸 |
| `ParamHairFront/Side/Back` | -1 ~ 1 | 头发物理输入 |
| `ParamCapeSwing` | -1 ~ 1 | 披肩摆动 |
| `ParamSkirtSwing` | -1 ~ 1 | 裙摆摆动 |
| `ParamBookOpen` | 0 ~ 1 | 书本开合 |
| `ParamGlassesReflect` | 0 ~ 1 | 眼镜反光 |
| `ParamStarGlow` | 0 ~ 1 | 星花发光 |
| `ParamAI_Glitch` | 0 ~ 1 | AI 故障特效 |

## 14. 物理分组

- `Physics_Hair_Ahoge`：呆毛，高弹性。
- `Physics_Hair_Bangs`：刘海，低幅快回弹。
- `Physics_Hair_Side_L/R`：侧发，中幅中回弹。
- `Physics_Hair_Back_Long`：后长发，大幅慢回弹。
- `Physics_Ribbon_Neck`：胸前蝴蝶结，轻飘。
- `Physics_Cape_L/R`：披肩，延迟大。
- `Physics_Skirt_Frill`：裙摆蕾丝，碎动。
- `Physics_Watch_Chain`：怀表链条，重力摆。
- `Physics_Bookmark`：书签，轻摆。
- `Physics_FlowerPetals`：花瓣粒子，循环漂浮。

## 15. 变形器层级建议

```text
Root
├─ Deformer_Body_All
│  ├─ Deformer_Torso
│  ├─ Deformer_Arm_L / Deformer_Arm_R
│  └─ Deformer_Leg_L / Deformer_Leg_R
├─ Deformer_Head_All
│  ├─ Deformer_Face_XYZ
│  ├─ Deformer_Eyes
│  ├─ Deformer_Mouth
│  └─ Deformer_Glasses
├─ Deformer_Hair_All
│  ├─ Deformer_Hair_Front
│  ├─ Deformer_Hair_Side_L/R
│  └─ Deformer_Hair_Back
├─ Deformer_Outfit_All
│  ├─ Deformer_Cape
│  ├─ Deformer_Skirt
│  └─ Deformer_Ribbons
└─ Deformer_Props_All
   ├─ Deformer_Book
   ├─ Deformer_Watch
   └─ Deformer_Effects
```

## 16. 网格与性能标准

- 脸部、眼睛、嘴巴：高密度网格，保证近景面捕质量。
- 长发、披肩、裙摆：中高密度网格，边缘多点，内部适中。
- 小饰品：低中密度，避免无意义高点数。
- 目标性能：常规直播 PC 下 60 FPS；移动端降级版 30 FPS。
- 建议导出两套：`Premium_Full` 与 `Streaming_Optimized`。

## 17. 交付物清单

- `AureliaScholar_Commercial.psd`：完整分层源文件。
- `AureliaScholar_Commercial.cmo3`：Cubism 工程。
- `AureliaScholar_Commercial.model3.json`：运行时模型。
- `motions/*.motion3.json`：动作文件。
- `expressions/*.exp3.json`：表情文件。
- `physics.json`：物理配置。
- `pose3.json`：姿势配置。
- `textures/`：纹理图集，推荐 4096 或 8192。
- `README_Live2D_CN.md`：使用说明。
- `qa_checklist.xlsx`：测试清单。

## 18. 验收清单

- 头部 X/Y/Z 大角度转动时脸型不崩。
- 眼睛有真实球面感，高光与瞳孔运动自然。
- 嘴型支持自然语音捕捉，不出现破面。
- 长发、披肩、裙摆具有不同延迟和重量感。
- 眼镜反光、书本翻页、怀表链条、头花、蝴蝶结均可独立动作。
- 至少 16 套基础表情与 10 套动作可一键触发。
- 所有遮挡区域补画完整，无露白、断边、透明缝。
- OBS 透明背景下边缘干净，无白边。
- VTube Studio 参数识别正常。

## 19. 制作备注

参考图中含有 第三方品牌 字样与品牌视觉元素。若该模型用于公开商业直播、周边或二创发布，建议将文字标识替换为原创名称与原创符号，保留“暖橙学者 AI 少女”的气质，但避免直接使用第三方品牌资产。
