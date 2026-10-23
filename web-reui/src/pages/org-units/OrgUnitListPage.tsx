import { useCallback, useEffect, useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import {
  Building2,
  FolderTree,
  Network,
  Plus,
  RotateCw,
  Pencil,
  Search,
  Trash2,
  Users,
} from 'lucide-react'
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
import { TreeSelectPopover } from '@/components/TreeSelectPopover'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listOrgUnits, createOrgUnit, updateOrgUnit, deleteOrgUnit } from '@/api/orgUnit'
import type {
  OrgUnit,
  CreateOrgUnitRequest,
  UpdateOrgUnitRequest,
  OrgUnitQueryParams,
  OrgUnitType,
} from '@/types/orgUnit'
import type { Status } from '@/types/api'

/** 组织类型元数据 */
const ORG_TYPE_META: Record<
  OrgUnitType,
  { label: string; variant: 'info' | 'success' | 'warning'; icon: typeof Building2 }
> = {
  COMPANY: { label: '公司', variant: 'info', icon: Building2 },
  DEPARTMENT: { label: '部门', variant: 'success', icon: Network },
  TEAM: { label: '团队', variant: 'warning', icon: Users },
}

/** 类型选项（'' 表示全部） */
const TYPE_OPTIONS: { label: string; value: OrgUnitType }[] = [
  { label: '公司', value: 'COMPANY' },
  { label: '部门', value: 'DEPARTMENT' },
  { label: '团队', value: 'TEAM' },
]

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS: { label: string; value: Status }[] = [
  { label: '启用', value: 'ACTIVE' },
  { label: '停用', value: 'INACTIVE' },
]

