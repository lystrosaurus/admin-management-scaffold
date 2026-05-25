-- 补充 sys_role_menu 表 role_id 索引（V8 遗漏）
-- 优化按角色查询菜单列表的性能

ALTER TABLE `sys_role_menu` ADD INDEX `idx_role_id` (`role_id`);
