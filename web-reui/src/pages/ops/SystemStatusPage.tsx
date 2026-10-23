import { useEffect, useState } from 'react'
import { Activity, RefreshCw } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { getAppInfo } from '@/api/ops'
import type { AppInfo } from '@/types/ops'

/** 信息行（键值对） */
const InfoRow = ({ label, value }: { label: string; value: string }) => (
  <div className="flex flex-col gap-1 border-b py-3 last:border-b-0 sm:flex-row sm:items-center">
    <span className="text-sm text-muted-foreground sm:w-40">{label}</span>
    <span className="text-sm font-medium">{value}</span>
  </div>
)

/** 系统状态页面 */
const SystemStatusPage = () => {
  const [info, setInfo] = useState<AppInfo | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    getAppInfo()
      .then((data) => {
        if (!cancelled) setInfo(data)
      })
      .catch((error) => {
        // 静默处理：下方以「-」占位，避免页面空白
        if (!cancelled) setInfo(null)
        console.error(error)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  /** 手动刷新：loading 在事件中开启，避免 effect 内同步 setState */
  const refresh = () => {
    setLoading(true)
    getAppInfo()
      .then(setInfo)
      .catch((error) => {
        setInfo(null)
        console.error(error)
      })
      .finally(() => setLoading(false))
  }

  /** 统计卡数据 */
  const stats = [
    { label: '应用名', value: info?.name ?? '-' },
    { label: '版本', value: info?.version ?? '-' },
    { label: 'Java 版本', value: info?.javaVersion ?? '-' },
    {
      label: '运行状态',
      value: info?.status ?? '-',
      highlight: info?.status === 'UP',
    },
  ]

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">系统状态</h1>
          <p className="text-sm text-muted-foreground">应用运行时信息（OpsInfo）</p>
        </div>
        <Button
          variant="outline"
          onClick={refresh}
          disabled={loading}
        >
          <RefreshCw className={`size-4 ${loading ? 'animate-spin' : ''}`} />
          刷新
        </Button>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardContent className="flex flex-col gap-1 pt-6">
              <span className="text-xs text-muted-foreground">{stat.label}</span>
              {loading && !info ? (
                <Skeleton className="h-7 w-20" />
              ) : (
                <span
                  className={`text-xl font-semibold ${stat.highlight ? 'text-emerald-600 dark:text-emerald-400' : ''}`}
                >
                  {stat.value}
                </span>
              )}
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 详细信息 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <Activity className="size-4" />
            运行详情
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loading && !info ? (
            <div className="flex flex-col gap-3">
              {Array.from({ length: 6 }).map((_, index) => (
                <Skeleton key={index} className="h-5 w-full" />
              ))}
            </div>
          ) : (
            <div className="flex flex-col">
              <InfoRow label="应用名" value={info?.name ?? '-'} />
              <InfoRow label="版本" value={info?.version ?? '-'} />
              <InfoRow label="Java 版本" value={info?.javaVersion ?? '-'} />
              <InfoRow label="运行状态" value={info?.status ?? '-'} />
              <InfoRow label="操作系统" value={info?.osName ?? '-'} />
              <InfoRow label="系统架构" value={info?.osArch ?? '-'} />
              <InfoRow label="运行 Profile" value={info?.profile ?? '-'} />
              <InfoRow label="启动时间" value={info?.startTime ?? '-'} />
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default SystemStatusPage