/** 组织表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type OrgUnitFormValues = {
  parentId: number | undefined
  name: string
  code: string
  type: OrgUnitType
  leaderId: string
  sort: number
  status: Status
}

const emptyForm: OrgUnitFormValues = {
  parentId: undefined,
  name: '',
  code: '',
  type: 'COMPANY',
  leaderId: '',
  sort: 0,
  status: 'ACTIVE',
}

/** 组织管理页面 */
const OrgUnitListPage = () => {
  const [orgUnits, setOrgUnits] = useState<OrgUnit[]>([])
  const [loading, setLoading] = useState(true)

  // 搜索条件
  const [searchName, setSearchName] = useState('')
  const [searchCode, setSearchCode] = useState('')
  const [searchType, setSearchType] = useState<OrgUnitType | ''>('')
  const [searchStatus, setSearchStatus] = useState<Status | ''>('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<OrgUnit | null>(null)
  const [createParentId, setCreateParentId] = useState<number | undefined>(undefined)
  const [deleting, setDeleting] = useState<OrgUnit | null>(null)

  const loadOrgUnits = useCallback(async (params?: OrgUnitQueryParams) => {
    try {
      const data = await listOrgUnits(params)
      setOrgUnits(data)
    } catch (err) {
      toast.error((err as Error).message || '加载组织列表失败')
    } finally {
      setLoading(false)
    }
  }, [])

  /** 交互触发的重新拉取：loading 在事件中开启，effect 内不同步 setState */
  const reloadOrgUnits = useCallback(
    (params?: OrgUnitQueryParams) => {
      setLoading(true)
      void loadOrgUnits(params)
    },
    [loadOrgUnits],
  )

  // 首屏加载：状态更新在 promise 回调中完成，并带取消保护
  useEffect(() => {
    let cancelled = false
    listOrgUnits()
      .then((data) => {
        if (!cancelled) setOrgUnits(data)
      })
      .catch((err: Error) => {
        if (!cancelled) toast.error(err.message || '加载组织列表失败')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const handleSearch = () => {
    reloadOrgUnits({
      name: searchName || undefined,
      code: searchCode || undefined,
      type: searchType || undefined,
      status: searchStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchName('')
    setSearchCode('')
    setSearchType('')
    setSearchStatus('')
    reloadOrgUnits()
  }

  const openCreate = (parentId?: number) => {
    setEditing(null)
    setCreateParentId(parentId)
    setDialogOpen(true)
  }

  const openEdit = (record: OrgUnit) => {
    setEditing(record)
    setCreateParentId(undefined)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: OrgUnitFormValues) => {
    const base = {
      parentId: values.parentId || undefined,
      name: values.name,
      code: values.code,
      type: values.type,
      leaderId: values.leaderId ? Number(values.leaderId) : undefined,
      sort: values.sort,
      status: values.status,
    }
    if (editing) {
      await updateOrgUnit(editing.id, base as UpdateOrgUnitRequest)
      toast.success('更新成功')
    } else {
      await createOrgUnit(base as CreateOrgUnitRequest)
      toast.success('创建成功')
    }
    reloadOrgUnits()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteOrgUnit(deleting.id)
      toast.success('删除成功')
      reloadOrgUnits()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, OrgUnit>()
  const columns = columnHelper.columns([
    columnHelper.accessor('name', {
      header: '组织名称',
      size: 220,
      cell: (info) => {
        const meta = ORG_TYPE_META[info.row.original.type]
        const Icon = meta?.icon ?? FolderTree
        return (
          <span className="flex items-center gap-2">
            <Icon className="size-4 shrink-0 text-muted-foreground" />
            {info.getValue()}
          </span>
        )
      },
    }),
    columnHelper.accessor('code', { header: '编码', size: 120 }),
    columnHelper.accessor('type', {
      header: '类型',
      size: 90,
      cell: (info) => {
        const meta = ORG_TYPE_META[info.getValue()]
        return (
          <Badge variant={meta?.variant ?? 'secondary'} size="sm">
            {meta?.label ?? info.getValue()}
          </Badge>
        )
      },
    }),
    columnHelper.accessor('leaderName', {
      header: '负责人',
      size: 120,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('sort', { header: '排序', size: 70 }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 220,
      cell: (info) => (
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-primary"
            onClick={() => openCreate(info.row.original.id)}
          >
            <Plus className="size-3.5" />
            添加子组织
          </Button>
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
          data={orgUnits}
          total={orgUnits.length}
          page={1}
          pageSize={orgUnits.length || 10}
          loading={loading}
          onPageChange={() => {}}
          onPageSizeChange={() => {}}
          getRowId={(row) => String(row.id)}
          getSubRows={(row) => row.children}
          toolbar={
            <div className="flex flex-wrap items-end gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">组织名称</Label>
                <Input
                  placeholder="组织名称"
                  className="w-[150px]"
                  value={searchName}
                  onChange={(event) => setSearchName(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">组织编码</Label>
                <Input
                  placeholder="组织编码"
                  className="w-[150px]"
                  value={searchCode}
                  onChange={(event) => setSearchCode(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">类型</Label>
                <Select
                  value={searchType || 'ALL'}
                  onValueChange={(value) =>
                    setSearchType(value === 'ALL' ? '' : (value as OrgUnitType))
                  }
                >
                  <SelectTrigger className="w-[120px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">全部</SelectItem>
                    {TYPE_OPTIONS.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">状态</Label>
                <Select
                  value={searchStatus || 'ALL'}
                  onValueChange={(value) =>
                    setSearchStatus(value === 'ALL' ? '' : (value as Status))
                  }
                >
                  <SelectTrigger className="w-[110px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">全部</SelectItem>
                    {STATUS_OPTIONS.map((option) => (
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
              <Button className="ml-auto" onClick={() => openCreate()}>
                <Plus className="size-4" />
                新增组织
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <OrgUnitFormDialog
          key={editing?.id ?? `new-${createParentId ?? 'root'}`}
          editing={editing}
          defaultParentId={createParentId}
          orgTree={orgUnits}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除组织「${deleting?.name ?? ''}」吗？删除后不可恢复。`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 组织表单弹窗：表单实例由本组件创建并持有 */
function OrgUnitFormDialog({
  editing,
  defaultParentId,
  orgTree,
  onOpenChange,
  onSubmit,
}: {
  editing: OrgUnit | null
  defaultParentId: number | undefined
  orgTree: OrgUnit[]
  onOpenChange: (open: boolean) => void
  onSubmit: (values: OrgUnitFormValues) => Promise<void>
}) {
  const form = useForm<OrgUnitFormValues>({
    defaultValues: editing
      ? {
          parentId: editing.parentId,
          name: editing.name,
          code: editing.code,
          type: editing.type,
          leaderId: editing.leaderId ? String(editing.leaderId) : '',
          sort: editing.sort,
          status: editing.status,
        }
      : { ...emptyForm, parentId: defaultParentId },
  })

  return (
    <FormDialog<OrgUnitFormValues>
      open
      title={editing ? '编辑组织' : '新增组织'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="parentId">上级组织</Label>
          <Controller
            control={form.control}
            name="parentId"
            render={({ field }) => (
              <TreeSelectPopover
                nodes={orgTree}
                value={field.value}
                onChange={field.onChange}
                placeholder="请选择上级组织（可选）"
              />
            )}
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="name">组织名称</Label>
          <Input
            id="name"
            placeholder="请输入组织名称"
            {...form.register('name', { required: '请输入组织名称' })}
          />
          {form.formState.errors.name && (
            <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="code">组织编码</Label>
          <Input
            id="code"
            placeholder="请输入组织编码"
            {...form.register('code', { required: '请输入组织编码' })}
          />
          {form.formState.errors.code && (
            <p className="text-xs text-destructive">{form.formState.errors.code.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="type">类型</Label>
          <Controller
            control={form.control}
            name="type"
            render={({ field }) => (
              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger id="type">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {TYPE_OPTIONS.map((option) => (
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
          <Label htmlFor="leaderId">负责人用户 ID</Label>
          <Input
            id="leaderId"
            type="number"
            placeholder="请输入负责人用户 ID（可选）"
            {...form.register('leaderId', {
              validate: (value) => !value || Number(value) > 0 || '请输入有效的用户 ID',
            })}
          />
          {form.formState.errors.leaderId && (
            <p className="text-xs text-destructive">{form.formState.errors.leaderId.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="sort">排序</Label>
          <Input
            id="sort"
            type="number"
            min={0}
            max={9999}
            {...form.register('sort', { valueAsNumber: true, min: 0, max: 9999 })}
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

export default OrgUnitListPage
