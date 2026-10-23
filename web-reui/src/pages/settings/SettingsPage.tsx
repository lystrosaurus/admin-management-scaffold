import { useEffect, useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import { Pencil, Plus, Save, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { DataTable } from '@/components/data-table/DataTable'
import { FormDialog } from '@/components/FormDialog'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { StatusBadge } from '@/components/StatusBadge'
import { useClientPagedList } from '@/hooks/useClientPagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { getAppInfo } from '@/api/ops'
import {
  listIntegrationSources,
  createIntegrationSource,
  updateIntegrationSource,
  deleteIntegrationSource,
} from '@/api/integrationSource'
import type {
  IntegrationSource,
  CreateIntegrationSourceRequest,
  UpdateIntegrationSourceRequest,
} from '@/types/integrationSource'
import type { AppInfo } from '@/types/ops'

/** 语言选项 */
const LANGUAGE_OPTIONS = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
]

/** 来源类型选项 */
const SOURCE_TYPE_OPTIONS = [
  { label: 'HR', value: 'HR' },
  { label: 'IM', value: 'IM' },
  { label: 'OA', value: 'OA' },
]

/** 状态选项 */
const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
]

/** 系统配置（沿用 antd 版 mock 口径：暂无后端 API，仅本地保存） */
interface SystemConfigValues {
  siteName: string
  language: string
  loginFailLockCount: number
  sessionTimeout: number
  fileUploadMaxSize: number
}

const defaultConfig: SystemConfigValues = {
  siteName: 'Admin Management',
  language: 'zh-CN',
  loginFailLockCount: 5,
  sessionTimeout: 30,
  fileUploadMaxSize: 10,
}

