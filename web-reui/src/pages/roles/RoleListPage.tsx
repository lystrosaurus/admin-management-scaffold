import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import { Plus, RotateCw, Pencil, Search, Settings, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import dayjs from 'dayjs'
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
import { PermissionAssignDialog } from '@/components/PermissionAssignDialog'
import { StatusBadge } from '@/components/StatusBadge'
import { useClientPagedList } from '@/hooks/useClientPagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listRoles, createRole, updateRole, deleteRole } from '@/api/role'
import type { Role, CreateRoleRequest, UpdateRoleRequest, RoleQueryParams } from '@/types/role'
import type { Status } from '@/types/api'

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS: { label: string; value: Status }[] = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
]

/** 角色表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type RoleFormValues = {
  code: string
  name: string
  description: string
  status: Status
}

const emptyForm: RoleFormValues = {
  code: '',
  name: '',
  description: '',
  status: 'ACTIVE',
}

/** 客户端过滤：角色编码/名称模糊匹配 + 状态精确匹配 */
const filterRoles = (rows: Role[], query: RoleQueryParams): Role[] => {
  const code = query.code?.trim().toLowerCase()
  const name = query.name?.trim().toLowerCase()
  return rows.filter((row) => {
    if (code && !row.code.toLowerCase().includes(code)) return false
    if (name && !row.name.toLowerCase().includes(name)) return false
    if (query.status && row.status !== query.status) return false
    return true
  })
}

/** 角色管理页面 */
const RoleListPage = () => {
  // 后端 roles 接口返回全量数组，采用客户端过滤 + 分页
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    useClientPagedList<Role, RoleQueryParams>({
      fetcher: listRoles,
      filter: filterRoles,
      initialSize: 10,
    })

  // 搜索条件
  const [searchCode, setSearchCode] = useState('')
  const [searchName, setSearchName] = useState('')
  const [searchStatus, setSearchStatus] = useState<Status | ''>('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Role | null>(null)
  const [deleting, setDeleting] = useState<Role | null>(null)

  // 权限分配弹窗状态
  const [permOpen, setPermOpen] = useState(false)
  const [permRoleId, setPermRoleId] = useState<number | null>(null)
  const [permRoleName, setPermRoleName] = useState<string>()

  const handleSearch = () => {
    setQuery({
      code: searchCode || undefined,
      name: searchName || undefined,
      status: searchStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchCode('')
    setSearchName('')
    setSearchStatus('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: Role) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const openPermission = (record: Role) => {
    setPermRoleId(record.id)
    setPermRoleName(record.name)
    setPermOpen(true)
  }

  const handleSubmit = async (values: RoleFormValues) => {
    if (editing) {
      const updateData: UpdateRoleRequest = {
        name: values.name,
        description: values.description || undefined,
        status: values.status,
      }
      await updateRole(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: CreateRoleRequest = {
        code: values.code,
        name: values.name,
        description: values.description || undefined,
        status: values.status,
      }
      await createRole(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteRole(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, Role>()
  const columns = columnHelper.columns([
    columnHelper.accessor('code', { header: '角色编码', size: 150 }),
    columnHelper.accessor('name', { header: '角色名称', size: 150 }),
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
    columnHelper.accessor('createdAt', {
      header: '创建时间',
      size: 180,
      cell: (info) =>
        info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-',
    }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 200,
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
            className="h-7 px-2"
            onClick={() => openPermission(info.row.original)}
          >
            <Settings className="size-3.5" />
            权限
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
                <Label className="text-xs text-muted-foreground">角色编码</Label>
                <Input
                  placeholder="角色编码"
                  className="w-[150px]"
                  value={searchCode}
                  onChange={(event) => setSearchCode(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">角色名称</Label>
                <Input
                  placeholder="角色名称"
                  className="w-[150px]"
                  value={searchName}
                  onChange={(event) => setSearchName(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">状态</Label>
                <Select
                  value={searchStatus || 'ALL'}
                  onValueChange={(value) =>
                    setSearchStatus(value === 'ALL' ? '' : (value as Status))
                  }
                >
                  <SelectTrigger className="w-[120px]">
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
              <Button className="ml-auto" onClick={openCreate}>
                <Plus className="size-4" />
                新增角色
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <RoleFormDialog
          key={editing?.id ?? 'new'}
          editing={editing}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 权限分配弹窗 */}
      <PermissionAssignDialog
        open={permOpen}
        roleId={permRoleId}
        roleName={permRoleName}
        onOpenChange={setPermOpen}
      />

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除角色「${deleting?.name ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 角色表单弹窗：表单实例由本组件创建并持有 */
function RoleFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: Role | null
  onOpenChange: (open: boolean) => void
  onSubmit: (values: RoleFormValues) => Promise<void>
}) {
  const form = useForm<RoleFormValues>({
    defaultValues: editing
      ? {
          code: editing.code,
          name: editing.name,
          description: editing.description ?? '',
          status: editing.status,
        }
      : emptyForm,
  })

  return (
    <FormDialog<RoleFormValues>
      open
      title={editing ? '编辑角色' : '新增角色'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="code">角色编码</Label>
          <Input
            id="code"
            placeholder="请输入角色编码"
            disabled={!!editing}
            {...form.register('code', {
              required: '请输入角色编码',
              minLength: { value: 2, message: '角色编码至少 2 个字符' },
              maxLength: { value: 50, message: '角色编码最多 50 个字符' },
            })}
          />
          {form.formState.errors.code && (
            <p className="text-xs text-destructive">{form.formState.errors.code.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="name">角色名称</Label>
          <Input
            id="name"
            placeholder="请输入角色名称"
            {...form.register('name', { required: '请输入角色名称' })}
          />
          {form.formState.errors.name && (
            <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
          )}
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

export default RoleListPage
