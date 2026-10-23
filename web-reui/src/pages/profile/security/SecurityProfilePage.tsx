import { useCallback, useEffect, useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { KeyRound, Link2, Unlink } from 'lucide-react'
import { toast } from 'sonner'
import dayjs from 'dayjs'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { FeishuIcon, WeComIcon, WechatIcon } from '@/components/OAuthIcons'
import { changePassword } from '@/api/auth'
import { listBoundAccounts, unbindAccount, getAuthorizeUrl } from '@/api/oauth'
import type { OAuthAccount } from '@/types/auth'

/** OAuth 提供商配置（编码需与后端 auth_provider.code 一致：LARK/WECOM/WECHAT） */
const providerConfig: Record<
  string,
  { name: string; icon: React.ReactNode }
> = {
  LARK: { name: '飞书', icon: <FeishuIcon /> },
  WECOM: { name: '企业微信', icon: <WeComIcon /> },
  WECHAT: { name: '微信', icon: <WechatIcon /> },
}

/** OAuth 提供商列表 */
const providers = ['LARK', 'WECOM', 'WECHAT']

/** 密码表单值 */
type PasswordFormValues = {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

/** 账号安全页面 */
const SecurityProfilePage = () => {
  // 修改密码
  const [changingPassword, setChangingPassword] = useState(false)
  const passwordForm = useForm<PasswordFormValues>({
    defaultValues: { currentPassword: '', newPassword: '', confirmPassword: '' },
  })
  // 订阅新密码用于「确认密码」一致性校验，避免使用不可安全缓存的 form.watch()
  const newPasswordValue = useWatch({ control: passwordForm.control, name: 'newPassword' })

  // OAuth 绑定账户
  const [accounts, setAccounts] = useState<OAuthAccount[]>([])
  const [loadingAccounts, setLoadingAccounts] = useState(false)
  const [unbinding, setUnbinding] = useState<OAuthAccount | null>(null)

  const fetchAccounts = useCallback(async () => {
    setLoadingAccounts(true)
    try {
      const data = await listBoundAccounts()
      setAccounts(data)
    } catch (error) {
      toast.error((error as Error).message || '获取绑定账户失败')
    } finally {
      setLoadingAccounts(false)
    }
  }, [])

  // 首屏加载：状态更新在 promise 回调中完成，并带取消保护
  useEffect(() => {
    let cancelled = false
    listBoundAccounts()
      .then((data) => {
        if (!cancelled) setAccounts(data)
      })
      .catch((error: Error) => {
        if (!cancelled) toast.error(error.message || '获取绑定账户失败')
      })
      .finally(() => {
        if (!cancelled) setLoadingAccounts(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const handleChangePassword = async (values: PasswordFormValues) => {
    setChangingPassword(true)
    try {
      await changePassword({
        oldPassword: values.currentPassword,
        newPassword: values.newPassword,
      })
      toast.success('密码修改成功')
      passwordForm.reset()
    } catch (error) {
      toast.error((error as Error).message || '密码修改失败')
    } finally {
      setChangingPassword(false)
    }
  }

  const handleUnbind = async () => {
    if (!unbinding) return
    try {
      await unbindAccount(unbinding.providerCode, unbinding.id)
      toast.success('解绑成功')
      fetchAccounts()
    } catch (error) {
      toast.error((error as Error).message || '解绑失败')
    } finally {
      setUnbinding(null)
    }
  }

  const handleBind = async (provider: string) => {
    try {
      const { url } = await getAuthorizeUrl(provider)
      // 使用 assign 而非直接赋值 href，避免修改组件外部的只读属性
      window.location.assign(url)
    } catch (error) {
      toast.error((error as Error).message || '获取授权链接失败')
    }
  }

  const getBoundAccount = (provider: string) =>
    accounts.find((item) => item.providerCode === provider)

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">账号安全</h1>
        <p className="text-sm text-muted-foreground">管理登录密码与三方账号绑定</p>
      </div>

      {/* 修改密码 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <KeyRound className="size-4" />
            修改密码
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={passwordForm.handleSubmit(handleChangePassword)}
            className="flex max-w-md flex-col gap-4"
          >
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="current-password">当前密码</Label>
              <Input
                id="current-password"
                type="password"
                placeholder="请输入当前密码"
                {...passwordForm.register('currentPassword', { required: '请输入当前密码' })}
              />
              {passwordForm.formState.errors.currentPassword && (
                <p className="text-xs text-destructive">
                  {passwordForm.formState.errors.currentPassword.message}
                </p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="new-password">新密码</Label>
              <Input
                id="new-password"
                type="password"
                placeholder="请输入新密码"
                {...passwordForm.register('newPassword', {
                  required: '请输入新密码',
                  minLength: { value: 6, message: '密码长度为 6-100 个字符' },
                  maxLength: { value: 100, message: '密码长度为 6-100 个字符' },
                })}
              />
              {passwordForm.formState.errors.newPassword && (
                <p className="text-xs text-destructive">
                  {passwordForm.formState.errors.newPassword.message}
                </p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="confirm-password">确认新密码</Label>
              <Input
                id="confirm-password"
                type="password"
                placeholder="请再次输入新密码"
                {...passwordForm.register('confirmPassword', {
                  required: '请确认新密码',
                  validate: (value) =>
                    value === newPasswordValue || '两次输入的密码不一致',
                })}
              />
              {passwordForm.formState.errors.confirmPassword && (
                <p className="text-xs text-destructive">
                  {passwordForm.formState.errors.confirmPassword.message}
                </p>
              )}
            </div>

            <Button type="submit" className="w-fit" disabled={changingPassword}>
              修改密码
            </Button>
          </form>
        </CardContent>
      </Card>

      {/* 三方账号绑定 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Link2 className="size-4" />
            三方账号绑定
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loadingAccounts && accounts.length === 0 ? (
            <p className="py-6 text-center text-sm text-muted-foreground">加载中…</p>
          ) : (
            <div className="flex flex-col divide-y">
              {providers.map((provider) => {
                const config = providerConfig[provider]
                if (!config) return null
                const account = getBoundAccount(provider)
                const isBound = !!account
                return (
                  <div key={provider} className="flex items-center gap-4 py-4">
                    <span className="text-2xl [&_svg]:size-6">{config.icon}</span>
                    <div className="flex flex-1 flex-col gap-0.5">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-medium">{config.name}</span>
                        <span
                          className={
                            isBound
                              ? 'rounded-full bg-emerald-100 px-2 py-0.5 text-xs text-emerald-700 dark:bg-emerald-950 dark:text-emerald-400'
                              : 'rounded-full bg-muted px-2 py-0.5 text-xs text-muted-foreground'
                          }
                        >
                          {isBound ? '已绑定' : '未绑定'}
                        </span>
                      </div>
                      <span className="text-xs text-muted-foreground">
                        {isBound
                          ? `账号：${account.nickname || account.providerUserId} · 上次登录：${
                              account.lastLoginAt
                                ? dayjs(account.lastLoginAt).format('YYYY-MM-DD HH:mm')
                                : '-'
                            }`
                          : `未绑定${config.name}账户`}
                      </span>
                    </div>
                    {isBound ? (
                      <Button
                        variant="outline"
                        size="sm"
                        className="text-destructive"
                        onClick={() => setUnbinding(account)}
                      >
                        <Unlink className="size-3.5" />
                        解绑
                      </Button>
                    ) : (
                      <Button variant="outline" size="sm" onClick={() => handleBind(provider)}>
                        绑定
                      </Button>
                    )}
                  </div>
                )
              })}
            </div>
          )}
        </CardContent>
      </Card>

      {/* 解绑确认 */}
      <ConfirmDialog
        open={!!unbinding}
        title="确认解绑"
        description={`确定要解绑${providerConfig[unbinding?.providerCode ?? '']?.name ?? ''}账户吗？`}
        destructive
        onConfirm={handleUnbind}
        onOpenChange={(open) => !open && setUnbinding(null)}
      />
    </div>
  )
}

export default SecurityProfilePage
