-- M6: 员工关联用户 - hr_employee 增加 user_id 列
-- 背景：前端员工表单「关联用户」将 userId 发往员工接口，并期望回写 userId/username。
--       按前端契约为准，采用「员工侧持有 user_id」方案（employee.user_id -> sys_user.id）。
-- 注意：本沙箱 Flyway 不自动执行；如需手动落地：
--   1) 在目标库执行本文件 SQL；
--   2) 向 flyway_schema_history 插入一行（version='16'、description='add_employee_user_id'、
--      type='SQL'、installed_rank 取当前最大+1、script='V16__add_employee_user_id.sql'、
--      checksum=<本文件内容的 CRC32 有符号 int>、installed_by='manual'、execution_time=0、success=1）；
--   或在 Flyway 启用的部署环境随应用启动自动迁移。

ALTER TABLE `hr_employee`
  ADD COLUMN `user_id` BIGINT COMMENT '关联系统用户ID（sys_user.id），可空'
    AFTER `primary_org_id`,
  ADD KEY `idx_user_id` (`user_id`);
