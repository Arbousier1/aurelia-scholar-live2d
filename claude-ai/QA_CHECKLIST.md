# Claude AI 拟人角色 Live2D 拆分 QA 清单

## 一、参考图与版权检查

- [ ] 已获得高清原图或授权重绘稿（≥2000px 短边）。
- [ ] 左侧品牌 Logo、角色信息卡、底部邮戳、竖排文案等第三方元素已替换为原创设计。
- [ ] 角色命名、世界观、配色已确认可商用。

## 二、PSD 分层检查

- [ ] 脸基底层完整补画（额头、耳朵、下颌、被刘海/眼镜遮挡处）。
- [ ] 眼镜框、镜片、反光分三层；镜片透明度与反光随转头视差偏移。
- [ ] 眼睛拆分：眼白、虹膜、瞳孔、大高光、小高光、上下眼睑、睫毛。
- [ ] 嘴型包含 A/I/U/E/O、微笑、抿嘴、惊讶、露齿、伸舌等变体。
- [ ] 头发按前/中/后三层拆分；双麻花辫按链节+散发束拆分；每束含根部/中段/末梢。
- [ ] 发饰花朵（左右）花瓣、花心、阴影、高光、黑色发带结/尾独立。
- [ ] 米白衬衫、荷叶边领口、泡泡袖、袖口蕾丝、胸前黑色领结、星芒标识分层。
- [ ] 披肩/外搭分外层/内层/边缘/纹理/阴影/高光。
- [ ] 腰带/蝴蝶结、多层裙摆、蕾丝边、荷叶边独立。
- [ ] 左手托脸、右手抱书，双臂与手指至少两段关节。
- [ ] 书堆（顶/中/底）+ 封面/书脊/书页/书签可动。
- [ ] 腿部左右独立，补画裙底遮挡；袜套褶皱、腿花饰、鞋扣鞋带独立。
- [ ] 怀表本体、表盘、指针、表链链节、流苏独立物理。
- [ ] 背景道具（书堆、杯子、花瓶、纸张、羽毛笔）作为 scene item，不并入主模型。

## 三、Cubism 参数与绑定

- [ ] 头部 XYZ：`ParamAngleX/Y/Z` 绑定头、脸、前发、侧发、后发、眼镜视差。
- [ ] 身体 XYZ：`ParamBodyAngleX/Y/Z` 绑定躯干、披肩、裙摆、腰带、书堆。
- [ ] 眼睛开闭与眼珠：`ParamEyeLOpen/ROpen`、`ParamEyeBallX/Y`。
- [ ] 嘴型：`ParamMouthOpenY`、`ParamMouthForm`。
- [ ] 呼吸：`ParamBreath` 绑定胸部、上衣、披肩轻微起伏。
- [ ] 头发物理：`ParamHairFront/Side/Back` 分前中后摆动。
- [ ] 服装物理：`ParamCapeSwing`、`ParamSkirtSwing`、`ParamSashSwing`、`ParamRibbonSwing`。
- [ ] 手臂与手：`ParamArmL/R`、`ParamHandPose`。
- [ ] 书本：`ParamBookTilt`、`ParamPageFlip`。
- [ ] 腿部：`ParamLegPose`、`ParamShoeStep`。
- [ ] 怀表：`ParamWatchSwing`。
- [ ] 特效：`ParamStarGlow`、`ParamPetalLoop`、`ParamQuestion`、`ParamAI_Glitch`。

## 四、物理与碰撞

- [ ] 双麻花辫设置多段物理链，根部到末梢刚度递减。
- [ ] 披肩/裙摆设置 6+ 条摆动链，与腿部碰撞避免穿模。
- [ ] 表链设置 20 节物理链，与裙摆轻微碰撞。
- [ ] 呆毛高弹性，限制最大摆动角度避免夸张。
- [ ] 发带尾、腰带尾、书签加入微风/摆动物理。

## 五、表情与动作

- [ ] 表情：default、smile、shy、cry、serious、glitch、thinking、surprise、sleepy 等 ≥16 个。
- [ ] 动作：idle_breathe、wave、bow、tilt、book、touch_face、nod、head_tilt 等 ≥10 个。
- [ ] 每个动作含 FadeIn/FadeOut，循环动作首尾衔接自然。

## 六、运行时与交付

- [ ] `runtime/claude-ai.model3.json` 指向真实 `.moc3` 与纹理图集。
- [ ] `physics.json`、`pose3.json` 与表情/动作文件完整无语法错误。
- [ ] `layer-map.json` 已更新，前端层级与 PSD 分组、Cubism Part、参数名一一对应。
- [ ] 浏览器预览可正常加载，无 404/跨域/CORS 错误。
- [ ] 输出交付包：PSD 源文件、.cmo3 工程、runtime 文件夹、拆分表、QA 清单。
