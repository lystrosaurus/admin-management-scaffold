import { useCallback, useEffect, useRef, useState } from 'react'
import { toast } from 'sonner'
import type { PageResult } from '@/types/api'

/** 分页查询基础参数 */
export interface PagedListQuery {
  [key: string]: unknown
}

interface UsePagedListOptions<TData, TQuery extends object> {
  /** 拉取函数，接收合并后的 page/size 与查询参数 */
  fetcher: (params: TQuery & { page: number; size: number }) => Promise<PageResult<TData>>
  /** 初始查询条件（不含分页） */
  initialQuery?: TQuery
  /** 初始每页条数 */
  initialSize?: number
}

/**
 * 服务端分页列表 hook
 * 封装 page/size/query 状态与拉取逻辑，页面只关心渲染。
 */
export function usePagedList<TData, TQuery extends object = Record<string, never>>({
  fetcher,
  initialQuery,
  initialSize = 10,
}: UsePagedListOptions<TData, TQuery>) {
  const [query, setQueryState] = useState<TQuery>(initialQuery ?? ({} as TQuery))
  const [page, setPage] = useState(1)
  const [size, setSize] = useState(initialSize)
  const [rows, setRows] = useState<TData[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)

  // fetcher 可能每次渲染都是新闭包，用 ref 避免重复触发 effect
  const fetcherRef = useRef(fetcher)
  fetcherRef.current = fetcher

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const result = await fetcherRef.current({ ...query, page, size } as TQuery & { page: number; size: number })
      setRows(result.items)
      setTotal(result.total)
    } catch (err) {
      toast.error((err as Error).message || '加载数据失败')
    } finally {
      setLoading(false)
    }
  }, [query, page, size])

  useEffect(() => {
    void load()
  }, [load])

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
    total,
    page,
    size,
    loading,
    setPage,
    setSize: changeSize,
    query,
    setQuery,
    refresh: load,
  }
}
