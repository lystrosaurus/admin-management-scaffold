import { useState } from 'react';
import { Form, Input, Button, Checkbox, Typography, Divider, Space, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { FeishuIcon, WeComIcon, WechatIcon } from '@/components/OAuthIcons';
import { useAuthStore } from '@/stores/authStore';
import type { FormProps } from 'antd';
import './LoginPage.css';

const { Title, Text } = Typography;

/** 登录表单字段类型 */
interface LoginFormValues {
  username: string;
  password: string;
  remember: boolean;
}

/** OAuth 三方登录提供方配置 */
const oauthProviders = [
  { key: 'feishu', name: '飞书', color: '#00D6B9', icon: <FeishuIcon size={22} /> },
  { key: 'wecom', name: '企业微信', color: '#07C160', icon: <WeComIcon size={22} /> },
  { key: 'wechat', name: '微信', color: '#07C160', icon: <WechatIcon size={22} /> },
];

/**
 * 登录页面
 * 包含用户名密码表单 + OAuth 三方登录入口
 */
const LoginPage = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  /** 表单提交：调用登录 API */
  const onFinish: FormProps<LoginFormValues>['onFinish'] = async (values) => {
    setLoading(true);
    try {
      await login(values.username, values.password);
      message.success('登录成功');
      navigate('/dashboard', { replace: true });
    } catch (err) {
      // 从错误中提取后端返回的 message，否则显示通用提示
      const errorMsg =
        err instanceof Error ? err.message : '登录失败，请检查用户名和密码';
      message.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  /** OAuth 登录跳转 */
  const handleOAuthLogin = (provider: string) => {
    // 使用 window.open 跳转到 OAuth 授权页面，避免直接修改 window.location
    window.open(`/api/public/oauth/${provider}/authorize`, '_self');
  };

  return (
    <div className="login-page">
      <div className="login-card">
        {/* Logo 和标题 */}
        <div className="login-card__header">
          <div className="login-card__logo">AM</div>
          <Title level={4} style={{ margin: '8px 0 4px' }}>
            Admin Management
          </Title>
          <Text type="secondary">后台管理系统</Text>
        </div>

        {/* 登录表单 */}
        <Form<LoginFormValues>
          name="login"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          size="large"
          autoComplete="off"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
              allowClear
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item>
            <div className="login-card__options">
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>记住我</Checkbox>
              </Form.Item>
            </div>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
            >
              登录
            </Button>
          </Form.Item>
        </Form>

        {/* OAuth 三方登录 */}
        <Divider plain>
          <Text type="secondary" style={{ fontSize: 12 }}>
            其他登录方式
          </Text>
        </Divider>

        <div className="login-card__oauth">
          <Space size={24}>
            {oauthProviders.map((provider) => (
              <Button
                key={provider.key}
                shape="circle"
                size="large"
                icon={provider.icon}
                title={provider.name}
                className="login-card__oauth-btn"
                style={{
                  borderColor: provider.color,
                  color: provider.color,
                }}
                onClick={() => handleOAuthLogin(provider.key)}
              />
            ))}
          </Space>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
