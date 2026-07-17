# admin-management-scaffold · 定制设计令牌文档（Design Token Spec）

> **交付形态**：本文档为 Phase 3 原型构建师的直接输入，亦作为 Phase 4 质量审查、Phase 5 导出的验收依据。
> **视觉方向**：现代克制 · 专业稳重（Modern Minimal · Restrained）—— 锚定 Ant Design 6 默认设计语言，微缩圆角、收敛主色至沉稳蓝/靛、强化中性灰阶层级，用留白与分隔线建立秩序感。
> **双形态**：① AntD `ConfigProvider theme={{ token, components }}` 配置；② 配套 CSS 变量（`:root` + `[data-theme="dark"]`），二者语义一一映射。

---

## 0. 文档元信息与重要提示

### 0.1 已确认决策（来自 Phase 1 / 用户拍板）
| 项 | 决策 |
|----|------|
| 调性方向 | 现代克制·专业稳重（候选 A）：克制、清晰、可信、低噪 |
| 主色 | 沉稳蓝/靛：收敛 AntD 默认蓝（#1677ff 系列）至更低饱和度/明度 |
| 深色变体 | 含深色可切换（浅色默认 + `theme.darkAlgorithm` 派生） |
| 字体 | 沿用系统字体栈 / AntD 默认体系，不引入非标准 Web 字体 |
| 交付约束 | AntD 6 token 体系为唯一权威；双形态；组件覆盖一致；不破坏默认可用性（WCAG AA）；浅色默认+深色可切换；零侵入（仅 ConfigProvider 包裹） |

### 0.2 ⚠️ 版本差异提示（须团队确认）
- 需求摘要写明 **"Ant Design 6"**，但 `frontend/package.json` 实际锁定 **`antd: ^5.29.3`**。
- 本文档所使用的一切 token 名称（`theme.token` 全局 token 与 `theme.components` 组件 token）在 **AntD v5.29 与 v6 中语义一致、可通用**，因此无论最终锁定哪个版本均可直接消费。
- **建议**：团队确认目标版本。若最终升级到 AntD 6，请对 `components` 子项 token 名做一次 diff（重点关注 `Card` / `Modal` / `Statistic` 的个别命名），全局 token 不受影响。

### 0.3 与现有代码的关系
- `frontend/src/main.tsx` 已启用 `ConfigProvider` 且 `cssVar: true`，当前仅设了 `colorPrimary: '#1677ff'`、`borderRadius: 6`。**本文档即用完整 `lightTheme` / `darkTheme` 替换该处 theme 对象**（见 §8.4）。
- `cssVar: true` 已开启 → AntD 会自动注入 `--ant-color-primary` / `--ant-color-bg-container` 等变量。本文档 §8.2 的 CSS 变量是**项目自有的语义镜像集**（前缀 `--color-*`），用于 AntD 之外的自定义表面（如 `MainLayout.css` 的触发器悬停色、图表、空状态）。两者可互相映射（见 §8.3）。
- `frontend/src/layouts/MainLayout.css` 当前硬编码 `#1677ff`（`hover` 色）与 `rgba(5,5,5,0.06)`（边框），应改为引用变量（见 §8.4）。

---

## 1. Visual Theme（视觉主题）

**Philosophy**: 以 Ant Design 默认语言为锚，做"减法"——收敛色彩、微缩圆角、放大留白与分隔，让后台管理员在信息密集的仪表盘里获得稳定、可信、低噪的视觉秩序。

**Direction**: `minimal, utilitarian, data-dense, low-noise`

**Personality**: `calm, precise, professional, trustworthy`

**Reference**: Ant Design 6 默认设计语言（中性灰阶 + 单主色强调）；视觉学派对齐 skill 的 **Modern Minimal（现代极简）**，但在 token 语义上严格受限于 AntD 体系。

**克制三原则**：
1. **单主色**：仅 `colorPrimary` 一个强调色，语义色（成功/警告/错误）保持 AntD 默认以保状态可识别，不另造彩色。
2. **微缩圆角**：全局圆角从 AntD 默认 6 收敛到 4，卡片/弹窗 6，标签/小控件 2。
3. **灰阶层级 + 分隔线**：用 `colorSplit` / `colorBorderSecondary` 与留白建立秩序，而非阴影与彩色。

---

## 2. 色彩令牌（浅色主题）【需求 A】

### 2.1 主色与派生

**主色收敛推导**：AntD 默认主色 `#1677ff` = HSL(215°, 84%, 54%)，为高饱和、偏高明度的天蓝。为得到"沉稳蓝/靛"，做三步收敛：
- 色相 215° → **227°**（向蓝紫偏移，带靛味，仍属"蓝"品牌延续）；
- 饱和度 84% → **64%**（去刺眼感）；
- 明度 54% → **48%**（更沉稳、专业）。

得到 **`colorPrimary = #2C4EC9`** = HSL(227°, 64%, 48%)。

| Token | HEX | 派生规则 | 用途 |
|-------|-----|---------|------|
| `colorPrimary` | `#2C4EC9` | 收敛 AntD 默认蓝所得（HSL 227/64/48） | 主按钮、选中态、激活态、关键强调 |
| `colorPrimaryHover` | `#3E62D6` | 主色提亮 ~8%（L +8%） | 悬停态 |
| `colorPrimaryActive` | `#2440A8` | 主色压暗 ~10%（L -10%） | 按压/激活态 |
| `colorPrimaryBg` | `#ECEFFB` | 主色 8% 透明度叠白（≈ rgba(44,78,201,.08)） | 浅底强调（标签/选中背景基底） |
| `colorPrimaryBgHover` | `#E2E7F8` | 主色 12% 叠白 | 浅底悬停 |
| `colorPrimaryBorder` | `#A9B8EC` | 主色 45% 叠白 | 描边态 |

