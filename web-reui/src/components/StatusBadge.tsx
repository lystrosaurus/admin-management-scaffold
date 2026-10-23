import type { Status } from '@/types/api'
import { Badge } from '@/components/reui/badge'

/** 通用状态徽章：兼容 Status（ACTIVE/INACTIVE）与字典/配置域的 ENABLED/DISABLED 两种口径 */
const statusMap: Record<string, { variant: 'success' | 'secondary'; label: string }> = {
  ACTIVE: { variant: 'success', label: '启用' },
  INACTIVE: { variant: 'secondary', label: '禁用' },
  ENABLED: { variant: 'success', label: '启用' },
  DISABLED: { variant: 'secondary', label: '禁用' },
}

export function StatusBadge({ status }: { status: Status | string }) {
  const config = statusMap[status] ?? { variant: 'secondary' as const, label: status }
  return (
    <Badge variant={config.variant} size="sm">
      {config.label}
    </Badge>
  )
}

export default StatusBadge
