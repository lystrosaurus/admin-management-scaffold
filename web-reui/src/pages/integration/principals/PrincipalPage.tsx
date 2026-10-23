import { useCallback, useState } from 'react'
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
  listPrincipals,
  createPrincipal,
  updatePrincipal,
  deletePrincipal,
  listSources,
} from '@/api/integration'
import type {
  PrincipalVO,
  PrincipalCreateRequest,
  PrincipalUpdateRequest,
  PrincipalQueryParams,
  SourceVO,
} from '@/types/integration'

/** 主体类型选项 */
const PRINCIPAL_TYPE_OPTIONS = [
  { label: '用户', value: 'USER' },
  { label: '组织', value: 'ORG' },
]

/** 状态选项（后端 ACTIVE / INACTIVE） */
const STATUS_OPTIONS = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
]

/** 关联状态选项 */
const LINK_STATUS_OPTIONS = [
  { label: '未关联', value: 'UNLINKED' },
  { label: '自动关联', value: 'AUTO_LINKED' },
  { label: '手动关联', value: 'MANUAL_LINKED' },
  { label: '冲突', value: 'CONFLICT' },
]

/** 映射目标类型选项 */
const CANONICAL_TYPE_OPTIONS = [
  { label: '员工', value: 'EMPLOYEE' },
  { label: '组织单元', value: 'ORG_UNIT' },
]

