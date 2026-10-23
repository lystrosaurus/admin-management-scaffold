import { useState } from 'react'
import { createColumnHelper } from '@tanstack/react-table'
import { Check, RotateCw, X } from 'lucide-react'
import { toast } from 'sonner'
import dayjs from 'dayjs'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { DataTable } from '@/components/data-table/DataTable'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { useClientPagedList } from '@/hooks/useClientPagedList'
import { coreTableFeatures } from '@/components/data-table/table-features'
import {
  listPendingLinkCandidates,
  confirmLinkCandidate,
  rejectLinkCandidate,
} from '@/api/integration'
import type { LinkCandidateVO } from '@/types/integration'

/** 候选类型选项 */
const CANDIDATE_TYPE_OPTIONS = [
  { label: '员工', value: 'EMPLOYEE' },
  { label: '组织单元', value: 'ORG_UNIT' },
]

/** 候选状态文案（后端 PENDING / CONFIRMED / REJECTED） */
const STATUS_TEXT: Record<string, string> = {
  PENDING: '待处理',
  CONFIRMED: '已确认',
  REJECTED: '已拒绝',
}

/** 待处理动作类型 */
type PendingAction = { type: 'confirm' | 'reject'; record: LinkCandidateVO } | null

/** 身份匹配候选（待处理）页面 */
const LinkCandidatePage = () => {
  const { rows, total, page, size, loading, setPage, setSize, refresh } =
    useClientPagedList<LinkCandidateVO, Record<string, never>>({
      fetcher: () => listPendingLinkCandidates(),
      initialSize: 10,
    })

  // 待确认的操作弹窗
  const [pendingAction, setPendingAction] = useState<PendingAction>(null)
  const [processing, setProcessing] = useState(false)

  const handleAction = async () => {
    if (!pendingAction) return
    const { type, record } = pendingAction
    setProcessing(true)
    try {
      if (type === 'confirm') {
        await confirmLinkCandidate(record.id)
        toast.success('已确认')
      } else {
        await rejectLinkCandidate(record.id)
        toast.success('已拒绝')
      }
      refresh()
    } catch (error) {
      toast.error((error as Error).message || (type === 'confirm' ? '确认失败' : '拒绝失败'))
    } finally {
      setProcessing(false)
      setPendingAction(null)
    }
  }

  /** 表格列定义 */
  const columnHelper = createColumnHelper<typeof coreTableFeatures, LinkCandidateVO>()
  const columns = columnHelper.columns([
    columnHelper.accessor('id', { header: 'ID', size: 80 }),
    columnHelper.accessor('sourcePrincipalId', { header: '外部主体 ID', size: 120 }),
    columnHelper.accessor('candidateType', {
      header: '候选类型',
      size: 110,
      cell: (info) =>
        CANDIDATE_TYPE_OPTIONS.find((o) => o.value === info.getValue())?.label || info.getValue(),
    }),
    columnHelper.accessor('candidateId', { header: '候选目标 ID', size: 120 }),
    columnHelper.accessor('score', {
      header: '匹配分数',
      size: 100,
      cell: (info) => info.getValue() ?? '-',
    }),
    columnHelper.accessor('reason', {
      header: '匹配原因',
      size: 220,
      cell: (info) => info.getValue() || '-',
    }),
    columnHelper.accessor('status', {
      header: '状态',
      size: 100,
      cell: (info) => STATUS_TEXT[info.getValue()] || info.getValue(),
    }),
    columnHelper.accessor('createdAt', {
      header: '创建时间',
      size: 180,
      cell: (info) => (info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD HH:mm:ss') : '-'),
    }),
    columnHelper.display({
      id: 'actions',
      header: '操作',
      size: 160,
      cell: (info) => (
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-primary"
            disabled={processing}
            onClick={() => setPendingAction({ type: 'confirm', record: info.row.original })}
          >
            <Check className="size-3.5" />
            确认
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className="h-7 px-2 text-destructive"
            disabled={processing}
            onClick={() => setPendingAction({ type: 'reject', record: info.row.original })}
          >
            <X className="size-3.5" />
            拒绝
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
            <div className="flex items-center gap-2">
              <Button variant="outline" onClick={refresh}>
                <RotateCw className="size-4" />
                刷新
              </Button>
            </div>
          }
        />
      </CardContent>

      {/* 确认 / 拒绝操作二次确认 */}
      <ConfirmDialog
        open={!!pendingAction}
        title={pendingAction?.type === 'reject' ? '拒绝候选' : '确认候选'}
        description={
          pendingAction?.type === 'reject'
            ? '确定要拒绝该匹配候选吗？'
            : '确定要确认该匹配候选吗？'
        }
        destructive={pendingAction?.type === 'reject'}
        confirmText={pendingAction?.type === 'reject' ? '拒绝' : '确认'}
        onConfirm={handleAction}
        onOpenChange={(open) => !open && setPendingAction(null)}
      />
    </Card>
  )
}

export default LinkCandidatePage
