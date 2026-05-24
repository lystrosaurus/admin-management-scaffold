import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 组织管理占位页面 */
const OrgUnitListPage = () => (
  <Card>
    <Title level={3}>组织管理</Title>
    <Paragraph type="secondary">管理组织架构和部门层级。</Paragraph>
  </Card>
);

export default OrgUnitListPage;