### 2.2 语义色（保持 AntD 默认，状态可识别）

> 设计取向：语义色**刻意保持 AntD 默认**，避免引入额外视觉噪音；其对比度风险见 §2.6（语义色不作白字实底，而用于图标/状态/浅底标签）。`colorInfo` 默认等于主色（与 AntD 行为一致），进一步减少"第二种蓝"。

| Token | HEX | 用途 |
|-------|-----|------|
| `colorInfo` | `#2C4EC9` | 信息态 = 主色（AntD 默认 info==primary） |
| `colorSuccess` | `#52C41A` | 成功/通过 |
| `colorWarning` | `#FAAD14` | 警告/注意 |
| `colorError` | `#FF4D4F` | 错误/危险/删除 |
| `colorSuccessBg` | `#F6FFED` | 成功浅底（配深绿字） |
| `colorWarningBg` | `#FFFBE6` | 警告浅底 |
| `colorErrorBg` | `#FFF2F0` | 错误浅底 |

### 2.3 中性文本阶

| Token | 取值 | 用途 |
|-------|------|------|
| `colorText` | `rgba(0, 0, 0, 0.88)` | 主文本/标题（≈ #1F1F1F） |
| `colorTextSecondary` | `rgba(0, 0, 0, 0.65)` | 次要文本/说明（≈ #595959） |
| `colorTextTertiary` | `rgba(0, 0, 0, 0.45)` | 三级文本/辅助（≈ #8C8C8C，仅大文本/装饰/禁用） |
| `colorTextQuaternary` | `rgba(0, 0, 0, 0.25)` | 四级/禁用/占位（≈ #BFBFBF，仅装饰/禁用） |

### 2.4 背景阶

| Token | HEX | 用途 |
|-------|-----|------|
| `colorBgLayout` | `#F5F5F5` | 页面/布局底色（content 区） |
| `colorBgContainer` | `#FFFFFF` | 卡片/容器/弹窗底色 |
| `colorBgElevated` | `#FFFFFF` | 浮层（Dropdown/Popover）底色（靠阴影区分层级） |
| `colorBgSpotlight` | `rgba(0, 0, 0, 0.85)` | Tooltip 等聚光底色 |

### 2.5 边框 / 分割 / 链接

| Token | 取值 | 用途 |
|-------|------|------|
| `colorBorder` | `#D9D9D9` | 主边框（输入框/卡片外框） |
| `colorBorderSecondary` | `#F0F0F0` | 次级边框（内部细分隔） |
| `colorSplit` | `rgba(5, 5, 5, 0.06)` | 分割线/列表分隔（≈ #F0F0F0 但更克制） |
| `colorLink` | `#2C4EC9` | 链接（= 主色） |
| `colorLinkHover` | `#3E62D6` | 链接悬停 |
| `colorLinkActive` | `#2440A8` | 链接激活 |

### 2.6 WCAG AA 对比度核验

> 基准背景：白 `#FFFFFF`（容器）/ 浅灰 `#F5F5F5`（布局）。AA 文本基线 = 正常文 4.5:1，大文本/UI 组件 3:1。

| 场景 | 前景 / 背景 | 对比度 | 结论 |
|------|------------|--------|------|
| 白字 on `colorPrimary` #2C4EC9 | 白 / #2C4EC9 | **6.9 : 1** | ✅ 超 AA 正常文 |
| 白字 on `colorPrimaryHover` #3E62D6 | 白 / #3E62D6 | **5.4 : 1** | ✅ AA 正常文 |
| 白字 on `colorPrimaryActive` #2440A8 | 白 / #2440A8 | **8.9 : 1** | ✅ AA 正常文 |
| `colorText` 0.88 on 白 | #1F1F1F / 白 | **16.5 : 1** | ✅ AA |
| `colorTextSecondary` 0.65 on 白 | #595959 / 白 | **7.0 : 1** | ✅ AA |
| `colorTextTertiary` 0.45 on 白 | #8C8C8C / 白 | **3.4 : 1** | ⚠️ 低于 4.5，仅用于大文本/装饰/禁用 |
| `colorTextQuaternary` 0.25 on 白 | #BFBFBF / 白 | **1.8 : 1** | ⚠️ 仅装饰/禁用态 |
| `colorLink` #2C4EC9 on 白 | #2C4EC9 / 白 | **6.9 : 1** | ✅ AA 链接 |
| 菜单选中文字 #2C4EC9 on #EAEFF9 | #2C4EC9 / #EAEFF9 | **6.0 : 1** | ✅ AA |
| `colorSuccess` #52C41A 作文字 on 白 | — / 白 | 2.3 : 1 | ⚠️ 不作白字实底（见 §9） |
| `colorWarning` #FAAD14 作文字 on 白 | — / 白 | 1.9 : 1 | ⚠️ 同上 |
| `colorError` #FF4D4F 作文字 on 白 | — / 白 | 3.3 : 1 | ⚠️ 同上 |

**结论**：主色白字、主文本、链接均满足 AA；`Tertiary/Quaternary` 与语义色按 AntD 既有用法（浅底 + 深字 / 图标 / 状态点）使用，不对其实底白字，故不破坏默认可用性。

---

