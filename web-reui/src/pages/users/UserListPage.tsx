import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm } from 'react-hook-form'
import { Plus, RotateCw, Pencil, Search, Trash2 } from 'lucide-react'
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
import { StatusBadge } from '@/components/StatusBadge'
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listUsers, createUser, updateUser, deleteUser } from '@/api/user'
import type { User, CreateUserRequest, UpdateUserRequest, UserQueryParams } from '@/types/user'
import type { Status } from '@/types/api'

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS: { label: string; value: Status }[] = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'INACTIVE' },
]

/** 用户表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type UserFormValues = {
  username: string
  nickname: string
  password: string
  email: string
  phone: string
  status: Status
}

const emptyForm: UserFormValues = {
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  status: 'ACTIVE',
}

/** 用户管理页面 */
const UserListPage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    usePagedList<User, UserQueryParams>({
      fetcher: listUsers,
      initialSize: 10,
    })

  // 搜索条件
  const [searchUsername, setSearchUsername] = useState('')
  const [searchNickname, setSearchNickname] = useState('')
  const [searchStatus, setSearchStatus] = useState<Status | ''>('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<User | null>(null)
  const [deleting, setDeleting] = useState<User | null>(null)

  const handleSearch = () => {
    setQuery({
      username: searchUsername || undefined,
      nickname: searchNickname || undefined,
      status: searchStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchUsername('')
    setSearchNickname('')
    setSearchStatus('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: User) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: UserFormValues) => {
    if (editing) {
      const updateData: UpdateUserRequest = {
        nickname: values.nickname,
        email: values.email || undefined,
        phone: values.phone || undefined,
        status: values.status,
      }
      await updateUser(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: CreateUserRequest = {
        username: values.username,
        nickname: values.nickname,
        password: values.password,
        email: values.email || undefined,
        phone: values.phone || undefined,
        status: values.status,
      }
      await createUser(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteUser(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, User>()
  const columns = columnHelper.columns([
    columnHelper.accessor('username', { header: '用户名', size: 120 }),
    columnHelper.accessor('nickname', { header: '昵称', size: 120 }),
    columnHelper.accessor('email', {
      header: '邮箱',
      size: 180,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('phone', {
      header: '手机',
      size: 140,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('lastLoginAt', {
      header: '最后登录时间',
      size: 180,
      cell: (info) =>
        info.getValue() ? dayjs(info.getValue() as string).format('YYYY-MM-DD HH:mm:ss') : '-',
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
                <Label className="text-xs text-muted-foreground">用户名</Label>
                <Input
                  placeholder="用户名"
                  className="w-[150px]"
                  value={searchUsername}
                  onChange={(event) => setSearchUsername(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">昵称</Label>
                <Input
                  placeholder="昵称"
                  className="w-[150px]"
                  value={searchNickname}
                  onChange={(event) => setSearchNickname(event.target.value)}
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
                新增用户
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <UserFormDialog
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
        description={`确定要删除用户「${deleting?.username ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 用户表单弹窗：表单实例由本组件创建并持有 */
function UserFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: User | null
  onOpenChange: (open: boolean) => void
  onSubmit: (values: UserFormValues) => Promise<void>
}) {
  const form = useForm<UserFormValues>({
    defaultValues: editing
      ? {
          username: editing.username,
          nickname: editing.nickname,
          password: '',
          email: editing.email ?? '',
          phone: editing.phone ?? '',
          status: editing.status,
        }
      : emptyForm,
  })

  return (
    <FormDialog<UserFormValues>
      open
      title={editing ? '编辑用户' : '新增用户'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="username">用户名</Label>
          <Input
            id="username"
            placeholder="请输入用户名"
            disabled={!!editing}
            {...form.register('username', {
              required: '请输入用户名',
              minLength: { value: 3, message: '用户名长度为 3-50 个字符' },
              maxLength: { value: 50, message: '用户名长度为 3-50 个字符' },
            })}
          />
          {form.formState.errors.username && (
            <p className="text-xs text-destructive">{form.formState.errors.username.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="nickname">昵称</Label>
          <Input
            id="nickname"
            placeholder="请输入昵称"
            {...form.register('nickname', { required: '请输入昵称' })}
          />
          {form.formState.errors.nickname && (
            <p className="text-xs text-destructive">{form.formState.errors.nickname.message}</p>
          )}
        </div>

        {!editing && (
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="password">密码</Label>
            <Input
              id="password"
              type="password"
              placeholder="请输入密码"
              {...form.register('password', {
                required: '请输入密码',
                minLength: { value: 6, message: '密码长度为 6-100 个字符' },
                maxLength: { value: 100, message: '密码长度为 6-100 个字符' },
              })}
            />
            {form.formState.errors.password && (
              <p className="text-xs text-destructive">{form.formState.errors.password.message}</p>
            )}
          </div>
        )}

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

export default UserListPage
