import { useEffect, useState } from 'react'
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
import { TreeSelectPopover } from '@/components/TreeSelectPopover'
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listEmployees, createEmployee, updateEmployee, deleteEmployee } from '@/api/employee'
import { listOrgUnits } from '@/api/orgUnit'
import type {
  Employee,
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeQueryParams,
} from '@/types/employee'
import type { OrgUnit } from '@/types/orgUnit'
import type { Status } from '@/types/api'

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS: { label: string; value: Status }[] = [
  { label: '启用', value: 'ACTIVE' },
  { label: '停用', value: 'INACTIVE' },
]

/** 员工表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type EmployeeFormValues = {
  userId: string
  name: string
  employeeNo: string
  orgUnitId: number | undefined
  position: string
  email: string
  phone: string
  status: Status
}

const emptyForm: EmployeeFormValues = {
  userId: '',
  name: '',
  employeeNo: '',
  orgUnitId: undefined,
  position: '',
  email: '',
  phone: '',
  status: 'ACTIVE',
}

/** 员工管理页面 */
const EmployeeListPage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    usePagedList<Employee, EmployeeQueryParams>({
      fetcher: listEmployees,
      initialSize: 10,
    })

  // 组织树（搜索条件与表单共用）
  const [orgTree, setOrgTree] = useState<OrgUnit[]>()
  useEffect(() => {
    void listOrgUnits().then(setOrgTree).catch(() => setOrgTree([]))
  }, [])

  // 搜索条件
  const [searchKeyword, setSearchKeyword] = useState('')
  const [searchOrgUnitId, setSearchOrgUnitId] = useState<number | undefined>(undefined)
  const [searchStatus, setSearchStatus] = useState<Status | ''>('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Employee | null>(null)
  const [deleting, setDeleting] = useState<Employee | null>(null)

  const handleSearch = () => {
    setQuery({
      keyword: searchKeyword || undefined,
      orgId: searchOrgUnitId,
      employmentStatus: searchStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchKeyword('')
    setSearchOrgUnitId(undefined)
    setSearchStatus('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: Employee) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: EmployeeFormValues) => {
    const base = {
      name: values.name,
      employeeNo: values.employeeNo,
      orgUnitId: values.orgUnitId || undefined,
      position: values.position || undefined,
      email: values.email || undefined,
      phone: values.phone || undefined,
      status: values.status,
    }
    if (editing) {
      await updateEmployee(editing.id, base as UpdateEmployeeRequest)
      toast.success('更新成功')
    } else {
      const createData: CreateEmployeeRequest = {
        ...base,
        userId: Number(values.userId),
      }
      await createEmployee(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteEmployee(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, Employee>()
  const columns = columnHelper.columns([
    columnHelper.accessor('employeeNo', { header: '工号', size: 100 }),
    columnHelper.accessor('name', { header: '姓名', size: 120 }),
    columnHelper.accessor('username', { header: '用户名', size: 120 }),
    columnHelper.accessor('orgUnitName', {
      header: '所属组织',
      size: 150,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('position', {
      header: '职位',
      size: 150,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('email', {
      header: '邮箱',
      size: 200,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('phone', {
      header: '手机',
      size: 150,
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
                <Label className="text-xs text-muted-foreground">关键词</Label>
                <Input
                  placeholder="姓名 / 工号 / 手机号"
                  className="w-[180px]"
                  value={searchKeyword}
                  onChange={(event) => setSearchKeyword(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">组织</Label>
                <TreeSelectPopover
                  nodes={orgTree}
                  value={searchOrgUnitId}
                  onChange={setSearchOrgUnitId}
                  placeholder="全部组织"
                  className="w-[180px]"
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
              <Button className="ml-auto" onClick={openCreate}>
                <Plus className="size-4" />
                新增员工
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <EmployeeFormDialog
          key={editing?.id ?? 'new'}
          editing={editing}
          orgTree={orgTree}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除员工「${deleting?.name ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 员工表单弹窗：表单实例由本组件创建并持有 */
function EmployeeFormDialog({
  editing,
  orgTree,
  onOpenChange,
  onSubmit,
}: {
  editing: Employee | null
  orgTree: OrgUnit[] | undefined
  onOpenChange: (open: boolean) => void
  onSubmit: (values: EmployeeFormValues) => Promise<void>
}) {
  const form = useForm<EmployeeFormValues>({
    defaultValues: editing
      ? {
          userId: String(editing.userId),
          name: editing.name,
          employeeNo: editing.employeeNo,
          orgUnitId: editing.orgUnitId,
          position: editing.position ?? '',
          email: editing.email ?? '',
          phone: editing.phone ?? '',
          status: editing.status,
        }
      : emptyForm,
  })

  return (
    <FormDialog<EmployeeFormValues>
      open
      title={editing ? '编辑员工' : '新增员工'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="userId">关联用户 ID</Label>
          <Input
            id="userId"
            type="number"
            placeholder="请输入关联用户 ID"
            disabled={!!editing}
            {...form.register('userId', {
              required: '请输入关联用户 ID',
              validate: (value) => Number(value) > 0 || '请输入有效的用户 ID',
            })}
          />
          {form.formState.errors.userId && (
            <p className="text-xs text-destructive">{form.formState.errors.userId.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="name">姓名</Label>
          <Input
            id="name"
            placeholder="请输入姓名"
            {...form.register('name', { required: '请输入姓名' })}
          />
          {form.formState.errors.name && (
            <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="employeeNo">工号</Label>
          <Input
            id="employeeNo"
            placeholder="请输入工号"
            {...form.register('employeeNo', { required: '请输入工号' })}
          />
          {form.formState.errors.employeeNo && (
            <p className="text-xs text-destructive">{form.formState.errors.employeeNo.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="orgUnitId">所属组织</Label>
          <Controller
            control={form.control}
            name="orgUnitId"
            render={({ field }) => (
              <TreeSelectPopover
                nodes={orgTree}
                value={field.value}
                onChange={field.onChange}
                placeholder="请选择组织（可选）"
              />
            )}
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="position">职位</Label>
          <Input id="position" placeholder="请输入职位" {...form.register('position')} />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="email">邮箱</Label>
          <Input
            id="email"
            placeholder="请输入邮箱"
            {...form.register('email', {
              pattern: {
                value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                message: '请输入有效的邮箱地址',
              },
            })}
          />
          {form.formState.errors.email && (
            <p className="text-xs text-destructive">{form.formState.errors.email.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="phone">手机</Label>
          <Input id="phone" placeholder="请输入手机号" {...form.register('phone')} />
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

export default EmployeeListPage