## 3. 字体与排版令牌【需求 B】

- **不引入非标准 Web 字体**，沿用系统字体栈（与 AntD 默认一致）。
- 字号基准 14px，保证后台密集信息下的可读性与一致性。

| Token | 取值 | 说明 |
|-------|------|------|
| `fontFamily` | `-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', 'Liberation Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol'` | 系统字体栈，与 AntD 默认一致 |
| `fontSize` | `14` | 基准正文 |
| `fontSizeLG` | `16` | 次级标题/统计数值标注 |
| `fontSizeSM` | `12` | 辅助/标签/表头注脚 |
| `lineHeight` | `1.5714285714285714` | 基准行高（AntD 默认 22/14） |
| `lineHeightLG` | `1.5` | 大字号行高（可随版本微调） |
| `lineHeightSM` | `1.6666666666666667` | 小字号行高（可随版本微调） |
| `fontWeightRegular` | `400` | 常规 |
| `fontWeightMedium` | `500` | 中等（次要强调） |
| `fontWeightStrong` | `600` | 强（标题/按钮文字/数字） |

> `fontWeightStrong` 为 AntD 既有 token（默认 600）。组件级标题、Statistic 数值、Modal 标题均取此值，建立"克制但仍可辨"的层级。

---

## 4. 间距 / 圆角 / 控件令牌【需求 C】

| Token | 取值 | 说明 |
|-------|------|------|
| `borderRadius` | `4` | 全局圆角（按钮/输入框/菜单项），比 AntD 默认 6 更克制 |
| `borderRadiusLG` | `6` | 大圆角（卡片/弹窗） |
| `borderRadiusSM` | `2` | 小圆角（标签/微控件） |
| `controlHeight` | `32` | 控件高度（AntD 默认，B 端已足够紧凑，维持不动） |
| `controlHeightLG` | `40` | 大控件（搜索框/主操作） |
| `controlHeightSM` | `24` | 小控件（紧凑筛选） |
| `sizeStep` | `4` | 间距基准步长 |
| `sizeUnit` | `4` | 间距基准单位 |
| `paddingXXS` | `4` | 极小内边距 |
| `paddingXS` | `8` | 小内边距 |
| `paddingSM` | `12` | 次小内边距 |
| `padding` | `16` | 默认内边距 |
| `paddingLG` | `24` | 大内边距（卡片/区块） |
| `paddingXL` | `32` | 超大内边距（页级区块） |

**紧凑性说明**：`controlHeight` 保持 32（AntD 默认），已贴合 B 端效率诉求，不额外压低以避免误触。需要数据密集的表格时，用 Table `size="small"` 局部降密（见 §5 Table），**不**全局改动，保证整体节奏统一。

---

## 5. 组件级令牌（components 配置）【需求 D】

> 下列组件 token 与 §8.1 的 `theme.components` 一一对应，确保"现代克制"在高频组件中一致落地。

### Button
| Token | 取值 | 说明 |
|-------|------|------|
| `borderRadius` | `4` | 主/次按钮圆角（克制） |
| `controlHeight` | `32` | 高度统一 |
| `paddingInline` | `16` | 横向内边距 |
| `primaryShadow` | `none` | 去掉主按钮投影，降噪 |
| `defaultShadow` | `none` | 去掉次按钮投影，降噪 |
| 文字色（primary） | `#FFFFFF` | 白字 on #2C4EC9（6.9:1） |

### Table
| Token | 取值 | 说明 |
|-------|------|------|
| `headerBg` | `#FAFAFA` | 表头极浅灰底，与容器白形成微弱层级 |
| `headerColor` | `rgba(0,0,0,0.88)` | 表头文字 |
| `headerSplitColor` | `transparent` | 去表头竖分隔线，更干净 |
| `rowHoverBg` | `#F5F7FD` | 行悬停用主色 4% 浅靛，品牌一致且不刺眼 |
| `borderColor` | `#D9D9D9` | 单元格边框 |
| `cellPaddingBlock` | `12` | 行内纵向内边距（略紧凑） |
| `cellPaddingInline` | `16` | 行内横向内边距 |
| 密度选项 | 局部 `size="small"` | 数据密集表可降密，不全局改 |

### Card
| Token | 取值 | 说明 |
|-------|------|------|
| `borderRadiusLG` | `6` | 卡片圆角（克制但比按钮柔和） |
| `headerBg` | `#FFFFFF` | 卡片头底白 |
| `boxShadow` | 不通过 token 设置 | AntD 默认卡片投影已极弱（`0 1px 2px rgba(0,0,0,.03)` 级）；如需"完全无影"，见 §6 的 CSS 覆盖 `.ant-card { box-shadow: none; }` |

### Menu（侧边栏，浅色 sider）
| Token | 取值 | 说明 |
|-------|------|------|
| `itemSelectedColor` | `#2C4EC9` | 选中项文字=主色 |
| `itemSelectedBg` | `#EAEFF9` | 选中项浅靛底（白字/主色字对比 6:1） |
| `itemHoverColor` | `#2C4EC9` | 悬停文字=主色 |
| `itemHoverBg` | `#F2F4FC` | 悬停浅靛底 |
| `itemColor` | `rgba(0,0,0,0.88)` | 常规项文字 |
| `itemHeight` | `40` | 菜单项高（紧凑） |
| `iconSize` | `16` | 图标尺寸 |

