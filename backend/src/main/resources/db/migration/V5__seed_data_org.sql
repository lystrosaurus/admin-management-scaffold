-- V5: 组织模块种子数据
-- LOCAL-ONLY: 仅用于本地开发环境
-- 依赖 V4 表结构

-- ============================================================
-- 1. 部门树：总公司(100) → 技术部(101)/产品部(102) → 后端组(103)/前端组(104)
-- ============================================================
INSERT INTO org_unit (id, parent_id, code, name, full_path, level, sort_order, status, source_type, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(100, 0, 'HQ', '总公司', '/100/', 1, 1, 'ENABLED', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(101, 100, 'TECH', '技术部', '/100/101/', 2, 1, 'ENABLED', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(102, 100, 'PRODUCT', '产品部', '/100/102/', 2, 2, 'ENABLED', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(103, 101, 'BACKEND', '后端组', '/100/101/103/', 3, 1, 'ENABLED', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(104, 101, 'FRONTEND', '前端组', '/100/101/104/', 3, 2, 'ENABLED', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 2. 员工数据
-- ============================================================
INSERT INTO hr_employee (id, employee_no, name, mobile, email, primary_org_id, job_title, employment_status, entry_date, source_type, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(100, 'EMP001', '张三', '13800000001', 'zhangsan@example.com', 101, '技术总监', 'ACTIVE', '2024-01-01', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(101, 'EMP002', '李四', '13800000002', 'lisi@example.com', 103, '后端开发', 'ACTIVE', '2024-03-15', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1),
(102, 'EMP003', '王五', '13800000003', 'wangwu@example.com', 102, '产品经理', 'ACTIVE', '2024-06-01', 'MANUAL', NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 3. 员工-组织关联
-- ============================================================
INSERT INTO employee_org (id, employee_id, org_id, is_primary, position_name, status, created_at, created_by, updated_at, updated_by, deleted, version)
VALUES
(1, 100, 101, 1, '技术总监', 'ACTIVE', NOW(), 'system', NOW(), 'system', 0, 1),
(2, 101, 103, 1, '后端开发', 'ACTIVE', NOW(), 'system', NOW(), 'system', 0, 1),
(3, 102, 102, 1, '产品经理', 'ACTIVE', NOW(), 'system', NOW(), 'system', 0, 1);

-- ============================================================
-- 4. 角色-组织关联（CUSTOM 数据权限）
-- role_id=2 为 V3 中的普通用户角色（USER），赋予技术部(101)的自定义数据权限
-- ============================================================
INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by)
VALUES
(1, 2, 101, NOW(), 'system'),
(2, 2, 102, NOW(), 'system');
