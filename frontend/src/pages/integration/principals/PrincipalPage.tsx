import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Select,
  Tag,
  Modal,
  Form,
  InputNumber,
  Input,
  message,
  Popconfirm,
  type TablePaginationConfig,
} from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import {
  listPrincipals,
  createPrincipal,
  updatePrincipal,
  deletePrincipal,
  listSources,
} from '@/api/integration';
import type {
  PrincipalVO,
  PrincipalCreateRequest,
  PrincipalUpdateRequest,
  PrincipalQueryParams,
  SourceVO,
} from '@/types/integration';
import dayjs from 'dayjs';

/** 主体类型选项 */
const principalTypeOptions = [
  { label: '用户', value: 'USER' },
  { label: '组织', value: 'ORG' },
];

/** 状态选项（后端 ACTIVE / INACTIVE） */
const statusOptions = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
];

/** 关联状态选项 */
const linkStatusOptions = [
  { label: '未关联', value: 'UNLINKED' },
  { label: '自动关联', value: 'AUTO_LINKED' },
  { label: '手动关联', value: 'MANUAL_LINKED' },
  { label: '冲突', value: 'CONFLICT' },
];

/** 映射目标类型选项 */
const canonicalTypeOptions = [
  { label: '员工', value: 'EMPLOYEE' },
  { label: '组织单元', value: 'ORG_UNIT' },
];

