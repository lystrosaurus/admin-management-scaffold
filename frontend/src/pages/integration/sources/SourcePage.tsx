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
  InputNumber,
  message,
  Popconfirm,
  type TablePaginationConfig,
} from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import {
  listSources,
  createSource,
  updateSource,
  deleteSource,
} from '@/api/integration';
import type {
  SourceVO,
  SourceCreateRequest,
  SourceUpdateRequest,
} from '@/types/integration';
import dayjs from 'dayjs';

/** 来源类型选项 */
const sourceTypeOptions = [
  { label: 'HR', value: 'HR' },
  { label: 'IM', value: 'IM' },
  { label: 'OA', value: 'OA' },
];

/** 状态选项（后端 ENABLED / DISABLED） */
const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
];

/** 外部身份源管理页面 */
const SourcePage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<SourceVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数（前端过滤）
  const [searchParams, setSearchParams] = useState<{ name?: string; code?: string }>({});

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增身份源');
  const [editingSource, setEditingSource] = useState<SourceVO | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取身份源列表
   */
  const fetchSources = useCallback(async () => {
    setLoading(true);
    try {
      const all = await listSources();
      const filtered = all.filter((s) => {
        if (searchParams.name && !s.name.includes(searchParams.name)) return false;
        if (searchParams.code && !s.code.includes(searchParams.code)) return false;
        return true;
      });
      setDataSource(filtered);
    } catch (error) {
      message.error((error as Error).message || '获取身份源列表失败');
    } finally {
      setLoading(false);
    }
  }, [searchParams]);

  useEffect(() => {
    // 首屏加载：setState 仅在 await 之后执行，并带取消保护
    let ignore = false;
    void (async () => {
      try {
        const all = await listSources();
        if (ignore) return;
        const filtered = all.filter((s) => {
          if (searchParams.name && !s.name.includes(searchParams.name)) return false;
          if (searchParams.code && !s.code.includes(searchParams.code)) return false;
          return true;
        });
        setDataSource(filtered);
      } catch (error) {
        if (!ignore) message.error((error as Error).message || '获取身份源列表失败');
      } finally {
        if (!ignore) setLoading(false);
      }
    })();
    return () => {
      ignore = true;
    };
  }, [searchParams]);

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    setLoading(true);
    setCurrent(1);
    setSearchParams({
      name: form.getFieldValue('searchName'),
      code: form.getFieldValue('searchCode'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    setLoading(true);
    form.resetFields(['searchName', 'searchCode']);
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
    setModalTitle('新增身份源');
    setEditingSource(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: SourceVO) => {
    setModalTitle('编辑身份源');
    setEditingSource(record);
    form.setFieldsValue({
      code: record.code,
      name: record.name,
      sourceType: record.sourceType,
      tenantKey: record.tenantKey,
      priority: record.priority,
      configJson: record.configJson,
      status: record.status,
    });
    setModalVisible(true);
  };

  /**
   * 处理删除
   */
  const handleDelete = async (id: number) => {
    try {
      await deleteSource(id);
      message.success('删除成功');
      fetchSources();
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

      if (editingSource) {
        // 编辑身份源
        const updateData: SourceUpdateRequest = {
          name: values.name,
          sourceType: values.sourceType,
          tenantKey: values.tenantKey,
          status: values.status,
          priority: values.priority,
          configJson: values.configJson,
        };
        await updateSource(editingSource.id, updateData);
        message.success('更新成功');
      } else {
        // 新增身份源
        const createData: SourceCreateRequest = {
          code: values.code,
          name: values.name,
          sourceType: values.sourceType,
          tenantKey: values.tenantKey,
          priority: values.priority,
          configJson: values.configJson,
        };
        await createSource(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchSources();
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
  const renderStatusTag = (status: string) => {
    const color = status === 'ENABLED' ? 'green' : 'red';
    const text = status === 'ENABLED' ? '启用' : '禁用';
    return <Tag color={color}>{text}</Tag>;
  };

  /**
   * 渲染来源类型标签
   */
  const renderSourceTypeTag = (type: string) => {
    const text = sourceTypeOptions.find((o) => o.value === type)?.label || type;
    return <Tag>{text}</Tag>;
  };

  /** 表格列定义 */
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '系统编码',
      dataIndex: 'code',
      key: 'code',
      width: 140,
    },
    {
      title: '系统名称',
      dataIndex: 'name',
      key: 'name',
      width: 160,
    },
    {
      title: '来源类型',
      dataIndex: 'sourceType',
      key: 'sourceType',
      width: 100,
      render: (type: string) => renderSourceTypeTag(type),
    },
    {
      title: '租户标识',
      dataIndex: 'tenantKey',
      key: 'tenantKey',
      width: 140,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 90,
      render: (value: number | undefined) => (value ?? '-'),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => renderStatusTag(status),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (text: string | undefined) =>
        text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: SourceVO) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该身份源吗？"
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
        <Form.Item name="searchName">
          <Input placeholder="系统名称" allowClear style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="searchCode">
          <Input placeholder="系统编码" allowClear style={{ width: 150 }} />
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
          新增身份源
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
        destroyOnHidden
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ sourceType: 'HR', status: 'ENABLED' }}
        >
          <Form.Item
            name="code"
            label="系统编码"
            rules={[
              { required: true, message: '请输入系统编码' },
              { max: 50, message: '系统编码长度不超过 50' },
            ]}
          >
            <Input placeholder="请输入系统编码" disabled={!!editingSource} />
          </Form.Item>
          <Form.Item
            name="name"
            label="系统名称"
            rules={[{ required: true, message: '请输入系统名称' }]}
          >
            <Input placeholder="请输入系统名称" />
          </Form.Item>
          <Form.Item
            name="sourceType"
            label="来源类型"
            rules={[{ required: true, message: '请选择来源类型' }]}
          >
            <Select options={sourceTypeOptions} />
          </Form.Item>
          <Form.Item name="tenantKey" label="租户标识">
            <Input placeholder="请输入租户标识" />
          </Form.Item>
          <Form.Item name="priority" label="优先级">
            <InputNumber placeholder="请输入优先级" style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="configJson" label="扩展配置(JSON)">
            <Input.TextArea placeholder="请输入扩展配置 JSON" rows={3} />
          </Form.Item>
          {editingSource && (
            <Form.Item name="status" label="状态">
              <Select options={statusOptions} />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </Card>
  );
};

export default SourcePage;
