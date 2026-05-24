import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 系统配置占位页面 */
const SettingsPage = () => (
  <Card>
    <Title level={3}>系统配置</Title>
    <Paragraph type="secondary">管理系统全局配置和参数设置。</Paragraph>
  </Card>
);

export default SettingsPage;
