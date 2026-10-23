import { useCallback, useEffect, useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Controller, useForm, useWatch } from 'react-hook-form'
import {
  Folder,
  LayoutDashboard,
  Plus,
  RotateCw,
  Pencil,
  Search,
  ToggleLeft,
  Trash2,
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
import { listMenus, createMenu, updateMenu, deleteMenu } from '@/api/menu'
import type { Menu, CreateMenuRequest, UpdateMenuRequest, MenuQueryParams, MenuType } from '@/types/menu'
import type { Status } from '@/types/api'

/** 菜单类型元数据 */
const MENU_TYPE_META: Record<MenuType, { label: string; variant: 'info' | 'success' | 'warning'; icon: typeof Folder }> = {
  DIRECTORY: { label: '目录', variant: 'info', icon: Folder },
  MENU: { label: '菜单', variant: 'success', icon: LayoutDashboard },
  BUTTON: { label: '按钮', variant: 'warning', icon: ToggleLeft },
}

/** 类型选项（'' 表示全部） */
const TYPE_OPTIONS: { label: string; value: MenuType }[] = [
  { label: '目录', value: 'DIRECTORY' },
  { label: '菜单', value: 'MENU' },
  { label: '按钮', value: 'BUTTON' },
]

/** 状态选项（'' 表示全部） */
const STATUS_OPTIONS: { label: string; value: Status }[] = [
  { label: '启用', value: 'ACTIVE' },
  { label: '停用', value: 'INACTIVE' },
]

/** 菜单表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type MenuFormValues = {
  parentId: number | undefined
  name: string
  type: MenuType
  path: string
  component: string
  icon: string
  permission: string
  sort: number
  status: Status
}

const emptyForm: MenuFormValues = {
  parentId: undefined,
  name: '',
  type: 'DIRECTORY',
  path: '',
  component: '',
  icon: '',
  permission: '',
  sort: 0,
  status: 'ACTIVE',
}

/** 菜单管理页面 */
const MenuListPage = () => {
  // 菜单为树形全量数据（/app/menus/tree），列表本身不分页，仅沿用拉取状态
  const [menus, setMenus] = useState<Menu[]>([])
  const [loading, setLoading] = useState(true)

  // 搜索条件
  const [searchName, setSearchName] = useState('')
  const [searchType, setSearchType] = useState<MenuType | ''>('')
  const [searchStatus, setSearchStatus] = useState<Status | ''>('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Menu | null>(null)
  const [createParentId, setCreateParentId] = useState<number | undefined>(undefined)
  const [deleting, setDeleting] = useState<Menu | null>(null)

  const loadMenus = useCallback(async (params?: MenuQueryParams) => {
    try {
      const data = await listMenus(params)
      setMenus(data)
    } catch (err) {
      toast.error((err as Error).message || '加载菜单列表失败')
    } finally {
      setLoading(false)
    }
  }, [])

  /** 交互触发的重新拉取：loading 在事件中开启，effect 内不同步 setState */
  const reloadMenus = useCallback(
    (params?: MenuQueryParams) => {
      setLoading(true)
      void loadMenus(params)
    },
    [loadMenus],
  )

  // 首屏加载：状态更新在 promise 回调中完成，并带取消保护
  useEffect(() => {
    let cancelled = false
    listMenus()
      .then((data) => {
        if (!cancelled) setMenus(data)
      })
      .catch((err: Error) => {
        if (!cancelled) toast.error(err.message || '加载菜单列表失败')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const handleSearch = () => {
    reloadMenus({
      name: searchName || undefined,
      type: searchType || undefined,
      status: searchStatus || undefined,
    })
  }

  const handleReset = () => {
    setSearchName('')
    setSearchType('')
    setSearchStatus('')
    reloadMenus()
  }

  const openCreate = (parentId?: number) => {
    setEditing(null)
    setCreateParentId(parentId)
    setDialogOpen(true)
  }

  const openEdit = (record: Menu) => {
    setEditing(record)
    setCreateParentId(undefined)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: MenuFormValues) => {
    const base = {
      // 后端顶层约定 parentId=0 表示无上级，0 与 undefined 统一为 undefined 提交
      parentId: values.parentId || undefined,
      name: values.name,
      type: values.type,
      path: values.path || undefined,
      component: values.component || undefined,
      icon: values.icon || undefined,
      permission: values.permission || undefined,
      sort: values.sort,
      status: values.status,
    }
    if (editing) {
      await updateMenu(editing.id, base as UpdateMenuRequest)
      toast.success('更新成功')
    } else {
      await createMenu(base as CreateMenuRequest)
      toast.success('创建成功')
    }
    reloadMenus()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteMenu(deleting.id)
      toast.success('删除成功')
      reloadMenus()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, Menu>()
  const columns = columnHelper.columns([
    columnHelper.accessor('name', {
      header: '菜单名称',
      size: 220,
      cell: (info) => {
        const meta = MENU_TYPE_META[info.row.original.type]
        const Icon = meta?.icon ?? Folder
        return (
          <span className="flex items-center gap-2">
            <Icon className="size-4 shrink-0 text-muted-foreground" />
            {info.getValue()}
          </span>
        )
      },
    }),
    columnHelper.accessor('type', {
      header: '类型',
      size: 90,
      cell: (info) => {
        const meta = MENU_TYPE_META[info.getValue()]
        return (
          <Badge variant={meta?.variant ?? 'secondary'} size="sm">
            {meta?.label ?? info.getValue()}
          </Badge>
        )
      },
    }),
    columnHelper.accessor('path', {
      header: '路径',
      size: 150,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('component', {
      header: '组件',
      size: 200,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('permission', {
      header: '权限标识',
      size: 150,
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
            添加子项
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
          data={menus}
          total={menus.length}
          page={1}
          pageSize={menus.length || 10}
          loading={loading}
          onPageChange={() => {}}
          onPageSizeChange={() => {}}
          getRowId={(row) => String(row.id)}
          getSubRows={(row) => row.children}
          toolbar={
            <div className="flex flex-wrap items-end gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">菜单名称</Label>
                <Input
                  placeholder="菜单名称"
                  className="w-[150px]"
                  value={searchName}
                  onChange={(event) => setSearchName(event.target.value)}
                  onKeyDown={(event) => event.key === 'Enter' && handleSearch()}
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">类型</Label>
                <Select
                  value={searchType || 'ALL'}
                  onValueChange={(value) =>
                    setSearchType(value === 'ALL' ? '' : (value as MenuType))
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
              <Button className="ml-auto" onClick={() => openCreate()}>
                <Plus className="size-4" />
                新增菜单
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <MenuFormDialog
          key={editing?.id ?? `new-${createParentId ?? 'root'}`}
          editing={editing}
          defaultParentId={createParentId}
          onOpenChange={setDialogOpen}
          onSubmit={handleSubmit}
        />
      )}

      {/* 删除确认 */}
      <ConfirmDialog
        open={!!deleting}
        title="确认删除"
        description={`确定要删除菜单「${deleting?.name ?? ''}」吗？删除后不可恢复。`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 菜单表单弹窗：表单实例由本组件创建并持有；含类型联动的条件字段 */
function MenuFormDialog({
  editing,
  defaultParentId,
  onOpenChange,
  onSubmit,
}: {
  editing: Menu | null
  defaultParentId: number | undefined
  onOpenChange: (open: boolean) => void
  onSubmit: (values: MenuFormValues) => Promise<void>
}) {
  const form = useForm<MenuFormValues>({
    defaultValues: editing
      ? {
          parentId: editing.parentId,
          name: editing.name,
          type: editing.type,
          path: editing.path ?? '',
          component: editing.component ?? '',
          icon: editing.icon ?? '',
          permission: editing.permission ?? '',
          sort: editing.sort,
          status: editing.status,
        }
      : { ...emptyForm, parentId: defaultParentId },
  })

  // 表单类型联动：MENU 显示路径/组件；非 BUTTON 显示图标；BUTTON 显示权限标识
  const watchType = useWatch({ control: form.control, name: 'type' })
  // 上级菜单为受控展示，用 useWatch 订阅避免整表重渲染
  const watchParentId = useWatch({ control: form.control, name: 'parentId' })

  // 上级菜单树数据（弹窗打开时拉取）
  const [menuTree, setMenuTree] = useState<Menu[]>()
  useEffect(() => {
    void listMenus().then(setMenuTree).catch(() => setMenuTree([]))
  }, [])

  return (
    <FormDialog<MenuFormValues>
      open
      title={editing ? '编辑菜单' : '新增菜单'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="parentId">上级菜单</Label>
          <TreeSelectPopover
            nodes={menuTree}
            value={watchParentId}
            onChange={(value) => form.setValue('parentId', value)}
            placeholder="请选择上级菜单（可选）"
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="name">菜单名称</Label>
          <Input
            id="name"
            placeholder="请输入菜单名称"
            {...form.register('name', { required: '请输入菜单名称' })}
          />
          {form.formState.errors.name && (
            <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
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

        {watchType === 'MENU' && (
          <>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="path">路径</Label>
              <Input
                id="path"
                placeholder="请输入路径"
                {...form.register('path', { required: '请输入路径' })}
              />
              {form.formState.errors.path && (
                <p className="text-xs text-destructive">{form.formState.errors.path.message}</p>
              )}
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="component">组件路径</Label>
              <Input
                id="component"
                placeholder="请输入组件路径"
                {...form.register('component', { required: '请输入组件路径' })}
              />
              {form.formState.errors.component && (
                <p className="text-xs text-destructive">
                  {form.formState.errors.component.message}
                </p>
              )}
            </div>
          </>
        )}

        {watchType !== 'BUTTON' && (
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="icon">图标</Label>
            <Input id="icon" placeholder="图标名称（可选）" {...form.register('icon')} />
          </div>
        )}

        {watchType === 'BUTTON' && (
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="permission">权限标识</Label>
            <Input
              id="permission"
              placeholder="请输入权限标识"
              {...form.register('permission')}
            />
          </div>
        )}

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

export default MenuListPage
