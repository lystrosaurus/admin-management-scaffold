import { useState, useMemo } from 'react';
import { Layout, Menu, Dropdown, Avatar, Space, theme } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  KeyOutlined,
  MenuOutlined,
  TeamOutlined,
  BankOutlined,
  SettingOutlined,
  SafetyOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import type { MenuProps } from 'antd';
import { useAuthStore } from '@/stores/authStore';
import './MainLayout.css';

const { Header, Sider, Content } = Layout;

/** 菜单配置项 */
const menuItems: MenuProps['items'] = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: '仪表盘' },
  { key: '/users', icon: <UserOutlined />, label: '用户管理' },
  { key: '/roles', icon: <KeyOutlined />, label: '角色管理' },
  { key: '/menus', icon: <MenuOutlined />, label: '菜单管理' },
  { key: '/employees', icon: <TeamOutlined />, label: '员工管理' },
  { key: '/org-units', icon: <BankOutlined />, label: '组织管理' },
  { key: '/settings', icon: <SettingOutlined />, label: '系统配置' },
  { key: '/profile/security', icon: <SafetyOutlined />, label: '账号安全' },
];

/** 路径 → 面包屑标题映射 */
const breadcrumbMap: Record<string, string> = {
  '/dashboard': '仪表盘',
  '/users': '用户管理',
  '/roles': '角色管理',
  '/menus': '菜单管理',
  '/employees': '员工管理',
  '/org-units': '组织管理',
  '/settings': '系统配置',
  '/profile/security': '账号安全',
};

/**
 * 主布局组件
 * 包含可折叠侧边栏、顶部导航和内容区域
 */
const MainLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { token: themeToken } = theme.useToken();

  const clearAuth = useAuthStore((s) => s.clearAuth);
  const user = useAuthStore((s) => s.user);

  /** 当前显示的用户名：优先取 user profile，否则取默认值 */
  const username = user?.nickname ?? user?.username ?? '管理员';

  /** 面包屑标题 */
  const breadcrumbTitle = breadcrumbMap[location.pathname] || '页面';

  /** 当前选中的菜单 key */
  const selectedKey = location.pathname;

  /** 用户下拉菜单项 */
  const userMenuItems: MenuProps['items'] = useMemo(
    () => [
      {
        key: 'profile',
        icon: <UserOutlined />,
        label: '个人中心',
        onClick: () => navigate('/profile/security'),
      },
      { type: 'divider' },
      {
        key: 'logout',
        icon: <LogoutOutlined />,
        label: '退出登录',
        danger: true,
        onClick: () => {
          clearAuth();
          navigate('/login', { replace: true });
        },
      },
    ],
    [navigate, clearAuth],
  );

  /** 菜单点击事件：导航到对应路由 */
  const onMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key);
  };

  return (
    <Layout className="main-layout">
      {/* 左侧边栏 */}
      <Sider
        className="main-layout__sider"
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={220}
        style={{ background: themeToken.colorBgContainer }}
      >
        {/* Logo 区域 */}
        <div className="main-layout__logo">
          {collapsed ? 'AM' : 'Admin Management'}
        </div>

        {/* 菜单 */}
        <Menu
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={onMenuClick}
          style={{ borderInlineEnd: 'none' }}
        />
      </Sider>

      {/* 右侧内容区域 */}
      <Layout>
        {/* 顶部 Header */}
        <Header
          className="main-layout__header"
          style={{ background: themeToken.colorBgContainer }}
        >
          {/* 左侧：折叠按钮 + 面包屑 */}
          <Space size="middle">
            <span
              className="main-layout__trigger"
              onClick={() => setCollapsed((prev) => !prev)}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </span>
            <span className="main-layout__breadcrumb">{breadcrumbTitle}</span>
          </Space>

          {/* 右侧：用户信息 */}
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space className="main-layout__user" size={8}>
              <Avatar
                size={28}
                icon={<UserOutlined />}
                style={{ background: themeToken.colorPrimary }}
              />
              <span>{username}</span>
            </Space>
          </Dropdown>
        </Header>

        {/* 内容区域 */}
        <Content className="main-layout__content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
