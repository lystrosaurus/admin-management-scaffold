import type { ReactNode } from 'react'
import {
  type ColumnDef,
  flexRender,
  useTable,
} from '@tanstack/react-table'
import { ChevronDown, ChevronLeft, ChevronRight, Loader2 } from 'lucide-react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { cn } from 'cn'

const PAGE_SIZE_OPTIONS = [10, 20, 50, 100]

export interface DataTableProps<TData> {
  /**
   * TanStack v9 列定义（由页面的 createColumnHelper<features, TData> 构建）。
   * 此处 TData 用 any 宽松化：列的类型安全在建列处已由 helper 保证。
   */
  columns: ReadonlyArray<ColumnDef<typeof coreTableFeatures, any>>
  /** 当前页数据（服务端已分页；树形表格传全量根节点数组） */
  data: TData[]
  /** 总条数（树形表格传展开后的总节点数或根节点数均可，仅用于展示） */
  total: number
  /** 当前页码（1 起） */
  page: number
  /** 每页条数 */
  pageSize: number
  loading?: boolean
  onPageChange: (page: number) => void
  onPageSizeChange: (size: number) => void
  /** 表格上方工具栏（搜索区 / 操作按钮） */
  toolbar?: ReactNode
  emptyText?: string
  getRowId?: (row: TData, index: number) => string
  /**
   * 树形表格：返回行的子节点数组。传入后启用行展开/折叠。
   * 不传则为普通扁平表格。
   */
  getSubRows?: (row: TData) => TData[] | undefined
  /** 是否默认展开所有行（默认 true，仅树形表格生效） */
  defaultExpandAll?: boolean
  className?: string
}

/**
 * 服务端分页数据表格
 * 基于 TanStack Table v9（headless）+ shadcn Table 样式，与后端 PageResult 契约对齐。
 * 分页状态由页面通过 usePagedList 管理，表格仅负责当前页渲染。
 * 传入 getSubRows 时呈现为可展开的树形表格（菜单管理等层级数据）。
 */
export function DataTable<TData extends object>({
  columns,
  data,
  total,
  page,
  pageSize,
  loading = false,
  onPageChange,
  onPageSizeChange,
  toolbar,
  emptyText = '暂无数据',
  getRowId,
  getSubRows,
  defaultExpandAll = true,
  className,
}: DataTableProps<TData>) {
  // TanStack v9 的行数据约束（RowData）与业务 interface 类型不兼容，
  // 在壳体组件内以 never 断言桥接；列与行的类型安全由页面侧 createColumnHelper 保证。
  const table = useTable({
    features: coreTableFeatures,
    columns: columns as never,
    data: data as never,
    ...(getRowId ? { getRowId: getRowId as never } : {}),
    ...(getSubRows ? { getSubRows: getSubRows as never } : {}),
    ...(getSubRows && defaultExpandAll ? { state: { expanded: true } } : {}),
  })

  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  const colCount = columns.length
  const isTree = !!getSubRows

  return (
    <div className={cn('flex flex-col gap-4', className)}>
      {toolbar}

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead
                    key={header.id}
                    style={{ width: header.column.columnDef.size }}
                    className="whitespace-nowrap"
                  >
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={colCount} className="h-48 text-center">
                  <Loader2 className="mx-auto size-6 animate-spin text-muted-foreground" />
                </TableCell>
              </TableRow>
            ) : table.getRowModel().rows.length === 0 ? (
              <TableRow>
                <TableCell colSpan={colCount} className="h-32 text-center text-muted-foreground">
                  {emptyText}
                </TableCell>
              </TableRow>
            ) : (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id} data-depth={row.depth}>
                  {/* 树形表格：首列前插入展开控制列 */}
                  {isTree && (
                    <TableCell className="w-10 px-2">
                      {row.getCanExpand() ? (
                        <Button
                          variant="ghost"
                          size="icon"
                          className="size-6"
                          onClick={row.getToggleExpandedHandler()}
                          aria-label={row.getIsExpanded() ? '折叠' : '展开'}
                        >
                          <ChevronDown
                            className={cn(
                              'size-4 transition-transform',
                              !row.getIsExpanded() && '-rotate-90',
                            )}
                          />
                        </Button>
                      ) : null}
                    </TableCell>
                  )}
                  {row.getAllCells().map((cell, cellIndex) => (
                    <TableCell
                      key={cell.id}
                      style={
                        isTree && cellIndex === 0
                          ? { paddingLeft: `${row.depth * 20 + 12}px` }
                          : undefined
                      }
                    >
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* 分页栏（树形表格隐藏：层级数据一般不过长，与 antd 版行为一致） */}
      {!isTree && (
        <div className="flex flex-wrap items-center justify-between gap-2 text-sm text-muted-foreground">
          <span>共 {total} 条</span>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <span>每页</span>
              <Select
                value={String(pageSize)}
                onValueChange={(value) => onPageSizeChange(Number(value))}
              >
                <SelectTrigger className="h-8 w-[72px]" size="sm">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {PAGE_SIZE_OPTIONS.map((option) => (
                    <SelectItem key={option} value={String(option)}>
                      {option}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <span>
              第 {Math.min(page, totalPages)} / {totalPages} 页
            </span>
            <div className="flex items-center gap-1">
              <Button
                variant="outline"
                size="icon"
                className="size-8"
                disabled={page <= 1}
                onClick={() => onPageChange(page - 1)}
              >
                <ChevronLeft className="size-4" />
              </Button>
              <Button
                variant="outline"
                size="icon"
                className="size-8"
                disabled={page >= totalPages}
                onClick={() => onPageChange(page + 1)}
              >
                <ChevronRight className="size-4" />
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
