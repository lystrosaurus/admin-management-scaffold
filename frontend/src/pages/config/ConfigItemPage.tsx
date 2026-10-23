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
import { listConfig, createConfig, updateConfig, deleteConfig } from '@/api/config';
import type {
  ConfigItemVO,
  ConfigItemCreateDTO,
  ConfigItemUpdateDTO,
  ConfigItemQuery,
} from '@/types/config';

/** 状态选项 */
const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
];

/** 配置类型选项 */
const configTypeOptions = [
  { label: 'STRING', value: 'STRING' },
  { label: 'NUMBER', value: 'NUMBER' },
  { label: 'BOOLEAN', value: 'BOOLEAN' },
  { label: 'JSON', value: 'JSON' },
];

/** 配置类型标签颜色 */
const configTypeColor: Record<string, string> = {
  STRING: 'blue',
  NUMBER: 'cyan',
  BOOLEAN: 'purple',
  JSON: 'orange',
};

/** 配置项页面 */
const ConfigItemPage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<ConfigItemVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<ConfigItemQuery>({});

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增配置项');
  const [editingConfig, setEditingConfig] = useState<ConfigItemVO | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取配置项列表
   */
  const fetchConfig = useCallback(async () => {
    setLoading(true);
    try {
      const params: ConfigItemQuery = {
        page: current,
        size: pageSize,
        ...searchParams,
      };
      const result = await listConfig(params);
      setDataSource(result.items);
      setTotal(result.total);
    } catch (error) {
      message.error((error as Error).message || '获取配置项列表失败');
    } finally {
      setLoading(false);
    }
  }, [current, pageSize, searchParams]);

  useEffect(() => {
    // 首屏/翻页加载：setState 仅在 await 之后执行，并带取消保护
    let ignore = false;
    void (async () => {
      try {
        const params: ConfigItemQuery = {
          page: current,
          size: pageSize,
          ...searchParams,
        };
        const result = await listConfig(params);
        if (ignore) return;
        setDataSource(result.items);
        setTotal(result.total);
      } catch (error) {
        if (!ignore) message.error((error as Error).message || '获取配置项列表失败');
      } finally {
        if (!ignore) setLoading(false);
      }
    })();
    return () => {
      ignore = true;
    };
  }, [current, pageSize, searchParams]);

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    setLoading(true);
    setCurrent(1);
    setSearchParams({
      configKey: form.getFieldValue('searchConfigKey'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    setLoading(true);
    form.resetFields(['searchConfigKey']);
    setCurrent(1);
    setSearchParams({});
  };

  /**
   * 处理分页变化
   */
  const handleTableChange = (pagination: TablePaginationConfig) => {
    setLoading(true);
    setCurrent(pagination.current || 1);
    setPageSize(pagination.pageSize || 10);
  };

  /**
   * 打开新增弹窗
   */
  const handleAdd = () => {
    setModalTitle('新增配置项');
    setEditingConfig(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: ConfigItemVO) => {
    setModalTitle('编辑配置项');
    setEditingConfig(record);
    form.setFieldsValue({
      configKey: record.configKey,
      configValue: record.configValue,
      configType: record.configType,
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
      await deleteConfig(id);
      message.success('删除成功');
      fetchConfig();
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

      if (editingConfig) {
        // 编辑配置项
        const updateData: ConfigItemUpdateDTO = {
          configKey: values.configKey,
          configValue: values.configValue,
          configType: values.configType,
          description: values.description,
          status: values.status,
        };
        await updateConfig(editingConfig.id, updateData);
        message.success('更新成功');
      } else {
        // 新增配置项
        const createData: ConfigItemCreateDTO = {
          configKey: values.configKey,
          configValue: values.configValue,
          configType: values.configType,
          description: values.description,
          status: values.status,
        };
        await createConfig(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchConfig();
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
   * 渲染配置类型标签
   */
  const renderConfigTypeTag = (configType: string) => (
    <Tag color={configTypeColor[configType] || 'default'}>{configType}</Tag>
  );

  /** 表格列定义 */
  const columns = [
    {
      title: '配置键',
      dataIndex: 'configKey',
      key: 'configKey',
      width: 200,
    },
    {
      title: '配置值',
      dataIndex: 'configValue',
      key: 'configValue',
      width: 200,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '配置类型',
      dataIndex: 'configType',
      key: 'configType',
      width: 120,
      render: (configType: string) => renderConfigTypeTag(configType),
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
      render: (status: string) => renderStatusTag(status),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: ConfigItemVO) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该配置项吗？"
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
        <Form.Item name="searchConfigKey">
          <Input placeholder="配置键" allowClear style={{ width: 200 }} />
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
          新增
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
        destroyOnHidden
      >
        <Form form={form} layout="vertical" initialValues={{ status: 'ENABLED', configType: 'STRING' }}>
          <Form.Item
            name="configKey"
            label="配置键"
            rules={[{ required: true, message: '请输入配置键' }]}
          >
            <Input placeholder="请输入配置键" />
          </Form.Item>
          <Form.Item
            name="configValue"
            label="配置值"
            rules={[{ required: true, message: '请输入配置值' }]}
          >
            <Input placeholder="请输入配置值" />
          </Form.Item>
          <Form.Item
            name="configType"
            label="配置类型"
            rules={[{ required: true, message: '请选择配置类型' }]}
          >
            <Select options={configTypeOptions} />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea placeholder="请输入描述" rows={3} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={statusOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default ConfigItemPage;
