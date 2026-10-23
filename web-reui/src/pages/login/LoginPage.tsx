import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FeishuIcon, WeComIcon, WechatIcon } from '@/components/OAuthIcons'
import { useAuthStore } from '@/stores/authStore'

/** 登录表单校验 schema */
const loginSchema = z.object({
  username: z.string().min(1, '请输入用户名'),
  password: z.string().min(1, '请输入密码'),
})

type LoginFormValues = z.infer<typeof loginSchema>

/** OAuth 三方登录提供方配置 */
const oauthProviders = [
  { key: 'feishu', name: '飞书', icon: <FeishuIcon size={20} /> },
  { key: 'wecom', name: '企业微信', icon: <WeComIcon size={20} /> },
  { key: 'wechat', name: '微信', icon: <WechatIcon size={20} /> },
]

/**
 * 登录页面
 * 用户名密码表单（react-hook-form + zod 校验）+ OAuth 三方登录入口。
 */
const LoginPage = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const login = useAuthStore((s) => s.login)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { username: '', password: '' },
  })

  /** 表单提交：调用登录 API */
  const onSubmit = handleSubmit(async (values) => {
    setLoading(true)
    try {
      await login(values.username, values.password)
      toast.success('登录成功')
      navigate('/dashboard', { replace: true })
    } catch (err) {
      toast.error(err instanceof Error ? err.message : '登录失败，请检查用户名和密码')
    } finally {
      setLoading(false)
    }
  })

  /** OAuth 登录跳转 */
  const handleOAuthLogin = (provider: string) => {
    window.open(`/api/public/oauth/${provider}/authorize`, '_self')
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-muted/30 p-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="items-center text-center">
          <div className="mb-2 flex size-11 items-center justify-center rounded-xl bg-primary text-base font-bold text-primary-foreground">
            AM
          </div>
          <CardTitle className="text-xl">Admin Management</CardTitle>
          <CardDescription>后台管理系统</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="flex flex-col gap-4" noValidate>
            <div className="flex flex-col gap-2">
              <Label htmlFor="username">用户名</Label>
              <Input
                id="username"
                placeholder="请输入用户名"
                autoComplete="username"
                aria-invalid={!!errors.username}
                {...register('username')}
              />
              {errors.username && (
                <p className="text-xs text-destructive">{errors.username.message}</p>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <Label htmlFor="password">密码</Label>
              <Input
                id="password"
                type="password"
                placeholder="请输入密码"
                autoComplete="current-password"
                aria-invalid={!!errors.password}
                {...register('password')}
              />
              {errors.password && (
                <p className="text-xs text-destructive">{errors.password.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? '登录中…' : '登录'}
            </Button>
          </form>

          <div className="relative my-6">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t" />
            </div>
            <div className="relative flex justify-center">
              <span className="bg-card px-2 text-xs text-muted-foreground">其他登录方式</span>
            </div>
          </div>

          <div className="flex justify-center gap-4">
            {oauthProviders.map((provider) => (
              <Button
                key={provider.key}
                type="button"
                variant="outline"
                size="icon"
                className="rounded-full"
                title={provider.name}
                onClick={() => handleOAuthLogin(provider.key)}
              >
                {provider.icon}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default LoginPage
