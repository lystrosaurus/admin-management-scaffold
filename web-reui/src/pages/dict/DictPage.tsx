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
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { listDict, createDict, updateDict, deleteDict } from '@/api/dict'
import type { DictVO, DictCreateDTO, DictUpdateDTO, DictQuery } from '@/types/dict'

/** 状态选项（'' 表示全部；字典域后端口径为 ENABLED / DISABLED） */
const STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLED' },
  { label: '禁用', value: 'DISABLED' },
]

/** 字典表单值（type 别名以获得隐式索引签名，满足 FormDialog 泛型约束） */
type DictFormValues = {
  dictType: string
  dictLabel: string
  dictValue: string
  sortOrder: number
  status: string
  remark: string
}

const emptyForm: DictFormValues = {
  dictType: '',
  dictLabel: '',
  dictValue: '',
  sortOrder: 0,
  status: 'ENABLED',
  remark: '',
}

/** 数据字典页面 */
const DictPage = () => {
  const { rows, total, page, size, loading, setPage, setSize, setQuery, refresh } =
    usePagedList<DictVO, DictQuery>({
      fetcher: listDict,
      initialSize: 10,
    })

  // 搜索条件
  const [searchDictType, setSearchDictType] = useState('')

  // 弹窗状态
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<DictVO | null>(null)
  const [deleting, setDeleting] = useState<DictVO | null>(null)

  const handleSearch = () => {
    setQuery({ dictType: searchDictType || undefined })
  }

  const handleReset = () => {
    setSearchDictType('')
    setQuery({})
  }

  const openCreate = () => {
    setEditing(null)
    setDialogOpen(true)
  }

  const openEdit = (record: DictVO) => {
    setEditing(record)
    setDialogOpen(true)
  }

  const handleSubmit = async (values: DictFormValues) => {
    if (editing) {
      const updateData: DictUpdateDTO = {
        dictType: values.dictType,
        dictLabel: values.dictLabel,
        dictValue: values.dictValue,
        sortOrder: values.sortOrder,
        status: values.status,
        remark: values.remark || undefined,
      }
      await updateDict(editing.id, updateData)
      toast.success('更新成功')
    } else {
      const createData: DictCreateDTO = {
        dictType: values.dictType,
        dictLabel: values.dictLabel,
        dictValue: values.dictValue,
        sortOrder: values.sortOrder,
        status: values.status,
        remark: values.remark || undefined,
      }
      await createDict(createData)
      toast.success('创建成功')
    }
    refresh()
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteDict(deleting.id)
      toast.success('删除成功')
      refresh()
    } finally {
      setDeleting(null)
    }
  }

  /** 表格列定义（TanStack v9 createColumnHelper 模式） */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, DictVO>()
  const columns = columnHelper.columns([
    columnHelper.accessor('dictType', { header: '字典类型', size: 140 }),
    columnHelper.accessor('dictLabel', { header: '字典标签', size: 140 }),
    columnHelper.accessor('dictValue', { header: '字典值', size: 140 }),
    columnHelper.accessor('sortOrder', {
      header: '排序',
      size: 80,
      cell: (info) => (info.getValue() ?? 0).toString(),
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 80,
      cell: (info) => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('remark', {
      header: '备注',
      size: 200,
      cell: (info) => info.getValue() || '-',
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
                <Label className="text-xs text-muted-foreground">字典类型</Label>
                <Input
                  placeholder="字典类型"
                  className="w-[200px]"
                  value={searchDictType}
                  onChange={(event) => setSearchDictType(event.target.value)}
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
                新增字典
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 新增 / 编辑弹窗（key 保证每次打开都以最新默认值挂载） */}
      {dialogOpen && (
        <DictFormDialog
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
        description={`确定要删除字典「${deleting?.dictLabel ?? ''}」吗？`}
        destructive
        onConfirm={handleDelete}
        onOpenChange={(open) => !open && setDeleting(null)}
      />
    </Card>
  )
}

/** 字典表单弹窗：表单实例由本组件创建并持有 */
function DictFormDialog({
  editing,
  onOpenChange,
  onSubmit,
}: {
  editing: DictVO | null
  onOpenChange: (open: boolean) => void
  onSubmit: (values: DictFormValues) => Promise<void>
}) {
  const form = useForm<DictFormValues>({
    defaultValues: editing
      ? {
          dictType: editing.dictType,
          dictLabel: editing.dictLabel,
          dictValue: editing.dictValue,
          sortOrder: editing.sortOrder,
          status: editing.status,
          remark: editing.remark ?? '',
        }
      : emptyForm,
  })

  return (
    <FormDialog<DictFormValues>
      open
      title={editing ? '编辑字典' : '新增字典'}
      submitText={editing ? '保存' : '创建'}
      form={form}
      onOpenChange={onOpenChange}
      onSubmit={onSubmit}
    >
      <>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="dictType">字典类型</Label>
          <Input
            id="dictType"
            placeholder="请输入字典类型"
            {...form.register('dictType', { required: '请输入字典类型' })}
          />
          {form.formState.errors.dictType && (
            <p className="text-xs text-destructive">{form.formState.errors.dictType.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="dictLabel">字典标签</Label>
          <Input
            id="dictLabel"
            placeholder="请输入字典标签"
            {...form.register('dictLabel', { required: '请输入字典标签' })}
          />
          {form.formState.errors.dictLabel && (
            <p className="text-xs text-destructive">{form.formState.errors.dictLabel.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="dictValue">字典值</Label>
          <Input
            id="dictValue"
            placeholder="请输入字典值"
            {...form.register('dictValue', { required: '请输入字典值' })}
          />
          {form.formState.errors.dictValue && (
            <p className="text-xs text-destructive">{form.formState.errors.dictValue.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="sortOrder">排序</Label>
          <Input
            id="sortOrder"
            type="number"
            min={0}
            {...form.register('sortOrder', { valueAsNumber: true, min: 0 })}
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

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="remark">备注</Label>
          <textarea
            id="remark"
            rows={3}
            placeholder="请输入备注"
            className="flex min-h-[60px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
            {...form.register('remark')}
          />
        </div>
      </>
    </FormDialog>
  )
}

export default DictPage
