import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import { Plus, RotateCw, Pencil, Search, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import dayjs from 'dayjs'
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
import { Card, CardContent } from '@/components/ui/card'
import { DataTable } from '@/components/data-table/DataTable'
import { FormDialog } from '@/components/FormDialog'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { StatusBadge } from '@/components/StatusBadge'
import { useClientPagedList } from '@/hooks/useClientPagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import {
  listSources,
  createSource,
  updateSource,
  deleteSource,
} from '@/api/integration'
import type { SourceVO, SourceCreateRequest, SourceUpdateRequest } from '@/types/integration'

/** 来源类型选项 */
const SOURCE_TYPE_OPTIONS = [
  { label: 'HR', value: 'HR' },
  { label: 'IM', value: 'IM' },
  { label: 'OA', value: 'OA' },
]

/** 状态选项（后端 ENABLED / DISABLED） */
const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
]

/** 查询条件：后端只返回全量数组，名称 / 编码走前端过滤 */
interface SourceQuery {
  name?: string
  code?: string
}

/** 表单值 */
type SourceFormValues = {
  code: string
  name: string
  sourceType: string
  tenantKey: string
  priority: number
  configJson: string
  status: string
}

const emptyForm: SourceFormValues = {
  code: '',
  name: '',
  sourceType: 'HR',
  tenantKey: '',
  priority: 0,
  configJson: '',
  status: 'ENABLED',
}

/** 外部身份源管理页面 */
const SourcePage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    useClientPagedList<SourceVO, SourceQuery>({
      fetcher: () => listSources(),
      filter: (all, query) =>
        all.filter((item) => {
          if (query.name && !item.name.includes(query.name)) return false
          if (query.code && !item.code.includes(query.code)) return false
          return true
        }),
      initialSize: 10,
    })

  // 搜索条件
  const [searchName, setSearchName] = useState('')
  const [searchCode, setSearchCode] = useState('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<SourceVO | null>(null)
  const [deleting, setDeleting] = useState<SourceVO | null>(null)

  const handleSearch = () => {
    setQuery({ name: searchName || undefined, code: searchCode || undefined })
  }

  const handleReset = () => {
    setSearchName('')
    setSearchCode('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: SourceVO) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: SourceFormValues) => {
    if (editing) {
      const updateData: SourceUpdateRequest = {
        name: values.name,
        sourceType: values.sourceType,
        tenantKey: values.tenantKey || undefined,
        status: values.status,
        priority: values.priority,
        configJson: values.configJson || undefined,
      }
      await updateSource(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: SourceCreateRequest = {
        code: values.code,
        name: values.name,
        sourceType: values.sourceType,
        tenantKey: values.tenantKey || undefined,
        priority: values.priority,
        configJson: values.configJson || undefined,
      }
      await createSource(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteSource(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义 */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, SourceVO>()
  const columns = columnHelper.columns([
    columnHelper.accessor('id', { header: 'ID', size: 80 }),
    columnHelper.accessor('code', { header: '系统编码', size: 140 }),
    columnHelper.accessor('name', { header: '系统名称', size: 160 }),
    columnHelper.accessor('sourceType', {
      header: '来源类型',
      size: 100,
      cell: (info) => {
        const label = SOURCE_TYPE_OPTIONS.find((o) => o.value === info.getValue())?.label
        return <span>{label || info.getValue()}</span>
      },
    }),
    columnHelper.accessor('tenantKey', {
      header: '租户标识',
      size: 140,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('priority', {
      header: '优先级',
      size: 90,
      cell: (info) => info.getValue() ?? '-',
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('createdAt', {
      header: '创建时间',
      size: 180,
      cell: (info) => (info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-'),
    }),
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
                <Label className="text-xs text-muted-foreground">系统名称</Label>
                <Input
                  placeholder="系统名称"
                  className="w-[150px]"
                  value={searchName}
                  onChange={(event) => setSearchName(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">系统编码</Label>
                <Input
                  placeholder="系统编码"
                  className="w-[150px]"
                  value={searchCode}
                  onChange={(event) => setSearchCode(event.target.value)}
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
                新增身份源
              </Button>
            </div>
          }
        />
      </CardContent>

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
    </Card>
  )
}

/** 身份源表单弹窗 */
function SourceFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: SourceVO | null
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
      : emptyForm,
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
        <Label htmlFor="source-code">系统编码</Label>
        <Input
          id="source-code"
          placeholder="请输入系统编码"
          disabled={!!editing}
          {...form.register('code', {
            required: '请输入系统编码',
            maxLength: { value: 50, message: '系统编码长度不超过 50' },
          })}
        />
        {form.formState.errors.code && (
          <p className="text-xs text-destructive">{form.formState.errors.code.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="source-name">系统名称</Label>
        <Input
          id="source-name"
          placeholder="请输入系统名称"
          {...form.register('name', { required: '请输入系统名称' })}
        />
        {form.formState.errors.name && (
          <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="source-type">来源类型</Label>
        <Controller
          control={form.control}
          name="sourceType"
          render={({ field }) => (
            <Select value={field.value} onValueChange={field.onChange}>
              <SelectTrigger id="source-type">
                <SelectValue />
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
        <Label htmlFor="source-tenant">租户标识</Label>
        <Input id="source-tenant" placeholder="请输入租户标识" {...form.register('tenantKey')} />
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="source-priority">优先级</Label>
        <Input
          id="source-priority"
          type="number"
          min={0}
          placeholder="请输入优先级"
          {...form.register('priority', { valueAsNumber: true, min: { value: 0, message: '优先级不能小于 0' } })}
        />
        {form.formState.errors.priority && (
          <p className="text-xs text-destructive">{form.formState.errors.priority.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="source-config">扩展配置（JSON）</Label>
        <Textarea
          id="source-config"
          rows={3}
          placeholder="请输入扩展配置 JSON"
          {...form.register('configJson')}
        />
      </div>

      {editing && (
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="source-status">状态</Label>
          <Controller
            control={form.control}
            name="status"
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger id="source-status">
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
    </FormDialog>
  )
}

export default SourcePage
