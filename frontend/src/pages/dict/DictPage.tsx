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
import { listDict, createDict, updateDict, deleteDict } from '@/api/dict';
import type { DictVO, DictCreateDTO, DictUpdateDTO, DictQuery } from '@/types/dict';

/** 状态选项 */
const statusOptions = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
];

/** 数据字典页面 */
const DictPage = () => {
  // 列表数据
  const [dataSource, setDataSource] = useState<DictVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<DictQuery>({});

  // 弹窗控制
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('新增字典');
  const [editingDict, setEditingDict] = useState<DictVO | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // 表单
  const [form] = Form.useForm();

  /**
   * 获取数据字典列表
   */
  const fetchDict = useCallback(async () => {
    setLoading(true);
    try {
      const params: DictQuery = {
        page: current,
        size: pageSize,
        ...searchParams,
      };
      const result = await listDict(params);
      setDataSource(result.items);
      setTotal(result.total);
    } catch (error) {
      message.error((error as Error).message || '获取数据字典列表失败');
    } finally {
      setLoading(false);
    }
  }, [current, pageSize, searchParams]);

  useEffect(() => {
    fetchDict();
  }, [fetchDict]);

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    setCurrent(1);
    setSearchParams({
      dictType: form.getFieldValue('searchDictType'),
    });
  };

  /**
   * 处理重置
   */
  const handleReset = () => {
    form.resetFields(['searchDictType']);
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
    setModalTitle('新增字典');
    setEditingDict(null);
    form.resetFields();
    setModalVisible(true);
  };

  /**
   * 打开编辑弹窗
   */
  const handleEdit = (record: DictVO) => {
    setModalTitle('编辑字典');
    setEditingDict(record);
    form.setFieldsValue({
      dictType: record.dictType,
      dictLabel: record.dictLabel,
      dictValue: record.dictValue,
      sortOrder: record.sortOrder,
      status: record.status,
      remark: record.remark,
    });
    setModalVisible(true);
  };

  /**
   * 处理删除
   */
  const handleDelete = async (id: number) => {
    try {
      await deleteDict(id);
      message.success('删除成功');
      fetchDict();
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

      if (editingDict) {
        // 编辑字典
        const updateData: DictUpdateDTO = {
          dictType: values.dictType,
          dictLabel: values.dictLabel,
          dictValue: values.dictValue,
          sortOrder: values.sortOrder,
          status: values.status,
          remark: values.remark,
        };
        await updateDict(editingDict.id, updateData);
        message.success('更新成功');
      } else {
        // 新增字典
        const createData: DictCreateDTO = {
          dictType: values.dictType,
          dictLabel: values.dictLabel,
          dictValue: values.dictValue,
          sortOrder: values.sortOrder,
          status: values.status,
          remark: values.remark,
        };
        await createDict(createData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchDict();
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

  /** 表格列定义 */
  const columns = [
    {
      title: '字典类型',
      dataIndex: 'dictType',
      key: 'dictType',
      width: 140,
    },
    {
      title: '字典标签',
      dataIndex: 'dictLabel',
      key: 'dictLabel',
      width: 140,
    },
    {
      title: '字典值',
      dataIndex: 'dictValue',
      key: 'dictValue',
      width: 140,
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 80,
      render: (text: number | undefined) => (text ?? 0).toString(),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => renderStatusTag(status),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200,
      render: (text: string | undefined) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: unknown, record: DictVO) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该字典吗？"
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
        <Form.Item name="searchDictType">
          <Input placeholder="字典类型" allowClear style={{ width: 180 }} />
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
        <Form form={form} layout="vertical" initialValues={{ status: 'ENABLED', sortOrder: 0 }}>
          <Form.Item
            name="dictType"
            label="字典类型"
            rules={[{ required: true, message: '请输入字典类型' }]}
          >
            <Input placeholder="请输入字典类型" />
          </Form.Item>
          <Form.Item
            name="dictLabel"
            label="字典标签"
            rules={[{ required: true, message: '请输入字典标签' }]}
          >
            <Input placeholder="请输入字典标签" />
          </Form.Item>
          <Form.Item
            name="dictValue"
            label="字典值"
            rules={[{ required: true, message: '请输入字典值' }]}
          >
            <Input placeholder="请输入字典值" />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber placeholder="请输入排序" style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={statusOptions} />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea placeholder="请输入备注" rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default DictPage;