### Tag
| Token | 取值 | 说明 |
|-------|------|------|
| `borderRadius` | `4` | 标签圆角（克制） |
| `fontSize` | `12` | 字号（fontSizeSM） |
| `defaultBg` | `#F5F5F5` | 默认标签浅灰底 |
| `defaultColor` | `rgba(0,0,0,0.65)` | 默认标签文字 |

### Input / Select
| Token（Input） | 取值 | 说明 |
|----------------|------|------|
| `borderRadius` | `4` | 输入框圆角 |
| `controlHeight` | `32` | 高度 |
| `activeBorderColor` | `#2C4EC9` | 聚焦边框=主色 |
| `hoverBorderColor` | `#3E62D6` | 悬停边框=主色悬停 |
| `activeShadow` | `0 0 0 2px rgba(44,78,201,0.12)` | 聚焦环：主色 12% 柔光，克制不刺眼 |

| Token（Select） | 取值 | 说明 |
|-----------------|------|------|
| `borderRadius` | `4` | 选择器圆角 |
| `controlHeight` | `32` | 高度 |
| `optionSelectedBg` | `#EAEFF9` | 选中项浅靛底（与 Menu 一致） |
| `optionSelectedColor` | `#2C4EC9` | 选中项文字 |
| `optionActiveBg` | `#F2F4FC` | 悬停项浅靛底 |

> Select 视觉复用 Input 的聚焦环与边框语义，保证输入类控件全家一致。

### Layout（侧边栏 + 顶栏）
| Token | 取值 | 说明 |
|-------|------|------|
| `headerBg` | `#FFFFFF` | 顶栏白底（靠 1px 下边框分区） |
| `headerHeight` | `56` | 顶栏高（略低于 AntD 默认 64，更紧凑） |
| `headerPadding` | `0 24px` | 顶栏内边距 |
| `siderBg` | `#FAFAFA` | 侧边栏极浅灰底（与内容区白形成微弱分区，避免重色块） |
| `bodyBg` | `#F5F5F5` | 内容区底色=colorBgLayout |

### Statistic
| Token | 取值 | 说明 |
|-------|------|------|
| `titleFontSize` | `14` | 标题（fontSize） |
| `contentFontSize` | `28` | 数值字号（醒目但不夸张） |
| 数值对齐 | CSS `font-variant-numeric: tabular-nums` | 数字等宽对齐（见 §8.2 工具类） |

### Modal
| Token | 取值 | 说明 |
|-------|------|------|
| `borderRadiusLG` | `6` | 弹窗圆角 |
| `titleFontSize` | `16` | 标题字号（fontSizeLG） |
| `titleFontWeight` | 600（CSS 施加） | 标题字重强（Modal 无独立 token，由 `.ant-modal-title{font-weight:600}` 兜底，见 §8.2） |
| `maskBg` | `rgba(0,0,0,0.45)` | 遮罩（AntD 默认，保留以突出浮层） |

---

## 6. 深度与层级（阴影 / 层级）【9 段结构 · 6】

**克制策略**：以"边框 + 留白"建立层级，而非堆叠阴影。仅浮层（Modal/Popover/Dropdown）保留必要投影以表达层级关系。

| 层级 | 场景 | 阴影规则 |
|------|------|---------|
| 扁平 | Layout / Card（默认）/ Menu | 仅 1px `colorBorder`（#D9D9D9）或 `colorSplit`，无投影 |
| 极弱 | Card（若保留默认） | AntD 默认已极弱（`0 1px 2px rgba(0,0,0,.03)` 级），可接受 |
| 浮起 | Dropdown / Popover / Tooltip | AntD 默认（经 `cssVar` 自动管理） |
| 悬浮 | Modal | AntD 默认（`0 6px 16px rgba(0,0,0,.08)` 级），保留以突出遮罩 |

**Z-index 规范**（沿用 AntD 体系，不另造）：
- Dropdown / Popover：`1000`
- 固定 Header：`100`
- Modal / Drawer：`1050`
- Message / Notification / Toast：`1050+`

> **可选去影规则**（最大克制）：若产品要求"零阴影"，在全局 CSS 加 `.ant-card { box-shadow: none !important; }`。默认不建议，因极弱投影有助于卡片在白底上的可辨性。

---

## 7. 深色变体（基于 `theme.darkAlgorithm` 的覆盖与微调）【需求 E】

深色变体**不**从零设计，而是：浅色 `lightTheme` 的 token 作为种子 → 套用 `theme.darkAlgorithm` 自动派生暗色映射 → 再叠加下列"微调覆盖"以消除切换破绽。**核心破绽点是侧边栏 Menu**：浅色用"浅靛底+主色字"，深色必须把 sider 转暗并切换为"暗底+提亮靛字"，否则原浅色 Menu token 在暗 sider 上会失配。

### 7.1 主色在深色下
- 默认：`darkAlgorithm` 会保留 `colorPrimary` 种子不变（仍为 #2C4EC9），在暗底上白字对比更高（仍 ≥6.9:1），可用。
- **推荐微调**：深色下将主色**轻微提亮**至 `#3E62D6`，增强在暗表面的存在感与可读性。
  - `colorPrimary` → `#3E62D6`
  - `colorPrimaryHover` → `#5A7BE0`
  - `colorPrimaryActive` → `#2C4EC9`

