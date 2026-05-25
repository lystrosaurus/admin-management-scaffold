-- 添加关联表缺失的索引，优化查询性能
-- Issue: P0 安全与性能修复

-- 为 sys_user_role.user_id 添加单列索引
-- 优化单独按 user_id 查询用户角色的场景（如查询用户的所有角色）
ALTER TABLE `sys_user_role` ADD INDEX `idx_user_id` (`user_id`);

-- 为 sys_role_permission.role_id 添加单列索引
-- 优化单独按 role_id 查询角色权限的场景（如查询角色的所有权限）
ALTER TABLE `sys_role_permission` ADD INDEX `idx_role_id` (`role_id`);
