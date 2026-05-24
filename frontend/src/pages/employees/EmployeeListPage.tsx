import { useState, useEffect, useCallback } from 'react';
import { Card, Typography, Table, Button, Space, Tag, Input, Select, Form, Modal, TreeSelect, message, Popconfirm } from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { listEmployees, createEmployee, updateEmployee, deleteEmployee } from '@/api/employee';
import { listOrgUnits } from '@/api/orgUnit';
import type { Employee, CreateEmployeeRequest, UpdateEmployeeRequest, EmployeeQueryParams } from '@/types/employee';
import type { OrgUnit } from '@/types/orgUnit';
import type { Status } from '@/types/api';
import type { TreeSelectData, TablePagination } from '@/types/components';

const { Title } = Typography;

// 状态映射
const statusMap = {
  ACTIVE: { label: '启用', color: 'green' },
  INACTIVE: { label: '停用', color: 'red' },
};

/** 员工管理页面 */
const EmployeeListPage = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [orgTreeData, setOrgTreeData] = useState<TreeSelectData[]>([]);
  const [pagination, setPagination] = useState<TablePagination>({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 加载员工列表
  const loadEmployees = useCallback(async (params?: EmployeeQueryParams) => {
    setLoading(true);
    try {
      const queryParams = {
        current: pagination.current,
        size: pagination.pageSize,
        ...params,
      };
      const data = await listEmployees(queryParams);
      setEmployees(data.records);
      setPagination(prev => ({
        ...prev,
        total: data.total,
        current: data.current,
      }));
    } catch (error) {
      message.error('加载员工列表失败');
      console.error('加载员工列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [pagination.current, pagination.pageSize]);

  // 加载组织树数据
  const loadOrgTreeData = useCallback(async () => {
    try {
      const data = await listOrgUnits();
      // 构建树形数据，用于TreeSelect
      const buildTreeData = (orgUnits: OrgUnit[]): TreeSelectData[] => {
        return orgUnits.map(org => ({
          value: org.id,
          label: org.name,
          children: org.children ? buildTreeData(org.children) : undefined,
        }));
      };
      setOrgTreeData(buildTreeData(data));
    } catch (error) {
      console.error('加载组织树数据失败:', error);
    }
  }, []);

  // 初始化加载
  useEffect(() => {
    const initializeData = async () => {
      await Promise.all([loadEmployees(), loadOrgTreeData()]);
    };
    initializeData();
  }, [loadEmployees, loadOrgTreeData]);

  // 搜索
  const handleSearch = (values: EmployeeQueryParams) => {
    setPagination(prev => ({ ...prev, current: 1 }));
    loadEmployees({ ...values, current: 1 });
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    setPagination(prev => ({ ...prev, current: 1 }));
    loadEmployees({ current: 1 });
  };

  // 分页变化
  const handleTableChange = (pagination: { current?: number; pageSize?: number }) => {
    setPagination(prev => ({
      ...prev,
      current: pagination.current || 1,
      pageSize: pagination.pageSize || 10,
    }));
    loadEmployees({
      current: pagination.current,
      size: pagination.pageSize,
    });
  };

  // 新增员工
  const handleAdd = () => {
    setEditingEmployee(null);
    form.resetFields();
    setModalVisible(true);
  };

  // 编辑员工
  const handleEdit = (employee: Employee) => {
    setEditingEmployee(employee);
    form.setFieldsValue({
      userId: employee.userId,
      name: employee.name,
      employeeNo: employee.employeeNo,
      orgUnitId: employee.orgUnitId,
      position: employee.position,
      email: employee.email,
      phone: employee.phone,
      status: employee.status,
    });
    setModalVisible(true);
  };

  // 删除员工
  const handleDelete = async (id: number) => {
    try {
      await deleteEmployee(id);
      message.success('删除成功');
      loadEmployees();
    } catch (error) {
      message.error('删除失败');
      console.error('删除员工失败:', error);
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingEmployee) {
        // 更新员工
        await updateEmployee(editingEmployee.id, values as UpdateEmployeeRequest);
        message.success('更新成功');
      } else {
        // 创建员工
        await createEmployee(values as CreateEmployeeRequest);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadEmployees();
    } catch (error) {
      message.error('操作失败');
      console.error('提交员工表单失败:', error);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '工号',
      dataIndex: 'employeeNo',
      key: 'employeeNo',
      width: 100,
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 120,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '所属组织',
      dataIndex: 'orgUnitName',
      key: 'orgUnitName',
      width: 150,
      ellipsis: true,
    },
    {
      title: '职位',
      dataIndex: 'position',
      key: 'position',
      width: 150,
      ellipsis: true,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      ellipsis: true,
    },
    {
      title: '手机',
      dataIndex: 'phone',
      key: 'phone',
      width: 150,
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
      width: 150,
      render: (_: unknown, record: Employee) => (
        <Space size="middle">
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
            description="确定要删除该员工吗？"
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
        <Title level={3} style={{ margin: 0 }}>员工管理</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增员工
        </Button>
      </div>

      {/* 搜索表单 */}
      <Form form={searchForm} layout="inline" onFinish={handleSearch} style={{ marginBottom: 16 }}>
        <Form.Item name="name" label="姓名">
          <Input placeholder="请输入姓名" />
        </Form.Item>
        <Form.Item name="employeeNo" label="工号">
          <Input placeholder="请输入工号" />
        </Form.Item>
        <Form.Item name="orgUnitId" label="组织">
          <TreeSelect
            treeData={orgTreeData}
            placeholder="请选择组织"
            allowClear
            treeDefaultExpandAll
            style={{ width: 200 }}
          />
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

      {/* 员工表格 */}
      <Table
        columns={columns}
        dataSource={employees}
        loading={loading}
        rowKey="id"
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={handleTableChange}
        scroll={{ x: 1200 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingEmployee ? '编辑员工' : '新增员工'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical" initialValues={{ status: 'ACTIVE' }}>
          <Form.Item
            name="userId"
            label="关联用户"
            rules={[{ required: !editingEmployee, message: '请输入用户ID' }]}
          >
            <Input placeholder="请输入用户ID" disabled={!!editingEmployee} />
          </Form.Item>
          <Form.Item name="name" label="姓名" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item name="employeeNo" label="工号" rules={[{ required: true, message: '请输入工号' }]}>
            <Input placeholder="请输入工号" />
          </Form.Item>
          <Form.Item name="orgUnitId" label="所属组织">
            <TreeSelect
              treeData={orgTreeData}
              placeholder="请选择组织"
              allowClear
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item name="position" label="职位">
            <Input placeholder="请输入职位" />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="phone" label="手机">
            <Input placeholder="请输入手机号" />
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

export default EmployeeListPage;