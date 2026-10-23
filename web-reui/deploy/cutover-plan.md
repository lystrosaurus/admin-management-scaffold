# 切流方案：antd 版 frontend → ReUI 版 web-reui

本方案描述如何将线上入口从 `frontend/`（Ant Design 版）平滑切换到 `web-reui/`（ReUI 版）。
两版前端共用同一套后端契约（`/api/app/**`、统一响应、`PageResult`、Sa-Token JWT），**后端零改动**。

## 0. 前置条件检查清单

| 项 | 状态 | 说明 |
|---|---|---|
| web-reui P0-P3 完成 | ✅ | 17 个页面全部迁移，无占位 |
| `tsc + vite build` 通过 | ✅ | 生产产物按路由分包 |
| 单元测试 16/16 | ✅ | `npm run test` |
| e2e 冒烟通过 | ⬜ | `npm run test:e2e`（需后端 8080 在线） |
| 后端健康 | ⚠️ | 已知 `GET /api/app/employees` 命中记录时 500（Trae 侧修复中） |
| OAuth 回调源 | — | 授权链接用 `window.location.origin` 拼回调地址，**同源切换无需改三方平台配置** |

> 员工列表 500 不阻塞切流（antd 版同样命中该 bug，非回归），但建议修复后切流。

## 1. 推荐方案：同源替换（零 URL 变更）

保持访问域名/端口不变，仅把该源上的静态产物从 antd 版 `dist/` 换成 ReUI 版 `dist/`。
用户无感知，书签、OAuth 回调、Sa-Token 票据全部兼容。

### 1.1 构建与产物

```bash
cd web-reui
npm ci
npm run build          # 产物在 web-reui/dist/
```

### 1.2 切换步骤（nginx 裸机部署）

```bash
# 1. 备份线上现有 antd 版产物与配置
sudo cp -r /etc/nginx/conf.d/admin.conf /etc/nginx/conf.d/admin.conf.bak
sudo mv /var/www/admin-dist /var/www/admin-dist.antd-bak

# 2. 部署 ReUI 版产物
sudo cp -r web-reui/dist /var/www/admin-dist

# 3. 语法检查并热加载（不断连接）
sudo nginx -t && sudo nginx -s reload
```

### 1.3 切换步骤（容器部署）

```bash
docker build -t admin-web-reui:1.0.0 ./web-reui
docker stop admin-web && docker rename admin-web admin-web-antd-bak
docker run -d --name admin-web -p 80:80 --add-host backend:host-gateway admin-web-reui:1.0.0
```

## 2. 备选方案：蓝绿域名（高风险场景预演）

先在独立源上线（如 `reui.admin.example.com`），验证后再切主域名：

1. 部署 web-reui 到新源（nginx server_name 指向新域名）。
2. 新域名下完整走一遍验证清单（第 4 节）。
3. 主域名 nginx `root` / 上游指向新源，`nginx -s reload` 完成切换。
4. 观察 24h，旧源保留待回滚。

> 注意：蓝绿阶段 OAuth 三方平台需把新域名加入回调白名单（授权链接按当前源拼接）。

## 3. 回滚预案

| 场景 | 动作 | 预计耗时 |
|---|---|---|
| 切换后严重 bug | nginx `root` 指回 `/var/www/admin-dist.antd-bak`（或 docker 起回旧容器），`nginx -s reload` | < 2 分钟 |
| 单页功能异常 | 单页问题不阻塞整体，可热修后重新构建发布 | - |
| 后端兼容问题 | 两版共用契约，回滚前端即可恢复，无需动后端 | < 2 分钟 |

回滚决策点：登录失败率 > 5%、核心列表页不可用、控制台批量报错任一出现即回滚。

## 4. 切换后验证清单（按序执行）

- [ ] 登录：admin/admin123 登录成功，落到 `/dashboard`
- [ ] 仪表盘：四张统计卡有数值，资源分布图渲染，最近操作有记录
- [ ] 用户管理：列表出种子数据；搜索「admin」命中 1 条
- [ ] 角色/菜单：角色列表、菜单树形表格渲染；权限分配弹窗可开
- [ ] 员工/组织：页面渲染（员工列表 500 为已知后端问题，确认 toast 有提示且不白屏）
- [ ] 审计：登录/操作日志两个 Tab 有数据
- [ ] 字典/配置项：分页正常
- [ ] 文件管理：上传一个文件 → 列表出现 → 下载成功 → 删除成功
- [ ] 系统状态：应用信息 UP、版本号正确
- [ ] 系统配置：身份源表格渲染
- [ ] 账号安全：三方绑定列表渲染；修改密码表单校验（两次不一致有提示）
- [ ] 集成三页：身份源/主体/匹配页均可打开
- [ ] 主题切换：亮/暗主题正常，刷新后保持
- [ ] 退出登录：回登录页，再访问 `/users` 被守卫重定向
- [ ] 浏览器控制台无未捕获错误（e2e 已覆盖 pageerror 断言）

## 5. 运维备注

- **缓存策略**：`/assets/*` 一年 immutable，`index.html` no-cache——发版只需替换 dist，用户下次打开即更新。
- **上传体积**：nginx `client_max_body_size 50m`，与后端 multipart 限制对齐（如需调整两边同步改）。
- **日志**：nginx access/error log 按现有运维规范收集；前端无遥测。
- **两版共存期**：antd 版 dist 保留 7 天后再清理；`frontend/` 目录不删除，作为回滚参照。
