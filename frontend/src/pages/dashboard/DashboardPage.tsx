import { Card, Row, Col, Statistic, Button, Space, Typography, List } from 'antd';
import {
  UserOutlined,
  KeyOutlined,
  TeamOutlined,
  BankOutlined,
  SettingOutlined,
  MenuOutlined,
  ClockCircleOutlined,
  CodeOutlined,
  RocketOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title, Text, Paragraph } = Typography;

/** Mock 统计数据 */
const stats = {
  userCount: 128,
  roleCount: 12,
  employeeCount: 96,
  orgUnitCount: 8,
};

/** 快速入口配置 */
const quickLinks = [
  { title: '用户管理', icon: <UserOutlined />, path: '/users', color: '#1677ff' },
  { title: '角色管理', icon: <KeyOutlined />, path: '/roles', color: '#52c41a' },
  { title: '菜单管理', icon: <MenuOutlined />, path: '/menus', color: '#faad14' },
  { title: '系统配置', icon: <SettingOutlined />, path: '/settings', color: '#722ed1' },
];

/** 系统信息 */
const systemInfoItems = [
  { label: '运行时间', value: '15 天 8 小时', icon: <ClockCircleOutlined /> },
  { label: '版本号', value: 'v1.0.0', icon: <RocketOutlined /> },
  { label: '技术栈', value: 'React 19 + Spring Boot 4 + Ant Design 6', icon: <CodeOutlined /> },
];

/** 仪表盘页面 */
const DashboardPage = () => {
  const navigate = useNavigate();

  return (
    <div>
      {/* 欢迎信息 */}
      <Card style={{ marginBottom: 16 }}>
        <Title level={4} style={{ marginBottom: 4 }}>
          欢迎使用 Admin Management 后台管理系统
        </Title>
        <Paragraph type="secondary" style={{ marginBottom: 0 }}>
          这是一个基于 React + Spring Boot 的后台管理系统脚手架，提供用户、角色、权限、菜单、组织等完整的后台管理功能。
        </Paragraph>
      </Card>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="用户总数"
              value={stats.userCount}
              prefix={<UserOutlined style={{ color: '#1677ff' }} />}
              suffix="人"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="角色总数"
              value={stats.roleCount}
              prefix={<KeyOutlined style={{ color: '#52c41a' }} />}
              suffix="个"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="员工总数"
              value={stats.employeeCount}
              prefix={<TeamOutlined style={{ color: '#faad14' }} />}
              suffix="人"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="组织总数"
              value={stats.orgUnitCount}
              prefix={<BankOutlined style={{ color: '#722ed1' }} />}
              suffix="个"
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        {/* 快速入口 */}
        <Col xs={24} lg={12}>
          <Card title="快速入口">
            <Space size="middle" wrap>
              {quickLinks.map((item) => (
                <Button
                  key={item.path}
                  icon={item.icon}
                  size="large"
                  onClick={() => navigate(item.path)}
                >
                  {item.title}
                </Button>
              ))}
            </Space>
          </Card>
        </Col>

        {/* 系统信息 */}
        <Col xs={24} lg={12}>
          <Card title="系统信息">
            <List
              dataSource={systemInfoItems}
              renderItem={(item) => (
                <List.Item>
                  <Space>
                    <span style={{ color: '#1677ff' }}>{item.icon}</span>
                    <Text strong>{item.label}：</Text>
                    <Text>{item.value}</Text>
                  </Space>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;