/** 系统配置页面 */
const SettingsPage = () => {
  // 系统信息（来自后端 ops 接口）
  const [appInfo, setAppInfo] = useState<AppInfo | null>(null)
  useEffect(() => {
    getAppInfo()
      .then(setAppInfo)
      .catch(() => setAppInfo(null))
  }, [])

  // 可编辑配置（本地态，保存即模拟成功）
  const [saving, setSaving] = useState(false)
  const configForm = useForm<SystemConfigValues>({ defaultValues: defaultConfig })

  const handleSaveConfig = async (values: SystemConfigValues) => {
    setSaving(true)
    try {
      // 暂无后端配置接口：模拟保存延迟
      await new Promise((resolve) => setTimeout(resolve, 300))
      console.info('系统配置已保存（本地）', values)
      toast.success('保存成功')
    } finally {
      setSaving(false)
    }
  }

  // 外部身份源管理
  const { rows, total, page, size, loading, setPage, setSize, refresh } =
    useClientPagedList<IntegrationSource, Record<string, never>>({
      fetcher: () => listIntegrationSources(),
      initialSize: 10,
    })

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<IntegrationSource | null>(null)
  const [deleting, setDeleting] = useState<IntegrationSource | null>(null)

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: IntegrationSource) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: SourceFormValues) => {
    if (editing) {
      const updateData: UpdateIntegrationSourceRequest = {
        name: values.name,
        sourceType: values.sourceType,
        tenantKey: values.tenantKey || undefined,
        status: values.status,
        priority: values.priority,
        configJson: values.configJson || undefined,
      }
      await updateIntegrationSource(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: CreateIntegrationSourceRequest = {
        code: values.code,
        name: values.name,
        sourceType: values.sourceType,
        tenantKey: values.tenantKey || undefined,
        priority: values.priority,
        configJson: values.configJson || undefined,
      }
      await createIntegrationSource(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteIntegrationSource(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 身份源表格列定义 */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, IntegrationSource>()
  const sourceColumns = columnHelper.columns([
    columnHelper.accessor('code', { header: '编码', size: 120 }),
    columnHelper.accessor('name', { header: '名称', size: 150 }),
    columnHelper.accessor('sourceType', { header: '来源类型', size: 90 }),
    columnHelper.accessor('tenantKey', {
      header: '租户标识',
      size: 120,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('priority', { header: '优先级', size: 80 }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 120,
      cell: (info) => (
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-primary"
            onClick={() => openEdit(info.row.original)}
          >
            <Pencil className="size-3.5" />
            编辑
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-destructive"
            onClick={() => setDeleting(info.row.original)}
          >
            <Trash2 className="size-3.5" />
            删除
          </Button>
        </div>
      ),
    }),
  ])

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">系统配置</h1>
        <p className="text-sm text-muted-foreground">系统信息、运行参数与外部身份源管理</p>
      </div>

      {/* 系统信息（只读，来自后端） */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">系统信息</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 gap-x-8 gap-y-4 lg:grid-cols-3">
            {[
              { label: '系统名称', value: appInfo?.name ?? '-' },
              { label: '版本号', value: appInfo?.version ?? '-' },
              { label: '运行环境', value: appInfo?.profile ?? '-' },
              { label: 'Java 版本', value: appInfo?.javaVersion ?? '-' },
              { label: '操作系统', value: appInfo?.osName ?? '-' },
              { label: '运行状态', value: appInfo?.status ?? '-' },
            ].map((item) => (
              <div key={item.label} className="flex flex-col gap-1">
                <span className="text-xs text-muted-foreground">{item.label}</span>
                <span className="text-sm font-medium">{item.value}</span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* 可编辑配置 */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">系统配置</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={configForm.handleSubmit(handleSaveConfig)}
            className="flex max-w-md flex-col gap-4"
          >
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="site-name">系统名称</Label>
              <Input
                id="site-name"
                placeholder="请输入系统名称"
                {...configForm.register('siteName', { required: '请输入系统名称' })}
              />
              {configForm.formState.errors.siteName && (
                <p className="text-xs text-destructive">
                  {configForm.formState.errors.siteName.message}
                </p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="language">默认语言</Label>
              <Controller
                control={configForm.control}
                name="language"
                render={({ field }) => (
                  <Select value={field.value} onValueChange={field.onChange}>
                    <SelectTrigger id="language">
                      <SelectValue placeholder="请选择默认语言" />
                    </SelectTrigger>
                    <SelectContent>
                      {LANGUAGE_OPTIONS.map((option) => (
                        <SelectItem key={option.value} value={option.value}>
                          {option.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                )}
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="lock-count">登录失败锁定次数</Label>
              <Input
                id="lock-count"
                type="number"
                min={1}
                max={20}
                {...configForm.register('loginFailLockCount', {
                  valueAsNumber: true,
                  required: '请输入登录失败锁定次数',
                  min: { value: 1, message: '取值范围 1-20' },
                  max: { value: 20, message: '取值范围 1-20' },
                })}
              />
              <p className="text-xs text-muted-foreground">
                连续登录失败达到此次数后，账户将被临时锁定
              </p>
              {configForm.formState.errors.loginFailLockCount && (
                <p className="text-xs text-destructive">
                  {configForm.formState.errors.loginFailLockCount.message}
                </p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="session-timeout">会话超时时间（分钟）</Label>
              <Input
                id="session-timeout"
                type="number"
                min={5}
                max={1440}
                {...configForm.register('sessionTimeout', {
                  valueAsNumber: true,
                  required: '请输入会话超时时间',
                  min: { value: 5, message: '取值范围 5-1440' },
                  max: { value: 1440, message: '取值范围 5-1440' },
                })}
              />
              <p className="text-xs text-muted-foreground">
                用户无操作后，会话将在指定时间后过期
              </p>
              {configForm.formState.errors.sessionTimeout && (
                <p className="text-xs text-destructive">
                  {configForm.formState.errors.sessionTimeout.message}
                </p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="upload-size">文件上传大小限制（MB）</Label>
              <Input
                id="upload-size"
                type="number"
                min={1}
                max={100}
                {...configForm.register('fileUploadMaxSize', {
                  valueAsNumber: true,
                  required: '请输入文件上传大小限制',
                  min: { value: 1, message: '取值范围 1-100' },
                  max: { value: 100, message: '取值范围 1-100' },
                })}
              />
              <p className="text-xs text-muted-foreground">单个文件上传的最大大小</p>
              {configForm.formState.errors.fileUploadMaxSize && (
                <p className="text-xs text-destructive">
                  {configForm.formState.errors.fileUploadMaxSize.message}
                </p>
              )}
            </div>

            <Button type="submit" className="w-fit" disabled={saving}>
              <Save className="size-4" />
              保存配置
            </Button>
          </form>
        </CardContent>
      </Card>

      {/* 外部身份源管理 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between text-base">
            <span>外部身份源</span>
            <Button size="sm" onClick={openCreate}>
              <Plus className="size-4" />
              新增
            </Button>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <DataTable
            columns={sourceColumns}
            data={rows}
            total={total}
            page={page}
            pageSize={size}
            loading={loading}
            onPageChange={setPage}
            onPageSizeChange={setSize}
            getRowId={(row) => String(row.id)}
          />
        </CardContent>
      </Card>

      {/* 新增 / 编辑弹窗 */}
      {dialogOpen && (
        <SourceFormDialog
          key={editing?.id ?? 'new'}
          editing={editing}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除身份源「${deleting?.name ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </div>
  )
}

/** 身份源表单值 */
type SourceFormValues = {
  code: string
  name: string
  sourceType: string
  tenantKey: string
  priority: number
  configJson: string
  status: string
}

/** 身份源表单弹窗 */
function SourceFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: IntegrationSource | null
  onOpenChange: (open: boolean) => void
  onSubmit: (values: SourceFormValues) => Promise<void>
}) {
  const form = useForm<SourceFormValues>({
    defaultValues: editing
      ? {
          code: editing.code,
          name: editing.name,
          sourceType: editing.sourceType,
          tenantKey: editing.tenantKey ?? '',
          priority: editing.priority ?? 0,
          configJson: editing.configJson ?? '',
          status: editing.status,
        }
      : {
          code: '',
          name: '',
          sourceType: 'HR',
          tenantKey: '',
          priority: 0,
          configJson: '',
          status: 'ENABLED',
        },
  })

  return (
    <FormDialog<SourceFormValues>
      open
      title={editing ? '编辑身份源' : '新增身份源'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-code">编码</Label>
        <Input
          id="settings-source-code"
          placeholder="如 LARK / WECOM / WECHAT"
          disabled={!!editing}
          {...form.register('code', { required: '请输入编码' })}
        />
        {form.formState.errors.code && (
          <p className="text-xs text-destructive">{form.formState.errors.code.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-name">名称</Label>
        <Input
          id="settings-source-name"
          placeholder="如 飞书 / 企业微信"
          {...form.register('name', { required: '请输入名称' })}
        />
        {form.formState.errors.name && (
          <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-type">来源类型</Label>
        <Controller
          control={form.control}
          name="sourceType"
          render={({ field }) => (
            <Select value={field.value} onValueChange={field.onChange}>
              <SelectTrigger id="settings-source-type">
                <SelectValue placeholder="请选择来源类型" />
              </SelectTrigger>
              <SelectContent>
                {SOURCE_TYPE_OPTIONS.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-tenant">租户标识</Label>
        <Input id="settings-source-tenant" placeholder="可选" {...form.register('tenantKey')} />
      </div>

      {editing && (
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="settings-source-status">状态</Label>
          <Controller
            control={form.control}
            name="status"
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger id="settings-source-status">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {STATUS_OPTIONS.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          />
        </div>
      )}

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-priority">优先级</Label>
        <Input
          id="settings-source-priority"
          type="number"
          min={0}
          max={100}
          {...form.register('priority', { valueAsNumber: true })}
        />
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="settings-source-config">扩展配置（JSON）</Label>
        <Textarea
          id="settings-source-config"
          rows={3}
          placeholder='{"clientId":"...","clientSecret":"..."}'
          {...form.register('configJson')}
        />
      </div>
    </FormDialog>
  )
}

export default SettingsPage