/** 外部主体管理页面 */
const PrincipalPage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    useClientPagedList<PrincipalVO, PrincipalQueryParams>({
      fetcher: (query) => listPrincipals(query),
      initialSize: 10,
    })

  // 搜索条件
  const [searchSourceId, setSearchSourceId] = useState<number | ''>('')
  const [searchLinkStatus, setSearchLinkStatus] = useState<string>('')

  // 身份源下拉选项
  const [sourceOptions, setSourceOptions] = useState<{ label: string; value: number }[]>([])

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<PrincipalVO | null>(null)
  const [deleting, setDeleting] = useState<PrincipalVO | null>(null)

  const fetchSources = useCallback(async () => {
    try {
      const sources = await listSources()
      setSourceOptions(sources.map((item: SourceVO) => ({ label: item.name, value: item.id })))
    } catch {
      // 忽略：下拉为空不影响主流程
    }
  }, [])

  // 首次加载：拉取身份源下拉 + 主体列表
  useState(() => {
    void fetchSources()
  })

  const handleSearch = () => {
    setQuery({
      sourceId: searchSourceId || undefined,
      linkStatus: searchLinkStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchSourceId('')
    setSearchLinkStatus('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: PrincipalVO) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: PrincipalFormValues) => {
    if (editing) {
      const updateData: PrincipalUpdateRequest = {
        displayName: values.displayName,
        status: values.status,
        canonicalType: values.canonicalType || undefined,
        canonicalId: values.canonicalId || undefined,
        linkStatus: values.linkStatus || undefined,
      }
      await updatePrincipal(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: PrincipalCreateRequest = {
        sourceId: values.sourceId,
        principalType: values.principalType,
        externalKey: values.externalKey,
        displayName: values.displayName,
        rawPayloadJson: values.rawPayloadJson || undefined,
      }
      await createPrincipal(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deletePrincipal(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义 */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, PrincipalVO>()
  const columns = columnHelper.columns([
    columnHelper.accessor('id', { header: 'ID', size: 80 }),
    columnHelper.accessor('sourceName', {
      header: '外部系统',
      size: 140,
      cell: (info) => info.getValue() || `ID:${info.row.original.sourceId}`,
    }),
    columnHelper.accessor('principalType', {
      header: '主体类型',
      size: 100,
      cell: (info) =>
        PRINCIPAL_TYPE_OPTIONS.find((o) => o.value === info.getValue())?.label || info.getValue(),
    }),
    columnHelper.accessor('externalKey', { header: '外部系统主键', size: 160 }),
    columnHelper.accessor('displayName', { header: '显示名称', size: 160 }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('linkStatus', {
      header: '关联状态',
      size: 110,
      cell: (info) => {
        const value = info.getValue()
        if (!value) return '-'
        const label = LINK_STATUS_OPTIONS.find((o) => o.value === value)?.label || value
        return <span>{label}</span>
      },
    }),
    columnHelper.accessor('canonicalType', {
      header: '映射目标',
      size: 140,
      cell: (info) => {
        const record = info.row.original
        if (!record.canonicalType) return '-'
        const label =
          CANONICAL_TYPE_OPTIONS.find((o) => o.value === record.canonicalType)?.label ||
          record.canonicalType
        return <span>{`${label} (${record.canonicalId})`}</span>
      },
    }),
    columnHelper.accessor('lastSyncAt', {
      header: '最后同步时间',
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
                <Label className="text-xs text-muted-foreground">外部系统</Label>
                <Select
                  value={searchSourceId ? String(searchSourceId) : 'ALL'}
                  onValueChange={(value) => setSearchSourceId(value === 'ALL' ? '' : Number(value))}
                >
                  <SelectTrigger className="w-[160px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">全部</SelectItem>
                    {sourceOptions.map((option) => (
                      <SelectItem key={option.value} value={String(option.value)}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">关联状态</Label>
                <Select
                  value={searchLinkStatus || 'ALL'}
                  onValueChange={(value) => setSearchLinkStatus(value === 'ALL' ? '' : value)}
                >
                  <SelectTrigger className="w-[140px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">全部</SelectItem>
                    {LINK_STATUS_OPTIONS.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
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
                新增外部主体
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗 */}
      {dialogOpen && (
        <PrincipalFormDialog
          key={editing?.id ?? 'new'}
          editing={editing}
          sourceOptions={sourceOptions}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除外部主体「${deleting?.displayName ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 表单值 */
type PrincipalFormValues = {
  sourceId: number
  principalType: string
  externalKey: string
  displayName: string
  rawPayloadJson: string
  status: string
  linkStatus: string
  canonicalType: string
  canonicalId: number
}

/** 外部主体表单弹窗 */
function PrincipalFormDialog({
  editing,
  sourceOptions,
  onOpenChange,
  onSubmit,
}: {
  editing: PrincipalVO | null
  sourceOptions: { label: string; value: number }[]
  onOpenChange: (open: boolean) => void
  onSubmit: (values: PrincipalFormValues) => Promise<void>
}) {
  const form = useForm<PrincipalFormValues>({
    defaultValues: editing
      ? {
          sourceId: editing.sourceId,
          principalType: editing.principalType,
          externalKey: editing.externalKey,
          displayName: editing.displayName,
          rawPayloadJson: '',
          status: editing.status,
          linkStatus: editing.linkStatus ?? 'UNLINKED',
          canonicalType: editing.canonicalType ?? '',
          canonicalId: editing.canonicalId ?? 0,
        }
      : {
          sourceId: 0,
          principalType: 'USER',
          externalKey: '',
          displayName: '',
          rawPayloadJson: '',
          status: 'ACTIVE',
          linkStatus: 'UNLINKED',
          canonicalType: '',
          canonicalId: 0,
        },
  })

  return (
    <FormDialog<PrincipalFormValues>
      open
      title={editing ? '编辑外部主体' : '新增外部主体'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      {!editing && (
        <>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="principal-source">外部系统</Label>
            <Controller
              control={form.control}
              name="sourceId"
              rules={{ validate: (value) => value > 0 || '请选择外部系统' }}
              render={({ field }) => (
                <Select
                  value={field.value ? String(field.value) : ''}
                  onValueChange={(value) => field.onChange(Number(value))}
                >
                  <SelectTrigger id="principal-source">
                    <SelectValue placeholder="请选择外部系统" />
                  </SelectTrigger>
                  <SelectContent>
                    {sourceOptions.map((option) => (
                      <SelectItem key={option.value} value={String(option.value)}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            {form.formState.errors.sourceId && (
              <p className="text-xs text-destructive">{form.formState.errors.sourceId.message}</p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="principal-type">主体类型</Label>
            <Controller
              control={form.control}
              name="principalType"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger id="principal-type">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {PRINCIPAL_TYPE_OPTIONS.map((option) => (
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
            <Label htmlFor="principal-key">外部系统主键</Label>
            <Input
              id="principal-key"
              placeholder="请输入外部系统主键"
              {...form.register('externalKey', { required: '请输入外部系统主键' })}
            />
            {form.formState.errors.externalKey && (
              <p className="text-xs text-destructive">{form.formState.errors.externalKey.message}</p>
            )}
          </div>
        </>
      )}

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="principal-name">显示名称</Label>
        <Input
          id="principal-name"
          placeholder="请输入显示名称"
          {...form.register('displayName', { required: '请输入显示名称' })}
        />
        {form.formState.errors.displayName && (
          <p className="text-xs text-destructive">{form.formState.errors.displayName.message}</p>
        )}
      </div>

      {!editing && (
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="principal-payload">原始数据（JSON）</Label>
          <Textarea
            id="principal-payload"
            rows={3}
            placeholder="请输入原始数据 JSON"
            {...form.register('rawPayloadJson')}
          />
        </div>
      )}

      {editing && (
        <>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="principal-status">状态</Label>
            <Controller
              control={form.control}
              name="status"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger id="principal-status">
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

          <div className="flex flex-col gap-1.5">
            <Label htmlFor="principal-link-status">关联状态</Label>
            <Controller
              control={form.control}
              name="linkStatus"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger id="principal-link-status">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {LINK_STATUS_OPTIONS.map((option) => (
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
            <Label htmlFor="principal-canonical-type">映射目标类型</Label>
            <Controller
              control={form.control}
              name="canonicalType"
              render={({ field }) => (
                <Select
                  value={field.value || 'NONE'}
                  onValueChange={(value) => field.onChange(value === 'NONE' ? '' : value)}
                >
                  <SelectTrigger id="principal-canonical-type">
                    <SelectValue placeholder="请选择映射目标类型" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="NONE">未设置</SelectItem>
                    {CANONICAL_TYPE_OPTIONS.map((option) => (
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
            <Label htmlFor="principal-canonical-id">映射目标 ID</Label>
            <Input
              id="principal-canonical-id"
              type="number"
              placeholder="请输入映射目标 ID"
              {...form.register('canonicalId', { valueAsNumber: true })}
            />
          </div>
        </>
      )}
    </FormDialog>
  )
}

export default PrincipalPage
