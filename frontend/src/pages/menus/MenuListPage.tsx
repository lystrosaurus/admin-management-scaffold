import { useState, useEffect, useCallback } from 'react';
import { Card, Typography, Table, Button, Space, Tag, Input, Select, Form, Modal, TreeSelect, InputNumber, message, Popconfirm } from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined, FolderOutlined, MenuOutlined, AppstoreOutlined } from '@ant-design/icons';
import { listMenus, createMenu, updateMenu, deleteMenu } from '@/api/menu';
import type { Menu, CreateMenuRequest, UpdateMenuRequest, MenuQueryParams } from '@/types/menu';
import type { Status } from '@/types/api';
import type { TreeSelectData } from '@/types/components';

const { Title } = Typography;

// 菜单类型映射
const menuTypeMap = {
  DIRECTORY: { label: '目录', color: 'blue', icon: <FolderOutlined /> },
  MENU: { label: '菜单', color: 'green', icon: <MenuOutlined /> },
  BUTTON: { label: '按钮', color: 'orange', icon: <AppstoreOutlined /> },
};

// 状态映射
const statusMap = {
  ACTIVE: { label: '启用', color: 'green' },
  INACTIVE: { label: '停用', color: 'red' },
};

/** 菜单管理页面 */
const MenuListPage = () => {
  const [menus, setMenus] = useState<Menu[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingMenu, setEditingMenu] = useState<Menu | null>(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [treeData, setTreeData] = useState<TreeSelectData[]>([]);

  // 加载菜单列表
  const loadMenus = useCallback(async (params?: MenuQueryParams) => {
    setLoading(true);
    try {
      const data = await listMenus(params);
      setMenus(data);
      // 构建树形数据，用于TreeSelect
      const buildTreeData = (menus: Menu[]): TreeSelectData[] => {
        return menus.map(menu => ({
          value: menu.id,
          label: menu.name,
          children: menu.children ? buildTreeData(menu.children) : undefined,
        }));
      };
      setTreeData(buildTreeData(data));
    } catch (error) {
      message.error('加载菜单列表失败');
      console.error('加载菜单列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // 初始化加载
  useEffect(() => {
    const initializeData = async () => {
      await loadMenus();
    };
    initializeData();
  }, [loadMenus]);

  // 搜索
  const handleSearch = (values: MenuQueryParams) => {
    loadMenus(values);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    loadMenus();
  };

  // 新增菜单
  const handleAdd = (parentId?: number) => {
    setEditingMenu(null);
    form.resetFields();
    if (parentId) {
      form.setFieldsValue({ parentId });
    }
    setModalVisible(true);
  };

  // 编辑菜单
  const handleEdit = (menu: Menu) => {
    setEditingMenu(menu);
    form.setFieldsValue({
      parentId: menu.parentId,
      name: menu.name,
      type: menu.type,
      path: menu.path,
      component: menu.component,
      icon: menu.icon,
      permission: menu.permission,
      sort: menu.sort,
      status: menu.status,
    });
    setModalVisible(true);
  };

  // 删除菜单
  const handleDelete = async (id: number) => {
    try {
      await deleteMenu(id);
      message.success('删除成功');
      loadMenus();
    } catch (error) {
      message.error('删除失败');
      console.error('删除菜单失败:', error);
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingMenu) {
        // 更新菜单
        await updateMenu(editingMenu.id, values as UpdateMenuRequest);
        message.success('更新成功');
      } else {
        // 创建菜单
        await createMenu(values as CreateMenuRequest);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadMenus();
    } catch (error) {
      message.error('操作失败');
      console.error('提交菜单表单失败:', error);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '菜单名称',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: keyof typeof menuTypeMap) => {
        const { label, color, icon } = menuTypeMap[type];
        return <Tag color={color} icon={icon}>{label}</Tag>;
      },
    },
    {
      title: '路径',
      dataIndex: 'path',
      key: 'path',
      width: 150,
      ellipsis: true,
    },
    {
      title: '组件',
      dataIndex: 'component',
      key: 'component',
      width: 200,
      ellipsis: true,
    },
    {
      title: '权限标识',
      dataIndex: 'permission',
      key: 'permission',
      width: 150,
      ellipsis: true,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
      sorter: (a: Menu, b: Menu) => a.sort - b.sort,
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
      render: (_: unknown, record: Menu) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record.id)}
          >
            添加子菜单
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
            description="确定要删除该菜单吗？"
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
        <Title level={3} style={{ margin: 0 }}>菜单管理</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>
          新增菜单
        </Button>
      </div>

      {/* 搜索表单 */}
      <Form form={searchForm} layout="inline" onFinish={handleSearch} style={{ marginBottom: 16 }}>
        <Form.Item name="name" label="菜单名称">
          <Input placeholder="请输入菜单名称" />
        </Form.Item>
        <Form.Item name="type" label="类型">
          <Select placeholder="请选择类型" allowClear style={{ width: 120 }}>
            <Select.Option value="DIRECTORY">目录</Select.Option>
            <Select.Option value="MENU">菜单</Select.Option>
            <Select.Option value="BUTTON">按钮</Select.Option>
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

      {/* 菜单表格 */}
      <Table
        columns={columns}
        dataSource={menus}
        loading={loading}
        rowKey="id"
        defaultExpandAllRows
        pagination={false}
        scroll={{ x: 1200 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingMenu ? '编辑菜单' : '新增菜单'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical" initialValues={{ status: 'ACTIVE', sort: 0 }}>
          <Form.Item name="parentId" label="上级菜单">
            <TreeSelect
              treeData={treeData}
              placeholder="请选择上级菜单（可选）"
              allowClear
              treeDefaultExpandAll
            />
          </Form.Item>
          <Form.Item name="name" label="菜单名称" rules={[{ required: true, message: '请输入菜单名称' }]}>
            <Input placeholder="请输入菜单名称" />
          </Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true, message: '请选择类型' }]}>
            <Select placeholder="请选择类型">
              <Select.Option value="DIRECTORY">目录</Select.Option>
              <Select.Option value="MENU">菜单</Select.Option>
              <Select.Option value="BUTTON">按钮</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => prevValues.type !== currentValues.type}
          >
            {({ getFieldValue }) => {
              const type = getFieldValue('type');
              return type === 'MENU' ? (
                <>
                  <Form.Item name="path" label="路径" rules={[{ required: true, message: '请输入路径' }]}>
                    <Input placeholder="请输入路径" />
                  </Form.Item>
                  <Form.Item name="component" label="组件路径" rules={[{ required: true, message: '请输入组件路径' }]}>
                    <Input placeholder="请输入组件路径" />
                  </Form.Item>
                </>
              ) : null;
            }}
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => prevValues.type !== currentValues.type}
          >
            {({ getFieldValue }) => {
              const type = getFieldValue('type');
              return type !== 'BUTTON' ? (
                <Form.Item name="icon" label="图标">
                  <Input placeholder="请输入图标（可选）" />
                </Form.Item>
              ) : null;
            }}
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => prevValues.type !== currentValues.type}
          >
            {({ getFieldValue }) => {
              const type = getFieldValue('type');
              return type === 'BUTTON' ? (
                <Form.Item name="permission" label="权限标识">
                  <Input placeholder="请输入权限标识" />
                </Form.Item>
              ) : null;
            }}
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

export default MenuListPage;