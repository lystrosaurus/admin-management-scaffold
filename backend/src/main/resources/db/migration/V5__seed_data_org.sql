-- V5: 组织模块种子数据
-- LOCAL-ONLY: 仅用于本地开发环境
-- 依赖 V4 表结构

-- ============================================================
-- 1. 部门树：总公司(100) → 技术部(101)/产品部(102) → 后端组(103)/前端组(104)
-- ============================================================
INSERT INTO org_unit (id, parent_id, code, name, status, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(100, 0, 'HQ', '总公司', 'ENABLED', 1, NOW(), NOW(), 'system', 'system', 0, 1),
(101, 100, 'TECH', '技术部', 'ENABLED', 1, NOW(), NOW(), 'system', 'system', 0, 1),
(102, 100, 'PRODUCT', '产品部', 'ENABLED', 2, NOW(), NOW(), 'system', 'system', 0, 1),
(103, 101, 'BACKEND', '后端组', 'ENABLED', 1, NOW(), NOW(), 'system', 'system', 0, 1),
(104, 101, 'FRONTEND', '前端组', 'ENABLED', 2, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 2. 员工数据
-- ============================================================
INSERT INTO hr_employee (id, org_id, employee_no, name, status, hire_date, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(100, 101, 'EMP001', '张三', 'ACTIVE', '2024-01-01', NOW(), NOW(), 'system', 'system', 0, 1),
(101, 103, 'EMP002', '李四', 'ACTIVE', '2024-03-15', NOW(), NOW(), 'system', 'system', 0, 1),
(102, 102, 'EMP003', '王五', 'ACTIVE', '2024-06-01', NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 3. 员工-部门关联
-- ============================================================
INSERT INTO employee_org (id, employee_id, org_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(1, 100, 101, NOW(), 'system', NOW(), 'system', 0),
(2, 101, 103, NOW(), 'system', NOW(), 'system', 0),
(3, 102, 102, NOW(), 'system', NOW(), 'system', 0);

-- ============================================================
-- 4. 角色-部门关联（CUSTOM 数据权限）
-- role_id=2 为 V3 中的普通用户角色（USER），赋予技术部(101)的自定义数据权限
-- ============================================================
INSERT INTO sys_role_org (id, role_id, org_id, created_at, created_by)
VALUES
(1, 2, 101, NOW(), 'system'),
(2, 2, 102, NOW(), 'system');
