import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 用户管理占位页面 */
const UserListPage = () => (
  <Card>
    <Title level={3}>用户管理</Title>
    <Paragraph type="secondary">管理系统中的所有用户账户。</Paragraph>
  </Card>
);

export default UserListPage;
