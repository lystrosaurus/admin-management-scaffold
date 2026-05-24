import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 仪表盘占位页面 */
const DashboardPage = () => (
  <Card>
    <Title level={3}>仪表盘</Title>
    <Paragraph type="secondary">
      欢迎使用 Admin Management 后台管理系统。
    </Paragraph>
  </Card>
);

export default DashboardPage;
