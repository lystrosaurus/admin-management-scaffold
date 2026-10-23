import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { toast } from 'sonner'

interface UseClientPagedListOptions<TData, TQuery extends object> {
  /** 拉取函数：一次性拉全量数据（后端未提供分页的域，如角色、菜单） */
  fetcher: (query: TQuery) => Promise<TData[]>
  /** 客户端过滤：输入全量数据与查询条件，返回过滤后的数据 */
  filter?: (rows: TData[], query: TQuery) => TData[]
  /** 初始查询条件 */
  initialQuery?: TQuery
  /** 初始每页条数 */
  initialSize?: number
}

/**
 * 客户端分页列表 hook
 * 用于后端只返回全量数组的场景（角色 / 菜单等）：拉全量 → 本地过滤 → 本地分页。
 * 与 usePagedList（服务端分页）输出结构保持一致，页面可无感切换。
 */
export function useClientPagedList<TData, TQuery extends object = Record<string, never>>({
  fetcher,
  filter,
  initialQuery,
  initialSize = 10,
}: UseClientPagedListOptions<TData, TQuery>) {
  const [query, setQueryState] = useState<TQuery>(initialQuery ?? ({} as TQuery))
  const [page, setPage] = useState(1)
  const [size, setSize] = useState(initialSize)
  const [allRows, setAllRows] = useState<TData[]>([])
  const [loading, setLoading] = useState(false)

  // fetcher 可能每次渲染都是新闭包，用 ref 避免重复触发 effect
  const fetcherRef = useRef(fetcher)
  fetcherRef.current = fetcher

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const rows = await fetcherRef.current(query)
      setAllRows(rows)
    } catch (err) {
      toast.error((err as Error).message || '加载数据失败')
    } finally {
      setLoading(false)
    }
  }, [query])

  useEffect(() => {
    void load()
  }, [load])

  /** 过滤后的全量数据 */
  const filtered = useMemo(
    () => (filter ? filter(allRows, query) : allRows),
    [allRows, query, filter],
  )

  /** 当前页切片 */
  const rows = useMemo(() => {
    const start = (page - 1) * size
    return filtered.slice(start, start + size)
  }, [filtered, page, size])

  // 过滤条件变化后当前页可能越界，自动收敛
  const totalPages = Math.max(1, Math.ceil(filtered.length / size))
  const safePage = Math.min(page, totalPages)
  useEffect(() => {
    if (safePage !== page) setPage(safePage)
  }, [safePage, page])

  /** 更新查询条件时重置回第一页 */
  const setQuery = useCallback((next: TQuery) => {
    setQueryState(next)
    setPage(1)
  }, [])

  /** 切换每页条数时重置回第一页 */
  const changeSize = useCallback((next: number) => {
    setSize(next)
    setPage(1)
  }, [])

  return {
    rows,
    total: filtered.length,
    page: safePage,
    size,
    loading,
    setPage,
    setSize: changeSize,
    query,
    setQuery,
    refresh: load,
  }
}
