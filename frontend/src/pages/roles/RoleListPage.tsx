import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 角色管理占位页面 */
const RoleListPage = () => (
  <Card>
    <Title level={3}>角色管理</Title>
    <Paragraph type="secondary">管理系统角色及其权限分配。</Paragraph>
  </Card>
);

export default RoleListPage;