### 7.2 背景 / 边框 / 文本（暗色覆盖）
| Token | 深色取值 | 说明 |
|-------|---------|------|
| `colorBgLayout` | `#0E0E12` | 近黑、微带蓝味，与靛主色呼应 |
| `colorBgContainer` | `#16161C` | 容器暗底（微蓝） |
| `colorBgElevated` | `#1F1F27` | 浮层暗底 |
| `colorBorder` | `#30303A` | 暗边框 |
| `colorBorderSecondary` | `#26262E` | 次级暗边框 |
| `colorSplit` | `rgba(255,255,255,0.12)` | 暗分割线 |
| `colorText` | `rgba(255,255,255,0.85)` | 暗主文本 |
| `colorTextSecondary` | `rgba(255,255,255,0.65)` | 暗次要文本 |
| `colorTextTertiary` | `rgba(255,255,255,0.45)` | 暗三级文本 |

### 7.3 组件级暗色微调（消除破绽）
| 组件 | Token | 深色取值 | 说明 |
|------|-------|---------|------|
| Layout | `siderBg` | `#121217` | 侧边栏转暗 |
| Layout | `headerBg` | `#16161C` | 顶栏转暗 |
| Layout | `bodyBg` | `#0E0E12` | 内容区转暗 |
| Menu | `itemSelectedColor` | `#7C93EE` | 提亮靛，保证暗底可读 |
| Menu | `itemSelectedBg` | `rgba(124,147,238,0.18)` | 暗底选中浅靛 |
| Menu | `itemHoverColor` | `#A9B9F2` | 悬停提亮 |
| Menu | `itemHoverBg` | `rgba(124,147,238,0.10)` | 暗底悬停 |
| Menu | `itemColor` | `rgba(255,255,255,0.85)` | 暗底常规项 |
| Table | `headerBg` | `#1A1A22` | 暗表头 |
| Table | `rowHoverBg` | `rgba(255,255,255,0.06)` | 暗行悬停 |
| Table | `borderColor` | `#30303A` | 暗边框 |
| Card | `headerBg` | `#16161C` | 暗卡片头 |
| Statistic | `contentFontSize` | `28` | 保持（字号与主题无关） |

### 7.4 切换无破绽校验清单
- [ ] sider 在暗色下为暗底（非残留浅色）—— 由 `Layout.siderBg` 覆盖保证。
- [ ] Menu 选中态在暗色下为"暗底+提亮靛字"—— 由 §7.3 Menu 覆盖保证。
- [ ] 主色在暗色下提亮且白字达标—— `#3E62D6` 白字约 5.4:1。
- [ ] 边框/分割线在暗色下可见且不刺眼—— `colorBorder #30303A` / `colorSplit rgba(255,255,255,.12)`。
- [ ] 阴影在暗色下由 `darkAlgorithm` 自动减弱—— 无需手工干预。

---

## 8. 双形态交付物【需求 F】

### 8.1 AntD ConfigProvider 配置（TypeScript，可直接粘贴）

> 放置建议：新建 `frontend/src/theme/theme.ts`，导出 `lightTheme` / `darkTheme`，在 `main.tsx` 按 `isDark` 选用（见 §8.4）。

