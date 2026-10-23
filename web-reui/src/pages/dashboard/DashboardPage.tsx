import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import {
  KeyRound,
  Landmark,
  LayoutDashboard,
  Menu as MenuIcon,
  Settings,
  User,
  Users,
} from 'lucide-react'
import dayjs from 'dayjs'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'
import { getOperationLogs } from '@/api/auditLog'
import type { OperationLog } from '@/types/auditLog'
import { useAuthStore } from '@/stores/authStore'

/** 快速入口配置 */
const quickLinks = [
  { title: '用户管理', icon: User, path: '/users' },
  { title: '角色管理', icon: KeyRound, path: '/roles' },
  { title: '菜单管理', icon: MenuIcon, path: '/menus' },
  { title: '系统配置', icon: Settings, path: '/settings' },
]

/** 统计卡片配置（数据来自后端 /app/dashboard/stats） */
const statCards = [
  { key: 'userCount', label: '用户总数', suffix: '人', icon: Users, color: 'var(--chart-1)' },
  { key: 'roleCount', label: '角色总数', suffix: '个', icon: KeyRound, color: 'var(--chart-2)' },
  { key: 'employeeCount', label: '员工总数', suffix: '人', icon: User, color: 'var(--chart-3)' },
  { key: 'orgUnitCount', label: '组织总数', suffix: '个', icon: Landmark, color: 'var(--chart-4)' },
] as const

/** 仪表盘页面 */
const DashboardPage = () => {
  const user = useAuthStore((s) => s.user)
  const username = user?.nickname || user?.username || '管理员'

  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [recentLogs, setRecentLogs] = useState<OperationLog[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    Promise.all([getDashboardStats(), getOperationLogs({ size: 8 })])
      .then(([statsData, logsData]) => {
        if (cancelled) return
        setStats(statsData)
        setRecentLogs(logsData.items)
      })
      .catch(() => {
        // 统计失败时保持占位，不阻塞页面
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  /** 图表数据（真实统计值） */
  const chartData = statCards.map((card) => ({
    name: card.label,
    value: stats ? stats[card.key] : 0,
    fill: card.color,
  }))

  return (
    <div className="flex flex-col gap-6">
      {/* 欢迎信息 */}
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">仪表盘</h1>
        <p className="text-sm text-muted-foreground">欢迎回来，{username}</p>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {statCards.map((card) => {
          const Icon = card.icon
          return (
            <Card key={card.key}>
              <CardContent className="flex flex-col gap-2 pt-6">
                <div className="flex items-center justify-between">
                  <span className="text-xs text-muted-foreground">{card.label}</span>
                  <span
                    className="flex size-8 items-center justify-center rounded-md"
                    style={{ backgroundColor: `color-mix(in oklab, ${card.color} 15%, transparent)` }}
                  >
                    <Icon className="size-4" style={{ color: card.color }} />
                  </span>
                </div>
                {loading && !stats ? (
                  <Skeleton className="h-8 w-16" />
                ) : (
                  <span className="text-2xl font-semibold tabular-nums">
                    {stats?.[card.key] ?? 0}
                    <span className="ml-1 text-sm font-normal text-muted-foreground">
                      {card.suffix}
                    </span>
                  </span>
                )}
              </CardContent>
            </Card>
          )
        })}
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 资源分布图表（真实统计数据） */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <LayoutDashboard className="size-4" />
              资源分布
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading && !stats ? (
              <Skeleton className="h-[260px] w-full" />
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={chartData} margin={{ top: 8, right: 8, bottom: 0, left: -16 }}>
                  <CartesianGrid
                    strokeDasharray="3 3"
                    vertical={false}
                    stroke="var(--border)"
                  />
                  <XAxis
                    dataKey="name"
                    tickLine={false}
                    axisLine={false}
                    fontSize={12}
                    stroke="var(--muted-foreground)"
                  />
                  <YAxis
                    allowDecimals={false}
                    tickLine={false}
                    axisLine={false}
                    fontSize={12}
                    stroke="var(--muted-foreground)"
                  />
                  <Tooltip
                    contentStyle={{
                      borderRadius: 8,
                      border: '1px solid var(--border)',
                      background: 'var(--popover)',
                      color: 'var(--popover-foreground)',
                      fontSize: 12,
                    }}
                    formatter={(value) => [String(value ?? ''), '数量']}
                  />
                  <Bar dataKey="value" radius={[4, 4, 0, 0]} maxBarSize={48} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>

        {/* 快速入口 */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">快速入口</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-3">
              {quickLinks.map((link) => {
                const Icon = link.icon
                return (
                  <Link
                    key={link.path}
                    to={link.path}
                    className="flex items-center gap-3 rounded-lg border p-4 transition-colors hover:bg-accent"
                  >
                    <span className="flex size-9 items-center justify-center rounded-md bg-primary/10 text-primary">
                      <Icon className="size-4" />
                    </span>
                    <span className="text-sm font-medium">{link.title}</span>
                  </Link>
                )
              })}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 最近操作日志（真实数据） */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">最近操作</CardTitle>
        </CardHeader>
        <CardContent>
          {recentLogs.length === 0 ? (
            <p className="py-6 text-center text-sm text-muted-foreground">
              {loading ? '加载中…' : '暂无操作记录'}
            </p>
          ) : (
            <div className="flex flex-col divide-y">
              {recentLogs.map((log) => (
                <div key={log.id} className="flex items-center gap-4 py-2.5 text-sm">
                  <span className="w-32 shrink-0 truncate font-medium">{log.username}</span>
                  <span className="w-20 shrink-0 text-muted-foreground">{log.module}</span>
                  <span className="flex-1 truncate">{log.action}</span>
                  <span className="shrink-0 text-xs text-muted-foreground">
                    {dayjs(log.opAt).format('MM-DD HH:mm')}
                  </span>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default DashboardPage
