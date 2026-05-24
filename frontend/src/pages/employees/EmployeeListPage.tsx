import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 员工管理占位页面 */
const EmployeeListPage = () => (
  <Card>
    <Title level={3}>员工管理</Title>
    <Paragraph type="secondary">管理系统员工信息和组织归属。</Paragraph>
  </Card>
);

export default EmployeeListPage;
