import { useState, useEffect, useCallback } from 'react';
import { Card, Typography, Table, Button, Space, Tag, Input, Select, Form, Modal, TreeSelect, InputNumber, message, Popconfirm } from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined, BankOutlined, ApartmentOutlined, TeamOutlined } from '@ant-design/icons';
import { listOrgUnits, createOrgUnit, updateOrgUnit, deleteOrgUnit } from '@/api/orgUnit';
import type { OrgUnit, CreateOrgUnitRequest, UpdateOrgUnitRequest, OrgUnitQueryParams } from '@/types/orgUnit';
import type { Status } from '@/types/api';
import type { TreeSelectData } from '@/types/components';

const { Title } = Typography;

// 组织类型映射
const orgTypeMap = {
  COMPANY: { label: '公司', color: 'blue', icon: <BankOutlined /> },
  DEPARTMENT: { label: '部门', color: 'green', icon: <ApartmentOutlined /> },
  TEAM: { label: '团队', color: 'orange', icon: <TeamOutlined /> },
};

// 状态映射
const statusMap = {
  ACTIVE: { label: '启用', color: 'green' },
  INACTIVE: { label: '停用', color: 'red' },
};

/** 组织管理页面 */
const OrgUnitListPage = () => {
  const [orgUnits, setOrgUnits] = useState<OrgUnit[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingOrgUnit, setEditingOrgUnit] = useState<OrgUnit | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [treeData, setTreeData] = useState<TreeSelectData[]>([]);

  // 加载组织列表
  const loadOrgUnits = useCallback(async (params?: OrgUnitQueryParams) => {
    setLoading(true);
    try {
      const data = await listOrgUnits(params);
      setOrgUnits(data);
      // 构建树形数据，用于TreeSelect
      const buildTreeData = (orgUnits: OrgUnit[]): TreeSelectData[] => {
        return orgUnits.map(org => ({
          value: org.id,
          label: org.name,
          children: org.children ? buildTreeData(org.children) : undefined,
        }));
      };
      setTreeData(buildTreeData(data));
    } catch (error) {
      message.error('加载组织列表失败');
      console.error('加载组织列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // 初始化加载
  useEffect(() => {
    const initializeData = async () => {
      await loadOrgUnits();
    };
    initializeData();
  }, [loadOrgUnits]);

  // 搜索
  const handleSearch = (values: OrgUnitQueryParams) => {
    loadOrgUnits(values);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    loadOrgUnits();
  };

  // 新增组织
  const handleAdd = (parentId?: number) => {
    setEditingOrgUnit(null);
    form.resetFields();
    if (parentId) {
      form.setFieldsValue({ parentId });
    }
    setModalVisible(true);
  };

  // 编辑组织
  const handleEdit = (orgUnit: OrgUnit) => {
    setEditingOrgUnit(orgUnit);
    form.setFieldsValue({
      parentId: orgUnit.parentId,
      name: orgUnit.name,
      code: orgUnit.code,
      type: orgUnit.type,
      leaderId: orgUnit.leaderId,
      sort: orgUnit.sort,
      status: orgUnit.status,
    });
    setModalVisible(true);
  };

  // 删除组织
  const handleDelete = async (id: number) => {
    try {
      await deleteOrgUnit(id);
      message.success('删除成功');
      loadOrgUnits();
    } catch (error) {
      message.error('删除失败');
      console.error('删除组织失败:', error);
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingOrgUnit) {
        // 更新组织
        await updateOrgUnit(editingOrgUnit.id, values as UpdateOrgUnitRequest);
        message.success('更新成功');
      } else {
        // 创建组织
        await createOrgUnit(values as CreateOrgUnitRequest);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadOrgUnits();
    } catch (error) {
      message.error('操作失败');
      console.error('提交组织表单失败:', error);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '组织名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: '编码',
      dataIndex: 'code',
      key: 'code',
      width: 120,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: keyof typeof orgTypeMap) => {
        const { label, color, icon } = orgTypeMap[type];
        return <Tag color={color} icon={icon}>{label}</Tag>;
      },
    },
    {
      title: '负责人',
      dataIndex: 'leaderName',
      key: 'leaderName',
      width: 120,
      ellipsis: true,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
      sorter: (a: OrgUnit, b: OrgUnit) => a.sort - b.sort,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: Status) => {
        const { label, color } = statusMap[status];
        return <Tag color={color}>{label}</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: OrgUnit) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record.id)}
          >
            添加子组织
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确定要删除该组织吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={3} style={{ margin: 0 }}>组织管理</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>
          新增组织
        </Button>
      </div>

      {/* 搜索表单 */}
      <Form form={searchForm} layout="inline" onFinish={handleSearch} style={{ marginBottom: 16 }}>
        <Form.Item name="name" label="组织名称">
          <Input placeholder="请输入组织名称" />
        </Form.Item>
        <Form.Item name="code" label="组织编码">
          <Input placeholder="请输入组织编码" />
        </Form.Item>
        <Form.Item name="type" label="类型">
          <Select placeholder="请选择类型" allowClear style={{ width: 120 }}>
            <Select.Option value="COMPANY">公司</Select.Option>
            <Select.Option value="DEPARTMENT">部门</Select.Option>
            <Select.Option value="TEAM">团队</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item name="status" label="状态">
          <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
            <Select.Option value="ACTIVE">启用</Select.Option>
            <Select.Option value="INACTIVE">停用</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" icon={<SearchOutlined />} htmlType="submit">
              搜索
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form.Item>
      </Form>

      {/* 组织表格 */}
      <Table
        columns={columns}
        dataSource={orgUnits}
        loading={loading}
        rowKey="id"
        defaultExpandAllRows
        pagination={false}
        scroll={{ x: 1000 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingOrgUnit ? '编辑组织' : '新增组织'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical" initialValues={{ status: 'ACTIVE', sort: 0 }}>
          <Form.Item name="parentId" label="上级组织">
            <TreeSelect
              treeData={treeData}
              placeholder="请选择上级组织（可选）"
              allowClear
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item name="name" label="组织名称" rules={[{ required: true, message: '请输入组织名称' }]}>
            <Input placeholder="请输入组织名称" />
          </Form.Item>
          <Form.Item name="code" label="组织编码" rules={[{ required: true, message: '请输入组织编码' }]}>
            <Input placeholder="请输入组织编码" />
          </Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true, message: '请选择类型' }]}>
            <Select placeholder="请选择类型">
              <Select.Option value="COMPANY">公司</Select.Option>
              <Select.Option value="DEPARTMENT">部门</Select.Option>
              <Select.Option value="TEAM">团队</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="leaderId" label="负责人">
            <Input placeholder="请输入负责人用户ID（可选）" />
          </Form.Item>
          <Form.Item name="sort" label="排序">
            <InputNumber min={0} max={9999} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select placeholder="请选择状态">
              <Select.Option value="ACTIVE">启用</Select.Option>
              <Select.Option value="INACTIVE">停用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default OrgUnitListPage;