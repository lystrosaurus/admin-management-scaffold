import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 菜单管理占位页面 */
const MenuListPage = () => (
  <Card>
    <Title level={3}>菜单管理</Title>
    <Paragraph type="secondary">配置系统菜单和导航结构。</Paragraph>
  </Card>
);

export default MenuListPage;