```ts
import { theme, type ThemeConfig } from 'antd';

/** 系统字体栈（与 AntD 默认一致，不引入非标准字体） */
const SYS_FONT =
  "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', 'Liberation Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol'";

/** 浅色主题（默认） */
export const lightTheme: ThemeConfig = {
  token: {
    // —— 主色与派生（沉稳蓝/靛，收敛自 AntD 默认 #1677ff）——
    colorPrimary: '#2C4EC9',
    colorPrimaryHover: '#3E62D6',
    colorPrimaryActive: '#2440A8',
    colorInfo: '#2C4EC9', // info == primary（AntD 默认行为，减少第二种蓝）

    // —— 语义色（保持 AntD 默认，状态可识别）——
    colorSuccess: '#52C41A',
    colorWarning: '#FAAD14',
    colorError: '#FF4D4F',

    // —— 链接 ——
    colorLink: '#2C4EC9',
    colorLinkHover: '#3E62D6',
    colorLinkActive: '#2440A8',

    // —— 中性文本阶 ——
    colorText: 'rgba(0, 0, 0, 0.88)',
    colorTextSecondary: 'rgba(0, 0, 0, 0.65)',
    colorTextTertiary: 'rgba(0, 0, 0, 0.45)',
    colorTextQuaternary: 'rgba(0, 0, 0, 0.25)',

    // —— 背景阶 ——
    colorBgLayout: '#F5F5F5',
    colorBgContainer: '#FFFFFF',
    colorBgElevated: '#FFFFFF',
    colorBgSpotlight: 'rgba(0, 0, 0, 0.85)',

    // —— 边框 / 分割 ——
    colorBorder: '#D9D9D9',
    colorBorderSecondary: '#F0F0F0',
    colorSplit: 'rgba(5, 5, 5, 0.06)',

    // —— 字体 ——
    fontFamily: SYS_FONT,
    fontSize: 14,
    fontSizeLG: 16,
    fontSizeSM: 12,
    lineHeight: 1.5714285714285714,
    lineHeightLG: 1.5,
    lineHeightSM: 1.6666666666666667,
    fontWeightStrong: 600,

    // —— 圆角（微缩）——
    borderRadius: 4,
    borderRadiusLG: 6,
    borderRadiusSM: 2,

    // —— 控件高度 ——
    controlHeight: 32,
    controlHeightLG: 40,
    controlHeightSM: 24,

    // —— 间距基准 ——
    sizeStep: 4,
    sizeUnit: 4,
    paddingXXS: 4,
    paddingXS: 8,
    paddingSM: 12,
    padding: 16,
    paddingLG: 24,
    paddingXL: 32,
  },
  components: {
    Button: {
      borderRadius: 4,
      controlHeight: 32,
      paddingInline: 16,
      primaryShadow: 'none',
      defaultShadow: 'none',
    },
    Table: {
      headerBg: '#FAFAFA',
      headerColor: 'rgba(0, 0, 0, 0.88)',
      headerSplitColor: 'transparent',
      rowHoverBg: '#F5F7FD',
      borderColor: '#D9D9D9',
      cellPaddingBlock: 12,
      cellPaddingInline: 16,
    },
    Card: {
      borderRadiusLG: 6,
      headerBg: '#FFFFFF',
      // 注：Card 无 boxShadow 组件 token；如需完全无影见 §8.2 的 .ant-card 覆盖
    },
    Menu: {
      itemSelectedColor: '#2C4EC9',
      itemSelectedBg: '#EAEFF9',
      itemHoverColor: '#2C4EC9',
      itemHoverBg: '#F2F4FC',
      itemColor: 'rgba(0, 0, 0, 0.88)',
      itemHeight: 40,
      iconSize: 16,
    },
    Tag: {
      borderRadius: 4,
      fontSize: 12,
      defaultBg: '#F5F5F5',
      defaultColor: 'rgba(0, 0, 0, 0.65)',
    },
    Input: {
      borderRadius: 4,
      controlHeight: 32,
      activeBorderColor: '#2C4EC9',
      hoverBorderColor: '#3E62D6',
      activeShadow: '0 0 0 2px rgba(44, 78, 201, 0.12)',
    },
    Select: {
      borderRadius: 4,
      controlHeight: 32,
      optionSelectedBg: '#EAEFF9',
      optionSelectedColor: '#2C4EC9',
      optionActiveBg: '#F2F4FC',
    },
    Layout: {
      headerBg: '#FFFFFF',
      headerHeight: 56,
      headerPadding: '0 24px',
      siderBg: '#FAFAFA',
      bodyBg: '#F5F5F5',
    },
    Statistic: {
      titleFontSize: 14,
      contentFontSize: 28,
    },
    Modal: {
      borderRadiusLG: 6,
      titleFontSize: 16,
      maskBg: 'rgba(0, 0, 0, 0.45)',
    },
  },
};

/** 深色变体：darkAlgorithm 派生 + 微调覆盖（保证切换无破绽） */
export const darkTheme: ThemeConfig = {
  algorithm: theme.darkAlgorithm,
  token: {
    // 主色在暗色下轻微提亮，增强存在感
    colorPrimary: '#3E62D6',
    colorPrimaryHover: '#5A7BE0',
    colorPrimaryActive: '#2C4EC9',
    colorInfo: '#3E62D6',
    colorLink: '#7C93EE', // 提亮以满足深色下 WCAG AA（落在 #16161C 上 ≥4.5:1；见 Phase 4 审查 D1 修正）
    colorLinkHover: '#A9B9F2',
    colorLinkActive: '#5A7BE0',

    // 背景 / 边框 / 文本（暗）
    colorBgLayout: '#0E0E12',
    colorBgContainer: '#16161C',
    colorBgElevated: '#1F1F27',
    colorBorder: '#30303A',
    colorBorderSecondary: '#26262E',
    colorSplit: 'rgba(255, 255, 255, 0.12)',
    colorText: 'rgba(255, 255, 255, 0.85)',
    colorTextSecondary: 'rgba(255, 255, 255, 0.65)',
    colorTextTertiary: 'rgba(255, 255, 255, 0.45)',
  },
  components: {
    Layout: {
      headerBg: '#16161C',
      siderBg: '#121217',
      bodyBg: '#0E0E12',
    },
    Menu: {
      itemSelectedColor: '#7C93EE',
      itemSelectedBg: 'rgba(124, 147, 238, 0.18)',
      itemHoverColor: '#A9B9F2',
      itemHoverBg: 'rgba(124, 147, 238, 0.10)',
      itemColor: 'rgba(255, 255, 255, 0.85)',
    },
    Table: {
      headerBg: '#1A1A22',
      rowHoverBg: 'rgba(255, 255, 255, 0.06)',
      borderColor: '#30303A',
    },
    Card: {
      headerBg: '#16161C',
    },
    Statistic: {
      contentFontSize: 28,
    },
  },
};
```

### 8.2 配套 CSS 变量清单（`:root` + 深色作用域）

> 与 §8.1 的 AntD token **语义一一映射**。因 `main.tsx` 已开 `cssVar: true`，AntD 同时自动注入 `--ant-color-primary` 等；本集为**项目自有镜像**，用于 AntD 之外的自定义表面（自定义布局 CSS、图表、空状态），并可和 `--ant-*` 互相映射（见 §8.3）。

