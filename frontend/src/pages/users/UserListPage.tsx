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
  type TablePaginationConfig,
} from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { listUsers, createUser, updateUser, deleteUser } from '@/api/user';
import type { User, CreateUserRequest, UpdateUserRequest, UserQueryParams } from '@/types/user';
import type { Status } from '@/types/api';
import dayjs from 'dayjs';

/** 状态选项 */
const statusOptions = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
];

/** 用户管理页面 */
const UserListPage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<UserQueryParams>({});

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增用户');
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取用户列表
   */
  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const params: UserQueryParams = {
        page: current,
        size: pageSize,
        ...searchParams,
      };
      const result = await listUsers(params);
      setDataSource(result.items);
      setTotal(result.total);
    } catch (error) {
      message.error((error as Error).message || '获取用户列表失败');
    } finally {
      setLoading(false);
    }
  }, [current, pageSize, searchParams]);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    setCurrent(1);
    setSearchParams({
      username: form.getFieldValue('searchUsername'),
      nickname: form.getFieldValue('searchNickname'),
      status: form.getFieldValue('searchStatus'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    form.resetFields(['searchUsername', 'searchNickname', 'searchStatus']);
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
    setModalTitle('新增用户');
    setEditingUser(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: User) => {
    setModalTitle('编辑用户');
    setEditingUser(record);
    form.setFieldsValue({
      username: record.username,
      nickname: record.nickname,
      email: record.email,
      phone: record.phone,
      status: record.status,
    });
    setModalVisible(true);
  };

  /**
   * 处理删除
   */
  const handleDelete = async (id: number) => {
    try {
      await deleteUser(id);
      message.success('删除成功');
      fetchUsers();
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

      if (editingUser) {
        // 编辑用户
        const updateData: UpdateUserRequest = {
          nickname: values.nickname,
          email: values.email,
          phone: values.phone,
          status: values.status,
        };
        await updateUser(editingUser.id, updateData);
        message.success('更新成功');
      } else {
        // 新增用户
        const createData: CreateUserRequest = {
          username: values.username,
          nickname: values.nickname,
          password: values.password,
          email: values.email,
          phone: values.phone,
          status: values.status,
        };
        await createUser(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchUsers();
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
   * 渲染状态标签
   */
  const renderStatusTag = (status: Status) => {
    const color = status === 'ACTIVE' ? 'green' : 'red';
    const text = status === 'ACTIVE' ? '启用' : '禁用';
    return <Tag color={color}>{text}</Tag>;
  };

  /** 表格列定义 */
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      width: 120,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 180,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '手机',
      dataIndex: 'phone',
      key: 'phone',
      width: 140,
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
      title: '最后登录时间',
      dataIndex: 'lastLoginAt',
      key: 'lastLoginAt',
      width: 180,
      render: (text: string | undefined) =>
        text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: User) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该用户吗？"
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
        <Form.Item name="searchUsername">
          <Input placeholder="用户名" allowClear style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="searchNickname">
          <Input placeholder="昵称" allowClear style={{ width: 150 }} />
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
          新增用户
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
        scroll={{ x: 1200 }}
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
            name="username"
            label="用户名"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, max: 50, message: '用户名长度为 3-50 个字符' },
            ]}
          >
            <Input placeholder="请输入用户名" disabled={!!editingUser} />
          </Form.Item>
          <Form.Item
            name="nickname"
            label="昵称"
            rules={[{ required: true, message: '请输入昵称' }]}
          >
            <Input placeholder="请输入昵称" />
          </Form.Item>
          {!editingUser && (
            <Form.Item
              name="password"
              label="密码"
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, max: 100, message: '密码长度为 6-100 个字符' },
              ]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>
          )}
          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="phone" label="手机">
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={statusOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default UserListPage;
