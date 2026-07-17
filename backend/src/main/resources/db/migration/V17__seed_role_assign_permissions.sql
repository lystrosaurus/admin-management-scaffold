-- V17: 角色赋值权限/菜单 权限码，并关联管理员角色
-- 依赖 V3 的 ADMIN(role id=1) 与既有 system:role:{view,create,update,delete}(id 5-8)
-- 仅新增 2 个"赋值"权限码(19,20)，绑定 ADMIN。严禁修改 V1-V16。
-- 幂等保障：INSERT IGNORE + 显式 id(19/20) + sys_permission(uk_permission_code) / sys_role_permission(uk_role_permission) 唯一键

INSERT IGNORE INTO sys_permission
  (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
  (19, 'system:role:assign-permission', '角色分配权限', '为角色分配权限码（含提权控制）', 'ENABLED', 'BUTTON', 'system', 'role', 'assign-permission', 19, NOW(), NOW(), 'system', 'system', 0, 1),
  (20, 'system:role:assign-menu',       '角色分配菜单', '为角色分配菜单',             'ENABLED', 'BUTTON', 'system', 'role', 'assign-menu',       20, NOW(), NOW(), 'system', 'system', 0, 1);

INSERT IGNORE INTO sys_role_permission
  (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
  (19, 1, 19, NOW(), 'system', NOW(), 'system', 0),
  (20, 1, 20, NOW(), 'system', NOW(), 'system', 0);
