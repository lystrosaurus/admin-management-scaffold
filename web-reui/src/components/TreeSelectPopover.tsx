import { useEffect, useState, type ReactNode } from 'react'
import { ChevronDown, ChevronRight, X } from 'lucide-react'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { cn } from 'cn'

/** 树节点最小契约：id + 可选 label + children */
export interface TreeNodeLike {
  id: number
  name?: string
  children?: TreeNodeLike[]
}

export interface TreeSelectPopoverProps {
  /** 树数据（数组） */
  nodes: TreeNodeLike[] | undefined
  /** 当前选中 id（0 或 undefined 均视为未选中，兼容后端顶层 parentId=0 约定） */
  value: number | undefined
  onChange: (value: number | undefined) => void
  placeholder?: string
  /** 节点展示文案，默认取 name */
  getLabel?: (node: TreeNodeLike) => string
  className?: string
}

/**
 * 树形选择器（替代 antd TreeSelect）
 * 以缩进树形式在 Popover 中呈现，支持选中回显与清除。
 */
export function TreeSelectPopover({
  nodes,
  value,
  onChange,
  placeholder = '请选择（可选）',
  getLabel,
  className,
}: TreeSelectPopoverProps) {
  const [open, setOpen] = useState(false)
  // 后端顶层节点 parentId 常为 0，统一按「未选择」处理
  const effectiveValue = value || undefined
  const [label, setLabel] = useState<string>()

  // 回显当前选中项名称
  useEffect(() => {
    if (effectiveValue == null || !nodes) {
      setLabel(undefined)
      return
    }
    let found: string | undefined
    const walk = (list: TreeNodeLike[]) => {
      for (const node of list) {
        if (node.id === effectiveValue) {
          found = getLabel ? getLabel(node) : (node.name ?? `#${node.id}`)
          return
        }
        if (node.children) {
          walk(node.children)
          if (found) return
        }
      }
    }
    walk(nodes)
    setLabel(found ?? `#${effectiveValue}`)
  }, [effectiveValue, nodes, getLabel])

  const renderNodes = (list: TreeNodeLike[], depth: number): ReactNode =>
    list.map((node) => (
      <div key={node.id}>
        <button
          type="button"
          className={cn(
            'flex w-full items-center gap-1.5 rounded-sm px-2 py-1.5 text-left text-sm hover:bg-accent',
            effectiveValue === node.id && 'bg-accent font-medium',
          )}
          style={{ paddingLeft: `${depth * 16 + 8}px` }}
          onClick={() => {
            onChange(node.id)
            setOpen(false)
          }}
        >
          {node.children?.length ? (
            <ChevronRight className="size-3.5 shrink-0 text-muted-foreground" />
          ) : (
            <span className="size-3.5 shrink-0" />
          )}
          {getLabel ? getLabel(node) : node.name}
        </button>
        {node.children?.length ? renderNodes(node.children, depth + 1) : null}
      </div>
    ))

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <div
          role="combobox"
          tabIndex={0}
          aria-expanded={open}
          onClick={() => setOpen((prev) => !prev)}
          onKeyDown={(event) => {
            if (event.key === 'Enter' || event.key === ' ') {
              event.preventDefault()
              setOpen((prev) => !prev)
            }
          }}
          className={cn(
            'flex h-9 w-full cursor-pointer items-center justify-between gap-2 rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-xs',
            'hover:bg-accent/50 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring',
            className,
          )}
        >
          <span className={cn(!label && 'text-muted-foreground')}>{label ?? placeholder}</span>
          {effectiveValue != null ? (
            <X
              className="size-4 shrink-0 opacity-50 hover:opacity-100"
              onClick={(event) => {
                event.stopPropagation()
                onChange(undefined)
              }}
            />
          ) : (
            <ChevronDown className="size-4 shrink-0 opacity-50" />
          )}
        </div>
      </PopoverTrigger>
      <PopoverContent className="max-h-72 w-[--radix-popover-trigger-width] overflow-y-auto p-1">
        {!nodes || nodes.length === 0 ? (
          <p className="px-2 py-4 text-center text-sm text-muted-foreground">暂无数据</p>
        ) : (
          renderNodes(nodes, 0)
        )}
      </PopoverContent>
    </Popover>
  )
}

export default TreeSelectPopover
