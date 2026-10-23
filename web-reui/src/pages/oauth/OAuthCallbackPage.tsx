import { useEffect } from 'react'
import { useParams, useSearchParams, useNavigate } from 'react-router-dom'
import { Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { bindAccount } from '@/api/oauth'

/**
 * OAuth 回调页
 *
 * 三方登录/绑定流程中，授权提供商（飞书/企业微信/微信）在用户授权后，
 * 将浏览器重定向到 <前端源>/oauth/callback/{provider}?code=xxx&state=yyy。
 * 本页读取 code/state，调用后端绑定接口完成账号绑定，随后跳回账号安全页。
 *
 * 注意：本页为独立路由（不依赖布局），因 OAuth 重定向是一次完整的页面刷新，
 * 登录态由 localStorage 中的 token 自动带入请求头。
 */
const OAuthCallbackPage = () => {
  const { provider } = useParams<{ provider: string }>()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const code = searchParams.get('code')
  const state = searchParams.get('state')
  const error = searchParams.get('error')
  const errorDescription = searchParams.get('error_description')

  useEffect(() => {
    let cancelled = false

    const finish = (ok: boolean, msg: string) => {
      if (cancelled) return
      if (ok) toast.success(msg)
      else toast.error(msg)
      navigate('/profile/security', { replace: true })
    }

    if (!provider) {
      finish(false, '回调参数缺失（缺少 provider）')
      return
    }
    // 厂商侧拒绝授权或出错（如 redirect_uri 未登记）：把真实错误透出，而不是静默回首页
    if (error) {
      finish(false, `授权失败：${error}${errorDescription ? ` - ${errorDescription}` : ''}`)
      return
    }
    if (!code || !state) {
      finish(false, '回调参数缺失（缺少 code 或 state），无法完成绑定')
      return
    }

    bindAccount(provider, { code, state })
      .then(() => finish(true, '三方账号绑定成功'))
      .catch((err: unknown) => finish(false, (err as Error).message || '三方账号绑定失败'))

    return () => {
      cancelled = true
    }
  }, [provider, code, state, error, errorDescription, navigate])

  return (
    <div className="flex h-svh flex-col items-center justify-center gap-4">
      <Loader2 className="size-8 animate-spin text-muted-foreground" />
      <p className="text-sm text-muted-foreground">正在完成账号绑定…</p>
      <p className="text-xs text-muted-foreground">绑定完成后将自动返回账号安全页</p>
      <Button variant="outline" onClick={() => navigate('/profile/security')}>
        返回账号安全
      </Button>
    </div>
  )
}

export default OAuthCallbackPage
