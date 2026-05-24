import { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  List,
  Tag,
  Popconfirm,
  message,
  Space,
  Typography,
  Empty,
} from 'antd';
import {
  LockOutlined,
  WechatOutlined,
  DingdingOutlined,
} from '@ant-design/icons';
import { changePassword } from '@/api/auth';
import { listBoundAccounts, unbindAccount, getAuthorizeUrl } from '@/api/oauth';
import type { OAuthAccount } from '@/types/auth';
import dayjs from 'dayjs';

const { Text } = Typography;

/** OAuth 提供商配置 */
const providerConfig: Record<string, { name: string; icon: React.ReactNode; color: string }> = {
  feishu: { name: '飞书', icon: <DingdingOutlined />, color: '#3370ff' },
  wecom: { name: '企业微信', icon: <WechatOutlined />, color: '#07c160' },
  wechat: { name: '微信', icon: <WechatOutlined />, color: '#07c160' },
};

/** OAuth 提供商列表 */
const providers = ['feishu', 'wecom', 'wechat'];

/** 账号安全页面 */
const SecurityProfilePage = () => {
  // 密码表单
  const [passwordForm] = Form.useForm();
  const [changingPassword, setChangingPassword] = useState(false);

  // OAuth 账户列表
  const [accounts, setAccounts] = useState<OAuthAccount[]>([]);
  const [loadingAccounts, setLoadingAccounts] = useState(false);

  /**
   * 获取已绑定的 OAuth 账户列表
   */
  const fetchAccounts = useCallback(async () => {
    setLoadingAccounts(true);
    try {
      const data = await listBoundAccounts();
      setAccounts(data);
    } catch (error) {
      message.error((error as Error).message || '获取绑定账户失败');
    } finally {
      setLoadingAccounts(false);
    }
  }, []);

  useEffect(() => {
    fetchAccounts();
  }, [fetchAccounts]);

  /**
   * 处理密码修改
   */
  const handleChangePassword = async () => {
    try {
      const values = await passwordForm.validateFields();
      setChangingPassword(true);

      await changePassword({
        oldPassword: values.currentPassword,
        newPassword: values.newPassword,
      });

      message.success('密码修改成功');
      passwordForm.resetFields();
    } catch (error) {
      if ((error as { errorFields?: unknown }).errorFields) {
        return;
      }
      message.error((error as Error).message || '密码修改失败');
    } finally {
      setChangingPassword(false);
    }
  };

  /**
   * 处理解绑 OAuth 账户
   */
  const handleUnbind = async (provider: string, accountId: number) => {
    try {
      await unbindAccount(provider, accountId);
      message.success('解绑成功');
      fetchAccounts();
    } catch (error) {
      message.error((error as Error).message || '解绑失败');
    }
  };

  /**
   * 处理绑定 OAuth 账户（跳转授权页）
   */
  const handleBind = async (provider: string) => {
    try {
      const { url } = await getAuthorizeUrl(provider);
      window.location.href = url;
    } catch (error) {
      message.error((error as Error).message || '获取授权链接失败');
    }
  };

  /**
   * 获取已绑定的账户（按提供商）
   */
  const getBoundAccount = (provider: string): OAuthAccount | undefined => {
    return accounts.find((a) => a.providerCode === provider);
  };

  /**
   * 渲染 OAuth 账户列表项
   */
  const renderOAuthItem = (provider: string) => {
    const config = providerConfig[provider];
    if (!config) return null;

    const account = getBoundAccount(provider);
    const isBound = !!account;

    return (
      <List.Item
        key={provider}
        actions={
          isBound
            ? [
                <Popconfirm
                  key="unbind"
                  title="确认解绑"
                  description={`确定要解绑${config.name}账户吗？`}
                  onConfirm={() => handleUnbind(provider, account!.id)}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button type="link" danger>
                    解绑
                  </Button>
                </Popconfirm>,
              ]
            : [
                <Button
                  key="bind"
                  type="link"
                  onClick={() => handleBind(provider)}
                >
                  绑定
                </Button>,
              ]
        }
      >
        <List.Item.Meta
          avatar={
            <span style={{ fontSize: 24, color: config.color }}>
              {config.icon}
            </span>
          }
          title={
            <Space>
              <span>{config.name}</span>
              {isBound ? (
                <Tag color="success">已绑定</Tag>
              ) : (
                <Tag color="default">未绑定</Tag>
              )}
            </Space>
          }
          description={
            isBound ? (
              <Text type="secondary">
                账号：{account!.nickname || account!.providerUserId} ·
                上次登录：{account!.lastLoginAt ? dayjs(account!.lastLoginAt).format('YYYY-MM-DD HH:mm') : '-'}
              </Text>
            ) : (
              <Text type="secondary">未绑定{config.name}账户</Text>
            )
          }
        />
      </List.Item>
    );
  };

  return (
    <div>
      {/* 密码修改卡片 */}
      <Card title="修改密码" style={{ marginBottom: 16 }}>
        <Form
          form={passwordForm}
          layout="vertical"
          style={{ maxWidth: 400 }}
        >
          <Form.Item
            name="currentPassword"
            label="当前密码"
            rules={[{ required: true, message: '请输入当前密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入当前密码"
            />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, max: 100, message: '密码长度为 6-100 个字符' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入新密码"
            />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请再次输入新密码"
            />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              onClick={handleChangePassword}
              loading={changingPassword}
            >
              修改密码
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {/* 三方账号绑定卡片 */}
      <Card title="三方账号绑定">
        <List
          loading={loadingAccounts}
          dataSource={providers}
          renderItem={renderOAuthItem}
          locale={{ emptyText: <Empty description="暂无可用的第三方平台" /> }}
        />
      </Card>
    </div>
  );
};

export default SecurityProfilePage;