```css
/* ===== 浅色（默认） ===== */
:root {
  /* 主色 / 语义色 */
  --color-primary: #2C4EC9;
  --color-primary-hover: #3E62D6;
  --color-primary-active: #2440A8;
  --color-info: #2C4EC9;
  --color-success: #52C41A;
  --color-warning: #FAAD14;
  --color-error: #FF4D4F;
  --color-link: #2C4EC9;
  --color-link-hover: #3E62D6;
  --color-link-active: #2440A8;

  /* 文本阶 */
  --color-text: rgba(0, 0, 0, 0.88);
  --color-text-secondary: rgba(0, 0, 0, 0.65);
  --color-text-tertiary: rgba(0, 0, 0, 0.45);
  --color-text-quaternary: rgba(0, 0, 0, 0.25);

  /* 背景阶 */
  --color-bg-layout: #F5F5F5;
  --color-bg-container: #FFFFFF;
  --color-bg-elevated: #FFFFFF;
  --color-bg-spotlight: rgba(0, 0, 0, 0.85);

  /* 边框 / 分割 */
  --color-border: #D9D9D9;
  --color-border-secondary: #F0F0F0;
  --color-split: rgba(5, 5, 5, 0.06);

  /* 字体 */
  --font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', 'Liberation Sans', sans-serif;
  --font-size: 14px;
  --font-size-lg: 16px;
  --font-size-sm: 12px;
  --line-height: 1.5714;
  --font-weight-regular: 400;
  --font-weight-medium: 500;
  --font-weight-strong: 600;

  /* 圆角 / 控件 */
  --border-radius: 4px;
  --border-radius-lg: 6px;
  --border-radius-sm: 2px;
  --control-height: 32px;
  --control-height-lg: 40px;
  --control-height-sm: 24px;

  /* 间距 */
  --space-step: 4px;
  --padding-xxs: 4px;
  --padding-xs: 8px;
  --padding-sm: 12px;
  --padding: 16px;
  --padding-lg: 24px;
  --padding-xl: 32px;
}

/* ===== 深色（[data-theme="dark"] 作用域，与 darkTheme 一致） ===== */
[data-theme='dark'] {
  --color-primary: #3E62D6;
  --color-primary-hover: #5A7BE0;
  --color-primary-active: #2C4EC9;
  --color-info: #3E62D6;
  --color-link: #7C93EE;
  --color-link-hover: #A9B9F2;
  --color-link-active: #5A7BE0;

  --color-text: rgba(255, 255, 255, 0.85);
  --color-text-secondary: rgba(255, 255, 255, 0.65);
  --color-text-tertiary: rgba(255, 255, 255, 0.45);

  --color-bg-layout: #0E0E12;
  --color-bg-container: #16161C;
  --color-bg-elevated: #1F1F27;
  --color-bg-spotlight: rgba(0, 0, 0, 0.85);

  --color-border: #30303A;
  --color-border-secondary: #26262E;
  --color-split: rgba(255, 255, 255, 0.12);
}

/* ===== 工具类（克制增强，可选） ===== */
/* 数字等宽对齐：Statistic 数值 */
.ant-statistic-content-value {
  font-variant-numeric: tabular-nums;
}
/* Modal 标题字重强（Modal 无独立字重 token） */
.ant-modal-title,
.ant-modal-confirm-title {
  font-weight: 600;
}
/* 最大克制：如需卡片完全无影（默认不建议） */
/*
.ant-card { box-shadow: none !important; }
*/
```

### 8.3 token ↔ CSS 变量映射表（双形态互相映射）

| AntD token | CSS 变量（浅色） | CSS 变量（深色） | AntD 自动注入（`cssVar:true`） |
|------------|-----------------|-----------------|-------------------------------|
| `colorPrimary` | `--color-primary` | `--color-primary` | `--ant-color-primary` |
| `colorPrimaryHover` | `--color-primary-hover` | `--color-primary-hover` | `--ant-color-primary-hover` |
| `colorInfo` | `--color-info` | `--color-info` | `--ant-color-info` |
| `colorSuccess` | `--color-success` | （同浅） | `--ant-color-success` |
| `colorWarning` | `--color-warning` | （同浅） | `--ant-color-warning` |
| `colorError` | `--color-error` | （同浅） | `--ant-color-error` |
| `colorLink` | `--color-link` | `--color-link` | `--ant-color-link` |
| `colorText` | `--color-text` | `--color-text` | `--ant-color-text` |
| `colorTextSecondary` | `--color-text-secondary` | `--color-text-secondary` | `--ant-color-text-secondary` |
| `colorBgLayout` | `--color-bg-layout` | `--color-bg-layout` | `--ant-color-bg-layout` |
| `colorBgContainer` | `--color-bg-container` | `--color-bg-container` | `--ant-color-bg-container` |
| `colorBgElevated` | `--color-bg-elevated` | `--color-bg-elevated` | `--ant-color-bg-elevated` |
| `colorBorder` | `--color-border` | `--color-border` | `--ant-color-border` |
| `colorBorderSecondary` | `--color-border-secondary` | `--color-border-secondary` | `--ant-color-border-secondary` |
| `colorSplit` | `--color-split` | `--color-split` | `--ant-color-split` |
| `borderRadius` | `--border-radius` | （同浅） | `--ant-border-radius` |
| `borderRadiusLG` | `--border-radius-lg` | （同浅） | `--ant-border-radius-lg` |
| `borderRadiusSM` | `--border-radius-sm` | （同浅） | `--ant-border-radius-sm` |
| `controlHeight` | `--control-height` | （同浅） | `--ant-control-height` |
| `fontSize` | `--font-size` | （同浅） | `--ant-font-size` |
| `fontFamily` | `--font-family` | （同浅） | `--ant-font-family` |

> 用法：自定义 CSS 优先引用 `--ant-*`（与主题实时同步，含深色）；`--color-*` 为可读镜像，用于文档/图表/脱离 AntD 上下文的样式。

### 8.4 集成指引（零侵入落地）

**① 替换 `frontend/src/main.tsx` 的 theme（保持 `cssVar: true`）：**

```tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ConfigProvider, App as AntApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import 'dayjs/locale/zh-cn';
import App from './App';
import { lightTheme, darkTheme } from './theme/theme'; // ← 新增

// isDark 来自主题开关（如 zustand / context / localStorage）
function Root({ isDark }: { isDark: boolean }) {
  return (
    <ConfigProvider locale={zhCN} theme={isDark ? darkTheme : lightTheme}>
      <AntApp>
        <App />
      </AntApp>
    </ConfigProvider>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Root isDark={false} />
  </StrictMode>,
);
```

**② 切换深色时同步 `data-theme` 属性**（让 §8.2 的 `[data-theme="dark"]` 生效）：

```ts
// 主题开关处：切换 isDark 的同时
document.documentElement.setAttribute('data-theme', isDark ? 'dark' : 'light');
```

**③ 去硬编码（改 `frontend/src/layouts/MainLayout.css`）：**

```css
/* 原：.main-layout__trigger:hover { color: #1677ff; } */
.main-layout__trigger:hover { color: var(--color-primary); }      /* 或 var(--ant-color-primary) */

/* 原：border-right: 1px solid rgba(5, 5, 5, 0.06); */
.main-layout__sider { border-right: 1px solid var(--color-split); }

/* 原：.main-layout__user:hover { background-color: rgba(0,0,0,0.04); } */
.main-layout__user:hover { background-color: var(--ant-color-primary-bg); }
```

> 以上三步即完成"仅 ConfigProvider 包裹 + 变量同步"，**不改任何业务组件代码**。`MainLayout.tsx` 已通过 `theme.useToken()` 取 `colorBgContainer` / `colorPrimary`，会自动跟随主题。

---

## 9. Cautions（注意事项）【9 段结构 · 7】

### Never Do
- ❌ 在主色 `#2C4EC9` 实底上放**非白字**（如深色正文）—— 仅白字达标。
- ❌ 把 `colorSuccess/colorWarning/colorError` 当作"白字实底按钮"的主色 —— 其白字对比不足（见 §2.6）；成功/警告/错误操作用 **tinted/bordered 变体**（如 `<Button color="success" variant="outlined">`）或图标 + 浅底标签。
- ❌ 引入非系统 Web 字体或自定义字重（仅 400/500/600）。
- ❌ 为追求"辨识度"另造强调色（如第二个蓝/紫）—— 单主色是克制原则的核心。
- ❌ 用重投影制造层级 —— 以边框 + 留白为主。
- ❌ 全局压低 `controlHeight` 到 <28 —— 会引发误触与密集焦虑，需降密请用 Table `size="small"` 局部处理。

### Prefer
- ✅ 状态表达用"浅底 + 深字 / 图标 / 状态点"，而非实色块。
- ✅ 分区用 `colorSplit` / `colorBorderSecondary` + 留白。
- ✅ 自定义表面引用 `--ant-*` / `--color-*`，保持与主题同步。
- ✅ 密集数据表用 `size="small"`，全局节奏仍统一。

---

## 10. Responsive Behavior（响应式行为）【9 段结构 · 8】

| 断点 | 宽度 | 行为 |
|------|------|------|
| 移动 | < 576px | Sider 自动收起/抽屉化；内容区 `margin` 收敛至 16px；表格横向滚动 |
| 平板 | 576–992px | Sider 可折叠（默认收起）；栅格 2 列；顶栏内边距保持 24px |
| 桌面 | > 992px | 完整布局；Sider 展开（width 220）；内容区 `margin: 24px` |

**适配规则**：
- 布局响应式由 AntD `Grid` / `Layout` 与 `react-router` 路由视图内部处理，主题层**不**依赖断点变量（圆角/间距全断点一致，保证"克制"不被响应式破坏）。
- 深色切换在所有断点一致生效（由 `data-theme` 属性驱动）。
- `controlHeight` / `borderRadius` 全断点恒定，避免小屏下形状漂移。

---

## 11. Agent Prompt Guide（下游消费指引）【9 段结构 · 9】

### Key Instructions（给 Phase 3 原型构建师）
- 严格以 §8.1 的 `lightTheme` / `darkTheme` 作为 `ConfigProvider` 输入，**禁止**在业务组件里写死颜色/Hex。
- 任何非 AntD 原生元素（自定义卡片、图表、空状态）一律引用 `--ant-*` 或 `--color-*`，不得出现裸 `#1677ff` / `#FAFAFA` 等。
- 主色仅用于：主按钮、选中态、激活态、链接、关键强调点。语义色仅用于状态表达。
- 深色切换必须同时：(a) 切换 `darkTheme`；(b) 设置 `document.documentElement[data-theme]`。二者缺一会"破绽"。
- WCAG 自检：主色白字、主文本、链接已 ≥4.5:1；三级/四级文本与语义色按 §2.6 约束使用，不得对其实底白字。

### Quick CSS Snippet（核心变量速查）
```css
:root {
  --color-primary: #2C4EC9;
  --color-bg-container: #FFFFFF;
  --color-bg-layout: #F5F5F5;
  --color-border: #D9D9D9;
  --color-split: rgba(5, 5, 5, 0.06);
  --color-text: rgba(0, 0, 0, 0.88);
  --border-radius: 4px;
  --control-height: 32px;
}
```

---

> **验收口径（Phase 4 / Phase 5）**：① `ConfigProvider` 接入后全站生效、业务组件零改动；② 浅/深两套 token 与 §8.1/§8.2 逐项一致；③ 主色白字、主文本、链接对比 ≥4.5:1；④ 圆角全局 ≤6、主色唯一、阴影仅浮层保留；⑤ 深色切换 sider/Menu 无浅色残留。