/** 外部主体管理页面 */
const PrincipalPage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<PrincipalVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<PrincipalQueryParams>({});
  const [sourceOptions, setSourceOptions] = useState<{ label: string; value: number }[]>([]);

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增外部主体');
  const [editingPrincipal, setEditingPrincipal] = useState<PrincipalVO | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取外部主体列表
   */
  const fetchPrincipals = useCallback(async () => {
    setLoading(true);
    try {
      const result = await listPrincipals(searchParams);
      setDataSource(result);
    } catch (error) {
      message.error((error as Error).message || '获取外部主体列表失败');
    } finally {
      setLoading(false);
    }
  }, [searchParams]);

  useEffect(() => {
    // 首屏加载：setState 仅在 await 之后执行，并带取消保护
    let ignore = false;
    // 身份源下拉选项（失败忽略，下拉为空不影响主流程）
    void (async () => {
      try {
        const sources = await listSources();
        if (ignore) return;
        setSourceOptions(sources.map((s: SourceVO) => ({ label: s.name, value: s.id })));
      } catch {
        // 忽略
      }
    })();
    void (async () => {
      try {
        const result = await listPrincipals(searchParams);
        if (ignore) return;
        setDataSource(result);
      } catch (error) {
        if (!ignore) message.error((error as Error).message || '获取外部主体列表失败');
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
      sourceId: form.getFieldValue('searchSourceId'),
      linkStatus: form.getFieldValue('searchLinkStatus'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    setLoading(true);
    form.resetFields(['searchSourceId', 'searchLinkStatus']);
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
    setModalTitle('新增外部主体');
    setEditingPrincipal(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: PrincipalVO) => {
    setModalTitle('编辑外部主体');
    setEditingPrincipal(record);
    form.setFieldsValue({
      displayName: record.displayName,
      status: record.status,
      canonicalType: record.canonicalType,
      canonicalId: record.canonicalId,
      linkStatus: record.linkStatus,
    });
    setModalVisible(true);
  };

  /**
   * 处理删除
   */
  const handleDelete = async (id: number) => {
    try {
      await deletePrincipal(id);
      message.success('删除成功');
      fetchPrincipals();
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

      if (editingPrincipal) {
        // 编辑外部主体
        const updateData: PrincipalUpdateRequest = {
          displayName: values.displayName,
          status: values.status,
          canonicalType: values.canonicalType,
          canonicalId: values.canonicalId,
          linkStatus: values.linkStatus,
        };
        await updatePrincipal(editingPrincipal.id, updateData);
        message.success('更新成功');
      } else {
        // 新增外部主体
        const createData: PrincipalCreateRequest = {
          sourceId: values.sourceId,
          principalType: values.principalType,
          externalKey: values.externalKey,
          displayName: values.displayName,
          rawPayloadJson: values.rawPayloadJson,
        };
        await createPrincipal(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchPrincipals();
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
    const color = status === 'ACTIVE' ? 'green' : 'red';
    const text = status === 'ACTIVE' ? '启用' : '禁用';
    return <Tag color={color}>{text}</Tag>;
  };

  /**
   * 渲染关联状态标签
   */
  const renderLinkStatusTag = (linkStatus: string | undefined) => {
    if (!linkStatus) return '-';
    const option = linkStatusOptions.find((o) => o.value === linkStatus);
    const color =
      linkStatus === 'CONFLICT' ? 'red' : linkStatus === 'UNLINKED' ? 'default' : 'blue';
    return <Tag color={color}>{option ? option.label : linkStatus}</Tag>;
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
      title: '外部系统',
      dataIndex: 'sourceName',
      key: 'sourceName',
      width: 140,
      render: (text: string | undefined, record: PrincipalVO) =>
        text || `ID:${record.sourceId}`,
    },
    {
      title: '主体类型',
      dataIndex: 'principalType',
      key: 'principalType',
      width: 100,
      render: (text: string) =>
        principalTypeOptions.find((o) => o.value === text)?.label || text,
    },
    {
      title: '外部系统主键',
      dataIndex: 'externalKey',
      key: 'externalKey',
      width: 160,
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 160,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => renderStatusTag(status),
    },
    {
      title: '关联状态',
      dataIndex: 'linkStatus',
      key: 'linkStatus',
      width: 110,
      render: (linkStatus: string | undefined) => renderLinkStatusTag(linkStatus),
    },
    {
      title: '映射目标',
      dataIndex: 'canonicalType',
      key: 'canonicalType',
      width: 140,
      render: (_: unknown, record: PrincipalVO) => {
        if (!record.canonicalType) return '-';
        const label =
          canonicalTypeOptions.find((o) => o.value === record.canonicalType)?.label ||
          record.canonicalType;
        return `${label} (${record.canonicalId})`;
      },
    },
    {
      title: '最后同步时间',
      dataIndex: 'lastSyncAt',
      key: 'lastSyncAt',
      width: 180,
      render: (text: string | undefined) =>
        text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: PrincipalVO) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该外部主体吗？"
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
        <Form.Item name="searchSourceId">
          <Select
            placeholder="外部系统"
            allowClear
            options={sourceOptions}
            style={{ width: 160 }}
          />
        </Form.Item>
        <Form.Item name="searchLinkStatus">
          <Select
            placeholder="关联状态"
            allowClear
            options={linkStatusOptions}
            style={{ width: 140 }}
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
          新增外部主体
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
        scroll={{ x: 1400 }}
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
          initialValues={{ principalType: 'USER', status: 'ACTIVE', linkStatus: 'UNLINKED' }}
        >
          {!editingPrincipal && (
            <>
              <Form.Item
                name="sourceId"
                label="外部系统"
                rules={[{ required: true, message: '请选择外部系统' }]}
              >
                <Select placeholder="请选择外部系统" options={sourceOptions} />
              </Form.Item>
              <Form.Item
                name="principalType"
                label="主体类型"
                rules={[{ required: true, message: '请选择主体类型' }]}
              >
                <Select options={principalTypeOptions} />
              </Form.Item>
              <Form.Item
                name="externalKey"
                label="外部系统主键"
                rules={[{ required: true, message: '请输入外部系统主键' }]}
              >
                <Input placeholder="请输入外部系统主键" />
              </Form.Item>
            </>
          )}
          <Form.Item
            name="displayName"
            label="显示名称"
            rules={[{ required: true, message: '请输入显示名称' }]}
          >
            <Input placeholder="请输入显示名称" />
          </Form.Item>
          {!editingPrincipal && (
            <Form.Item name="rawPayloadJson" label="原始数据(JSON)">
              <Input.TextArea placeholder="请输入原始数据 JSON" rows={3} />
            </Form.Item>
          )}
          {editingPrincipal && (
            <>
              <Form.Item name="status" label="状态">
                <Select options={statusOptions} />
              </Form.Item>
              <Form.Item name="linkStatus" label="关联状态">
                <Select options={linkStatusOptions} />
              </Form.Item>
              <Form.Item name="canonicalType" label="映射目标类型">
                <Select placeholder="请选择映射目标类型" allowClear options={canonicalTypeOptions} />
              </Form.Item>
              <Form.Item name="canonicalId" label="映射目标ID">
                <InputNumber placeholder="请输入映射目标ID" style={{ width: '100%' }} />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </Card>
  );
};

export default PrincipalPage;
