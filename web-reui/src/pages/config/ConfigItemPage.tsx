import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import { Plus, RotateCw, Pencil, Search, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent } from '@/components/ui/card'
import { DataTable } from '@/components/data-table/DataTable'
import { FormDialog } from '@/components/FormDialog'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { StatusBadge } from '@/components/StatusBadge'
import { Badge } from '@/components/reui/badge'
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listConfig, createConfig, updateConfig, deleteConfig } from '@/api/config'
import type {
  ConfigItemVO,
  ConfigItemCreateDTO,
  ConfigItemUpdateDTO,
  ConfigItemQuery,
} from '@/types/config'

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
]

/** 配置类型选项 */
const CONFIG_TYPE_OPTIONS = [
  { label: 'STRING', value: 'STRING' },
  { label: 'NUMBER', value: 'NUMBER' },
  { label: 'BOOLEAN', value: 'BOOLEAN' },
  { label: 'JSON', value: 'JSON' },
]

/** 配置类型徽章配色 */
const CONFIG_TYPE_VARIANT: Record<string, 'info' | 'success' | 'warning' | 'secondary'> = {
  STRING: 'info',
  NUMBER: 'success',
  BOOLEAN: 'warning',
  JSON: 'secondary',
}

/** 配置项表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type ConfigFormValues = {
  configKey: string
  configValue: string
  configType: string
  description: string
  status: string
}

const emptyForm: ConfigFormValues = {
  configKey: '',
  configValue: '',
  configType: 'STRING',
  description: '',
  status: 'ENABLED',
}

/** 配置项页面 */
const ConfigItemPage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    usePagedList<ConfigItemVO, ConfigItemQuery>({
      fetcher: listConfig,
      initialSize: 10,
    })

  // 搜索条件
  const [searchConfigKey, setSearchConfigKey] = useState('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<ConfigItemVO | null>(null)
  const [deleting, setDeleting] = useState<ConfigItemVO | null>(null)

  const handleSearch = () => {
    setQuery({ configKey: searchConfigKey || undefined })
  }

  const handleReset = () => {
    setSearchConfigKey('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: ConfigItemVO) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: ConfigFormValues) => {
    if (editing) {
      const updateData: ConfigItemUpdateDTO = {
        configKey: values.configKey,
        configValue: values.configValue,
        configType: values.configType,
        description: values.description || undefined,
        status: values.status,
      }
      await updateConfig(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: ConfigItemCreateDTO = {
        configKey: values.configKey,
        configValue: values.configValue,
        configType: values.configType,
        description: values.description || undefined,
        status: values.status,
      }
      await createConfig(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteConfig(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, ConfigItemVO>()
  const columns = columnHelper.columns([
    columnHelper.accessor('configKey', { header: '配置键', size: 200 }),
    columnHelper.accessor('configValue', {
      header: '配置值',
      size: 200,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('configType', {
      header: '配置类型',
      size: 120,
      cell: (info) => (
        <Badge variant={CONFIG_TYPE_VARIANT[info.getValue()] ?? 'secondary'} size="sm">
          {info.getValue()}
        </Badge>
      ),
    }),
    columnHelper.accessor('description', {
      header: '描述',
      size: 200,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 150,
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
    <Card>
      <CardContent className="pt-6">
        <DataTable
          columns={columns}
          data={rows}
          total={total}
          page={page}
          pageSize={size}
          loading={loading}
          onPageChange={setPage}
          onPageSizeChange={setSize}
          getRowId={(row) => String(row.id)}
          toolbar={
            <div className="flex flex-wrap items-end gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">配置键</Label>
                <Input
                  placeholder="配置键"
                  className="w-[200px]"
                  value={searchConfigKey}
                  onChange={(event) => setSearchConfigKey(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex gap-2">
                <Button onClick={handleSearch}>
                  <Search className="size-4" />
                  搜索
                </Button>
                <Button variant="outline" onClick={handleReset}>
                  <RotateCw className="size-4" />
                  重置
                </Button>
              </div>
              <Button className="ml-auto" onClick={openCreate}>
                <Plus className="size-4" />
                新增配置项
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <ConfigFormDialog
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
        description={`确定要删除配置项「${deleting?.configKey ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 配置项表单弹窗：表单实例由本组件创建并持有 */
function ConfigFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: ConfigItemVO | null
  onOpenChange: (open: boolean) => void
  onSubmit: (values: ConfigFormValues) => Promise<void>
}) {
  const form = useForm<ConfigFormValues>({
    defaultValues: editing
      ? {
          configKey: editing.configKey,
          configValue: editing.configValue,
          configType: editing.configType,
          description: editing.description ?? '',
          status: editing.status,
        }
      : emptyForm,
  })

  return (
    <FormDialog<ConfigFormValues>
      open
      title={editing ? '编辑配置项' : '新增配置项'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="configKey">配置键</Label>
          <Input
            id="configKey"
            placeholder="请输入配置键"
            {...form.register('configKey', { required: '请输入配置键' })}
          />
          {form.formState.errors.configKey && (
            <p className="text-xs text-destructive">{form.formState.errors.configKey.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="configValue">配置值</Label>
          <Input
            id="configValue"
            placeholder="请输入配置值"
            {...form.register('configValue', { required: '请输入配置值' })}
          />
          {form.formState.errors.configValue && (
            <p className="text-xs text-destructive">{form.formState.errors.configValue.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="configType">配置类型</Label>
          <Controller
            control={form.control}
            name="configType"
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger id="configType">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {CONFIG_TYPE_OPTIONS.map((option) => (
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
          <Label htmlFor="description">描述</Label>
          <textarea
            id="description"
            rows={3}
            placeholder="请输入描述"
            className="flex min-h-[60px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
            {...form.register('description')}
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="status">状态</Label>
          <Controller
            control={form.control}
            name="status"
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger id="status">
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
      </>
    </FormDialog>
  )
}

export default ConfigItemPage
