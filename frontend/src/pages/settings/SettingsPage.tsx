import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Form,
  Input,
  InputNumber,
  Button,
  Select,
  Descriptions,
  message,
  Divider,
  Table,
  Modal,
  Space,
  Tag,
  Popconfirm,
  type TableColumnsType,
} from 'antd';
import { SaveOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import {
  listIntegrationSources,
  createIntegrationSource,
  updateIntegrationSource,
  deleteIntegrationSource,
} from '@/api/integrationSource';
import type {
  IntegrationSource,
  CreateIntegrationSourceRequest,
  UpdateIntegrationSourceRequest,
} from '@/types/integrationSource';

/** 语言选项 */
const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
];

/** 来源类型选项 */
const sourceTypeOptions = [
  { label: 'HR', value: 'HR' },
  { label: 'IM', value: 'IM' },
  { label: 'OA', value: 'OA' },
];

/** 系统配置页面 */
const SettingsPage = () => {
  const [form] = Form.useForm();
  const [saving, setSaving] = useState(false);

  // === 系统配置（mock，暂无后端 API）===
  const systemInfo = {
    name: 'Admin Management Scaffold',
    version: '1.0.0',
    environment: 'Production',
    javaVersion: '21',
    springBootVersion: '4.0.6',
    nodeVersion: '20.x',
  };

  const [config, setConfig] = useState({
    siteName: 'Admin Management',
    language: 'zh-CN',
    loginFailLockCount: 5,
    sessionTimeout: 30,
    fileUploadMaxSize: 10,
  });

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      await new Promise((resolve) => setTimeout(resolve, 500));
      setConfig(values);
      message.success('保存成功');
    } catch (error) {
      if ((error as { errorFields?: unknown }).errorFields) {
        return;
      }
      message.error((error as Error).message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  // === 外部身份源管理 ===
  const [sources, setSources] = useState<IntegrationSource[]>([]);
  const [sourcesLoading, setSourcesLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSource, setEditingSource] = useState<IntegrationSource | null>(null);
  const [sourceForm] = Form.useForm();

  const fetchSources = useCallback(async () => {
    setSourcesLoading(true);
    try {
      const data = await listIntegrationSources();
      setSources(data);
    } catch {
      message.error('加载身份源列表失败');
    } finally {
      setSourcesLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchSources();
  }, [fetchSources]);

  const handleAddSource = () => {
    setEditingSource(null);
    sourceForm.resetFields();
    sourceForm.setFieldsValue({ priority: 0 });
    setModalOpen(true);
  };

  const handleEditSource = (record: IntegrationSource) => {
    setEditingSource(record);
    sourceForm.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDeleteSource = async (id: number) => {
    try {
      await deleteIntegrationSource(id);
      message.success('删除成功');
      fetchSources();
    } catch {
      message.error('删除失败');
    }
  };

  const handleSourceSubmit = async () => {
    try {
      const values = await sourceForm.validateFields();
      if (editingSource) {
        const updateData: UpdateIntegrationSourceRequest = {
          name: values.name,
          sourceType: values.sourceType,
          tenantKey: values.tenantKey,
          status: values.status,
          priority: values.priority,
          configJson: values.configJson,
        };
        await updateIntegrationSource(editingSource.id, updateData);
        message.success('更新成功');
      } else {
        const createData: CreateIntegrationSourceRequest = {
          code: values.code,
          name: values.name,
          sourceType: values.sourceType,
          tenantKey: values.tenantKey,
          priority: values.priority,
          configJson: values.configJson,
        };
        await createIntegrationSource(createData);
        message.success('创建成功');
      }
      setModalOpen(false);
      fetchSources();
    } catch {
      // form validation error, ignore
    }
  };

  const sourceColumns: TableColumnsType<IntegrationSource> = [
    { title: '编码', dataIndex: 'code', width: 120 },
    { title: '名称', dataIndex: 'name', width: 150 },
    { title: '来源类型', dataIndex: 'sourceType', width: 80 },
    { title: '租户标识', dataIndex: 'tenantKey', width: 120 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: string) => (
        <Tag color={status === 'ENABLED' ? 'green' : 'default'}>
          {status === 'ENABLED' ? '启用' : '禁用'}
        </Tag>
      ),
    },
    { title: '优先级', dataIndex: 'priority', width: 80 },
    {
      title: '操作',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditSource(record)}
          >
            编辑
          </Button>
          <Popconfirm title="确认删除？" onConfirm={() => handleDeleteSource(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      {/* 系统基本信息（只读） */}
      <Card title="系统信息" style={{ marginBottom: 16 }}>
        <Descriptions column={2} bordered size="small">
          <Descriptions.Item label="系统名称">{systemInfo.name}</Descriptions.Item>
          <Descriptions.Item label="版本号">{systemInfo.version}</Descriptions.Item>
          <Descriptions.Item label="运行环境">{systemInfo.environment}</Descriptions.Item>
          <Descriptions.Item label="Java 版本">{systemInfo.javaVersion}</Descriptions.Item>
          <Descriptions.Item label="Spring Boot 版本">{systemInfo.springBootVersion}</Descriptions.Item>
          <Descriptions.Item label="Node.js 版本">{systemInfo.nodeVersion}</Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 可编辑配置 */}
      <Card title="系统配置" style={{ marginBottom: 16 }}>
        <Form
          form={form}
          layout="vertical"
          initialValues={config}
          style={{ maxWidth: 500 }}
        >
          <Form.Item
            name="siteName"
            label="系统名称"
            rules={[{ required: true, message: '请输入系统名称' }]}
          >
            <Input placeholder="请输入系统名称" />
          </Form.Item>
          <Form.Item
            name="language"
            label="默认语言"
            rules={[{ required: true, message: '请选择默认语言' }]}
          >
            <Select options={languageOptions} placeholder="请选择默认语言" />
          </Form.Item>
          <Form.Item
            name="loginFailLockCount"
            label="登录失败锁定次数"
            rules={[{ required: true, message: '请输入登录失败锁定次数' }]}
            extra="连续登录失败达到此次数后，账户将被临时锁定"
          >
            <InputNumber min={1} max={20} placeholder="请输入次数" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="sessionTimeout"
            label="会话超时时间（分钟）"
            rules={[{ required: true, message: '请输入会话超时时间' }]}
            extra="用户无操作后，会话将在指定时间后过期"
          >
            <InputNumber min={5} max={1440} placeholder="请输入分钟数" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="fileUploadMaxSize"
            label="文件上传大小限制（MB）"
            rules={[{ required: true, message: '请输入文件上传大小限制' }]}
            extra="单个文件上传的最大大小"
          >
            <InputNumber min={1} max={100} placeholder="请输入 MB 数" style={{ width: '100%' }} />
          </Form.Item>
          <Divider />
          <Form.Item>
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSave} loading={saving}>
              保存配置
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {/* 外部身份源管理 */}
      <Card
        title="外部身份源"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAddSource}>
            新增
          </Button>
        }
      >
        <Table
          rowKey="id"
          columns={sourceColumns}
          dataSource={sources}
          loading={sourcesLoading}
          pagination={false}
          size="small"
        />
      </Card>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingSource ? '编辑身份源' : '新增身份源'}
        open={modalOpen}
        onOk={handleSourceSubmit}
        onCancel={() => setModalOpen(false)}
        destroyOnClose
      >
        <Form form={sourceForm} layout="vertical" preserve={false}>
          <Form.Item
            name="code"
            label="编码"
            rules={[{ required: true, message: '请输入编码' }]}
          >
            <Input placeholder="如 LARK / WECOM / WECHAT" disabled={!!editingSource} />
          </Form.Item>
          <Form.Item
            name="name"
            label="名称"
            rules={[{ required: true, message: '请输入名称' }]}
          >
            <Input placeholder="如 飞书 / 企业微信" />
          </Form.Item>
          <Form.Item name="sourceType" label="来源类型" rules={[{ required: true }]}>
            <Select options={sourceTypeOptions} placeholder="请选择来源类型" />
          </Form.Item>
          <Form.Item name="tenantKey" label="租户标识">
            <Input placeholder="可选" />
          </Form.Item>
          {editingSource && (
            <Form.Item name="status" label="状态">
              <Select
                options={[
                  { label: '启用', value: 'ENABLED' },
                  { label: '禁用', value: 'DISABLED' },
                ]}
              />
            </Form.Item>
          )}
          <Form.Item name="priority" label="优先级">
            <InputNumber min={0} max={100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="configJson" label="扩展配置 (JSON)">
            <Input.TextArea rows={3} placeholder='{"clientId":"...","clientSecret":"..."}' />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default SettingsPage;
