import { useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { Spin, Typography, Button } from 'antd';
import { bindAccount } from '@/api/oauth';
import { message } from 'antd';

const { Text } = Typography;

/**
 * OAuth 回调页
 *
 * <p>三方登录/绑定流程中，授权提供商（飞书/企业微信/微信）在用户授权后，
 * 将浏览器重定向到 <前端源>/oauth/callback/{provider}?code=xxx&state=yyy。
 * 本页读取 code/state，调用后端绑定接口完成账号绑定，随后跳回账号安全页。
 *
 * <p>注意：本页为独立路由（不依赖布局/鉴权布局），因 OAuth 重定向是一次完整的页面刷新，
 * 登录态由 localStorage 中的 token 自动带入请求头。
 */
const OAuthCallbackPage = () => {
  const { provider } = useParams<{ provider: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const code = searchParams.get('code');
  const state = searchParams.get('state');

  useEffect(() => {
    let cancelled = false;

    const finish = (ok: boolean, msg: string) => {
      if (cancelled) return;
      if (ok) message.success(msg);
      else message.error(msg);
      navigate('/profile/security', { replace: true });
    };

    if (!provider || !code || !state) {
      finish(false, '回调参数缺失，无法完成绑定');
      return;
    }

    bindAccount(provider, { code, state })
      .then(() => finish(true, '三方账号绑定成功'))
      .catch((err: unknown) =>
        finish(false, (err as Error).message || '三方账号绑定失败'),
      );

    return () => {
      cancelled = true;
    };
  }, [provider, code, state, navigate]);

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        height: '60vh',
        gap: 16,
      }}
    >
      <Spin tip="正在完成账号绑定…" size="large" />
      <Text type="secondary">绑定完成后将自动返回账号安全页</Text>
      <Button onClick={() => navigate('/profile/security')}>返回账号安全</Button>
    </div>
  );
};

export default OAuthCallbackPage;
