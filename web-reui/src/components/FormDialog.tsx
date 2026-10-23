import type { ReactNode } from 'react'
import type { UseFormReturn } from 'react-hook-form'
import { toast } from 'sonner'
import { Loader2 } from 'lucide-react'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { cn } from 'cn'

export interface FormDialogProps<TValues extends Record<string, unknown>> {
  open: boolean
  title: string
  description?: string
  submitText?: string
  cancelText?: string
  /** 提交处理，返回 Promise；resolve 后自动关闭 */
  onSubmit: (values: TValues) => Promise<void>
  onOpenChange: (open: boolean) => void
  /** 由页面创建并持有的表单实例（页面掌握校验规则） */
  form: UseFormReturn<TValues>
  /** 表单体 */
  children: ReactNode
  className?: string
}

/**
 * 表单弹窗骨架：Dialog + react-hook-form
 * 统一处理提交中状态、成功关闭、失败 toast。
 */
export function FormDialog<TValues extends Record<string, unknown>>({
  open,
  title,
  description,
  submitText = '保存',
  cancelText = '取消',
  onSubmit,
  onOpenChange,
  form,
  children,
  className,
}: FormDialogProps<TValues>) {
  const submitting = form.formState.isSubmitting

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className={cn('sm:max-w-[520px]', className)}>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          {description ? <DialogDescription>{description}</DialogDescription> : null}
        </DialogHeader>
        <form
          onSubmit={form.handleSubmit(async (values) => {
            try {
              await onSubmit(values)
              onOpenChange(false)
            } catch (err) {
              toast.error((err as Error).message || '操作失败')
            }
          })}
          className="flex flex-col gap-4"
        >
          {children}
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={submitting}
            >
              {cancelText}
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? (
                <>
                  <Loader2 className="mr-2 size-4 animate-spin" />
                  提交中…
                </>
              ) : (
                submitText
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
