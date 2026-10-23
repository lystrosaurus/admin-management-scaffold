import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { RotateCw, Search } from 'lucide-react'
import dayjs from 'dayjs'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { DataTable } from '@/components/data-table/DataTable'
import { Badge } from '@/components/reui/badge'
import { usePagedList } from '@/hooks/usePagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import { getLoginLogs, getOperationLogs } from '@/api/auditLog'
import type { LoginLog, LoginLogQueryParams, OperationLog, OperationLogQueryParams } from '@/types/auditLog'

/**
 * 审计日志页面
 * 登录日志 / 操作日志两个页签，共用「用户名 + 时间范围」筛选与服务端分页。
 * 时间范围使用原生 datetime-local 输入，转 ISO 字符串传后端。
 */

/** 时间范围值（datetime-local 的 value 为 'YYYY-MM-DDTHH:mm' 本地格式） */
interface TimeRange {
  start: string
  end: string
}

/** datetime-local 值 → ISO 字符串；空值返回 undefined */
const toISO = (value: string): string | undefined =>
  value ? new Date(value).toISOString() : undefined

/** 时间筛选栏（两个页签共用） */
function TimeFilterBar({
  username,
  onUsernameChange,
  range,
  onRangeChange,
  onSearch,
  onReset,
}: {
  username: string
  onUsernameChange: (value: string) => void
  range: TimeRange
  onRangeChange: (range: TimeRange) => void
  onSearch: () => void
  onReset: () => void
}) {
  return (
    <div className="flex flex-wrap items-end gap-3">
      <div className="flex flex-col gap-1.5">
        <Label className="text-xs text-muted-foreground">用户名</Label>
        <Input
          placeholder="用户名"
          className="w-[160px]"
          value={username}
          onChange={(event) => onUsernameChange(event.target.value)}
          onKeyDown={(event) => event.key === 'Enter' && onSearch()}
        />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label className="text-xs text-muted-foreground">开始时间</Label>
        <Input
          type="datetime-local"
          className="w-[220px]"
          value={range.start}
          onChange={(event) => onRangeChange({ ...range, start: event.target.value })}
        />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label className="text-xs text-muted-foreground">结束时间</Label>
        <Input
          type="datetime-local"
          className="w-[220px]"
          value={range.end}
          onChange={(event) => onRangeChange({ ...range, end: event.target.value })}
        />
      </div>
      <div className="flex gap-2">
        <Button onClick={onSearch}>
          <Search className="size-4" />
          搜索
        </Button>
        <Button variant="outline" onClick={onReset}>
          <RotateCw className="size-4" />
          重置
        </Button>
      </div>
    </div>
  )
}

/** 登录日志表格 */
function LoginLogTable() {
  const { rows, total, page, size, loading, setPage, setSize, setQuery } =
    usePagedList<LoginLog, LoginLogQueryParams>({
      fetcher: getLoginLogs,
      initialSize: 10,
    })

  const [username, setUsername] = useState('')
  const [range, setRange] = useState<TimeRange>({ start: '', end: '' })

  const handleSearch = () => {
    setQuery({
      username: username || undefined,
      startTime: toISO(range.start),
      endTime: toISO(range.end),
    })
  }

  const handleReset = () => {
    setUsername('')
    setRange({ start: '', end: '' })
    setQuery({})
  }

  const columnHelper = createColumnHelper<typeof coreTableFeatures, LoginLog>()
  const columns = columnHelper.columns([
    columnHelper.accessor('username', { header: '用户名', size: 140 }),
    columnHelper.accessor('ip', { header: 'IP', size: 140 }),
    columnHelper.accessor('success', {
      header: '是否成功',
      size: 100,
      cell: (info) =>
        info.getValue() ? (
          <Badge variant="success" size="sm">
            成功
          </Badge>
        ) : (
          <Badge variant="destructive" size="sm">
            失败
          </Badge>
        ),
    }),
    columnHelper.accessor('loginAt', {
      header: '登录时间',
      size: 180,
      cell: (info) => (info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-'),
    }),
    columnHelper.accessor('userAgent', {
      header: 'UA',
      cell: (info) => (
        <span className="block max-w-[300px] truncate" title={info.getValue() || ''}>
          {info.getValue() || '-'}
        </span>
      ),
    }),
  ])

  return (
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
        <TimeFilterBar
          username={username}
          onUsernameChange={setUsername}
          range={range}
          onRangeChange={setRange}
          onSearch={handleSearch}
          onReset={handleReset}
        />
      }
    />
  )
}

/** 操作日志表格 */
function OperationLogTable() {
  const { rows, total, page, size, loading, setPage, setSize, setQuery } = usePagedList<
    OperationLog,
    OperationLogQueryParams
  >({
    fetcher: getOperationLogs,
    initialSize: 10,
  })

  const [username, setUsername] = useState('')
  const [range, setRange] = useState<TimeRange>({ start: '', end: '' })

  const handleSearch = () => {
    setQuery({
      username: username || undefined,
      startTime: toISO(range.start),
      endTime: toISO(range.end),
    })
  }

  const handleReset = () => {
    setUsername('')
    setRange({ start: '', end: '' })
    setQuery({})
  }

  const columnHelper = createColumnHelper<typeof coreTableFeatures, OperationLog>()
  const columns = columnHelper.columns([
    columnHelper.accessor('username', { header: '用户名', size: 140 }),
    columnHelper.accessor('module', { header: '模块', size: 120 }),
    columnHelper.accessor('action', { header: '动作', size: 140 }),
    columnHelper.accessor('bizId', { header: '业务 ID', size: 160 }),
    columnHelper.accessor('opAt', {
      header: '操作时间',
      size: 180,
      cell: (info) => (info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-'),
    }),
    columnHelper.accessor('ip', { header: 'IP', size: 140 }),
  ])

  return (
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
        <TimeFilterBar
          username={username}
          onUsernameChange={setUsername}
          range={range}
          onRangeChange={setRange}
          onSearch={handleSearch}
          onReset={handleReset}
        />
      }
    />
  )
}

/** 审计日志页面 */
const AuditLogPage = () => {
  return (
    <Card>
      <CardContent className="pt-6">
        <Tabs defaultValue="login">
          <TabsList>
            <TabsTrigger value="login">登录日志</TabsTrigger>
            <TabsTrigger value="operation">操作日志</TabsTrigger>
          </TabsList>
          <TabsContent value="login" className="pt-4">
            <LoginLogTable />
          </TabsContent>
          <TabsContent value="operation" className="pt-4">
            <OperationLogTable />
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}

export default AuditLogPage
