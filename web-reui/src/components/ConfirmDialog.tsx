import type { ReactNode } from 'react'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { buttonVariants } from '@/components/ui/button-variants'
import { cn } from 'cn'

export interface ConfirmDialogProps {
  open: boolean
  title: string
  description?: ReactNode
  confirmText?: string
  cancelText?: string
  /** 危险操作（删除等）使用 destructive 样式 */
  destructive?: boolean
  onConfirm: () => void
  onOpenChange: (open: boolean) => void
}

/**
 * 确认对话框（替代 antd Popconfirm 的弹窗形态）
 */
export function ConfirmDialog({
  open,
  title,
  description,
  confirmText = '确定',
  cancelText = '取消',
  destructive = false,
  onConfirm,
  onOpenChange,
}: ConfirmDialogProps) {
  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          {description ? <AlertDialogDescription>{description}</AlertDialogDescription> : null}
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{cancelText}</AlertDialogCancel>
          <AlertDialogAction
            className={cn(
              destructive &&
                buttonVariants({ variant: 'destructive' }),
            )}
            onClick={(event) => {
              // 阻止 AlertDialogAction 默认关闭，由调用方控制关闭时机
              event.preventDefault()
              onConfirm()
            }}
          >
            {confirmText}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
