import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Input,
  Select,
  Tag,
  Modal,
  Form,
  message,
  Popconfirm,
  Tree,
  Spin,
  type TablePaginationConfig,
} from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined, SettingOutlined } from '@ant-design/icons';
import { listRoles, createRole, updateRole, deleteRole, assignPermissions } from '@/api/role';
import { listPermissions, getPermissionsByRoleId } from '@/api/permission';
import type { Role, CreateRoleRequest, UpdateRoleRequest, RoleQueryParams, Permission } from '@/types/role';
import type { Status } from '@/types/api';
import dayjs from 'dayjs';

/** 状态选项 */
const statusOptions = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
];

/** 权限类型映射 */
const permissionTypeMap: Record<string, string> = {
  MENU: '菜单',
  BUTTON: '按钮',
  API: '接口',
};

/** 角色管理页面 */
const RoleListPage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<Role[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<RoleQueryParams>({});

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增角色');
  const [editingRole, setEditingRole] = useState<Role | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 权限分配弹窗
  const [permissionModalVisible, setPermissionModalVisible] = useState(false);
  const [currentRoleId, setCurrentRoleId] = useState<number | null>(null);
  const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<number[]>([]);
  const [permissionLoading, setPermissionLoading] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取角色列表
   */
  const fetchRoles = useCallback(async () => {
    setLoading(true);
    try {
      const params: RoleQueryParams = {
        current,
        size: pageSize,
        ...searchParams,
      };
      const result = await listRoles(params);
      setDataSource(result.records);
      setTotal(result.total);
    } catch (error) {
      message.error((error as Error).message || '获取角色列表失败');
    } finally {
      setLoading(false);
    }
  }, [current, pageSize, searchParams]);

  useEffect(() => {
    fetchRoles();
  }, [fetchRoles]);

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    setCurrent(1);
    setSearchParams({
      code: form.getFieldValue('searchCode'),
      name: form.getFieldValue('searchName'),
      status: form.getFieldValue('searchStatus'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    form.resetFields(['searchCode', 'searchName', 'searchStatus']);
    setCurrent(1);
    setSearchParams({});
  };

  /**
   * 处理分页变化
   */
  const handleTableChange = (pagination: TablePaginationConfig) => {
    setCurrent(pagination.current || 1);
    setPageSize(pagination.pageSize || 10);
  };

  /**
   * 打开新增弹窗
   */
  const handleAdd = () => {
    setModalTitle('新增角色');
    setEditingRole(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: Role) => {
    setModalTitle('编辑角色');
    setEditingRole(record);
    form.setFieldsValue({
      code: record.code,
      name: record.name,
      description: record.description,
      status: record.status,
    });
    setModalVisible(true);
  };

  /**
   * 处理删除
   */
  const handleDelete = async (id: number) => {
    try {
      await deleteRole(id);
      message.success('删除成功');
      fetchRoles();
    } catch (error) {
      message.error((error as Error).message || '删除失败');
    }
  };

  /**
   * 处理表单提交
   */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);

      if (editingRole) {
        // 编辑角色
        const updateData: UpdateRoleRequest = {
          name: values.name,
          description: values.description,
          status: values.status,
        };
        await updateRole(editingRole.id, updateData);
        message.success('更新成功');
      } else {
        // 新增角色
        const createData: CreateRoleRequest = {
          code: values.code,
          name: values.name,
          description: values.description,
          status: values.status,
        };
        await createRole(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchRoles();
    } catch (error) {
      if ((error as { errorFields?: unknown }).errorFields) {
        // 表单验证错误，不处理
        return;
      }
      message.error((error as Error).message || '操作失败');
    } finally {
      setSubmitting(false);
    }
  };

  /**
   * 打开权限分配弹窗
   */
  const handlePermission = async (record: Role) => {
    setCurrentRoleId(record.id);
    setPermissionModalVisible(true);
    setPermissionLoading(true);

    try {
      // 并行获取所有权限和当前角色的权限
      const [permissions, rolePermissions] = await Promise.all([
        listPermissions(),
        getPermissionsByRoleId(record.id),
      ]);

      setAllPermissions(permissions);
      setCheckedKeys(rolePermissions.map((p) => p.id));
    } catch (error) {
      message.error((error as Error).message || '获取权限数据失败');
    } finally {
      setPermissionLoading(false);
    }
  };

  /**
   * 处理权限分配提交
   */
  const handlePermissionSubmit = async () => {
    if (!currentRoleId) return;

    setSubmitting(true);
    try {
      await assignPermissions(currentRoleId, checkedKeys);
      message.success('权限分配成功');
      setPermissionModalVisible(false);
    } catch (error) {
      message.error((error as Error).message || '权限分配失败');
    } finally {
      setSubmitting(false);
    }
  };

  /**
   * 渲染状态标签
   */
  const renderStatusTag = (status: Status) => {
    const color = status === 'ACTIVE' ? 'green' : 'red';
    const text = status === 'ACTIVE' ? '启用' : '禁用';
    return <Tag color={color}>{text}</Tag>;
  };

  /**
   * 构建权限树数据
   */
  const buildPermissionTree = (permissions: Permission[]) => {
    // 按类型分组
    const grouped: Record<string, Permission[]> = {
      MENU: [],
      BUTTON: [],
      API: [],
    };

    permissions.forEach((p) => {
      if (grouped[p.type]) {
        grouped[p.type].push(p);
      }
    });

    // 构建树结构
    return Object.entries(grouped).map(([type, items]) => ({
      key: `group-${type}`,
      title: permissionTypeMap[type] || type,
      children: items.map((item) => ({
        key: item.id,
        title: `${item.name} (${item.code})`,
      })),
    }));
  };

  /** 表格列定义 */
  const columns = [
    {
      title: '角色编码',
      dataIndex: 'code',
      key: 'code',
      width: 150,
    },
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
      width: 150,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 200,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: Status) => renderStatusTag(status),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: Role) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            icon={<SettingOutlined />}
            onClick={() => handlePermission(record)}
          >
            权限
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该角色吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      {/* 搜索栏 */}
      <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="searchCode">
          <Input placeholder="角色编码" allowClear style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="searchName">
          <Input placeholder="角色名称" allowClear style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="searchStatus">
          <Select
            placeholder="状态"
            allowClear
            options={statusOptions}
            style={{ width: 120 }}
          />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
              搜索
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      {/* 工具栏 */}
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增角色
        </Button>
      </div>

      {/* 表格 */}
      <Table
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        pagination={{
          current,
          pageSize,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={handleTableChange}
        scroll={{ x: 1000 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={modalTitle}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ status: 'ACTIVE' }}
        >
          <Form.Item
            name="code"
            label="角色编码"
            rules={[
              { required: true, message: '请输入角色编码' },
              { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线' },
            ]}
          >
            <Input placeholder="请输入角色编码" disabled={!!editingRole} />
          </Form.Item>
          <Form.Item
            name="name"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称' }]}
          >
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea placeholder="请输入描述" rows={3} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={statusOptions} />
          </Form.Item>
        </Form>
      </Modal>

      {/* 权限分配弹窗 */}
      <Modal
        title="权限分配"
        open={permissionModalVisible}
        onOk={handlePermissionSubmit}
        onCancel={() => setPermissionModalVisible(false)}
        confirmLoading={submitting}
        width={600}
        destroyOnClose
      >
        <div style={{ marginBottom: 8, color: '#666' }}>
          选择要分配给该角色的权限：
        </div>
        <Spin spinning={permissionLoading}>
          <Tree
            checkable
            defaultExpandAll
            checkedKeys={checkedKeys}
            onCheck={(checked) => setCheckedKeys(checked as number[])}
            treeData={buildPermissionTree(allPermissions)}
            style={{ maxHeight: 400, overflow: 'auto', border: '1px solid #d9d9d9', borderRadius: 6, padding: 8 }}
          />
        </Spin>
      </Modal>
    </Card>
  );
};

export default RoleListPage;
