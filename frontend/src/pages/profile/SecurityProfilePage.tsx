import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/** 账号安全占位页面 */
const SecurityProfilePage = () => (
  <Card>
    <Title level={3}>账号安全</Title>
    <Paragraph type="secondary">管理密码、二次验证和登录设备。</Paragraph>
  </Card>
);

export default SecurityProfilePage;
