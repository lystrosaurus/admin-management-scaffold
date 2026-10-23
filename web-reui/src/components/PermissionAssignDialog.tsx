import { useEffect, useState } from 'react'
import { Loader2, ShieldCheck } from 'lucide-react'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { listPermissions, getPermissionsByRoleId } from '@/api/permission'
import { assignPermissions } from '@/api/role'
import type { Permission } from '@/types/role'
import { toast } from 'sonner'

/** 权限类型分组顺序与展示名 */
const PERMISSION_GROUPS: { type: Permission['type']; label: string }[] = [
  { type: 'MENU', label: '菜单' },
  { type: 'BUTTON', label: '按钮' },
  { type: 'API', label: '接口' },
]

/** 稳定空数组引用，避免未加载时每帧新建 */
const EMPTY_PERMISSIONS: Permission[] = []

export interface PermissionAssignDialogProps {
  open: boolean
  roleId: number | null
  roleName?: string
  onOpenChange: (open: boolean) => void
}

/**
 * 角色权限分配弹窗
 * 拉取全部权限与角色已有权限，按 MENU / BUTTON / API 分组勾选，提交分配。
 */
export function PermissionAssignDialog({
  open,
  roleId,
  roleName,
  onOpenChange,
}: PermissionAssignDialogProps) {
  const [loaded, setLoaded] = useState<{ roleId: number; permissions: Permission[] } | null>(null)
  const [checkedIds, setCheckedIds] = useState<Set<number>>(new Set())
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!open || roleId == null) return
    let cancelled = false
    Promise.all([listPermissions(), getPermissionsByRoleId(roleId)])
      .then(([all, owned]) => {
        if (cancelled) return
        setLoaded({ roleId, permissions: all })
        setCheckedIds(new Set(owned.map((p) => p.id)))
      })
      .catch((err: Error) => {
        if (cancelled) return
        toast.error(err.message || '获取权限数据失败')
        // 标记该角色已尝试加载，结束 loading 态并落到空数据提示
        setLoaded({ roleId, permissions: [] })
      })
    return () => {
      cancelled = true
    }
  }, [open, roleId])

  // loading 由「当前角色是否已加载完」派生，避免在 effect 内同步 setState
  const loading = open && roleId != null && loaded?.roleId !== roleId
  const permissions = loaded?.roleId === roleId ? loaded.permissions : EMPTY_PERMISSIONS

  const toggle = (id: number, checked: boolean) => {
    setCheckedIds((prev) => {
      const next = new Set(prev)
      if (checked) {
        next.add(id)
      } else {
        next.delete(id)
      }
      return next
    })
  }

  const toggleGroup = (type: Permission['type'], checked: boolean) => {
    setCheckedIds((prev) => {
      const next = new Set(prev)
      for (const p of permissions) {
        if (p.type === type) {
          if (checked) {
            next.add(p.id)
          } else {
            next.delete(p.id)
          }
        }
      }
      return next
    })
  }

  const handleSubmit = async () => {
    if (roleId == null) return
    setSubmitting(true)
    try {
      await assignPermissions(roleId, [...checkedIds])
      toast.success('权限分配成功')
      onOpenChange(false)
    } catch (err) {
      toast.error((err as Error).message || '权限分配失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <ShieldCheck className="size-4" />
            分配权限{roleName ? ` - ${roleName}` : ''}
          </DialogTitle>
          <DialogDescription>勾选该角色可访问的菜单与操作权限。</DialogDescription>
        </DialogHeader>

        {loading ? (
          <div className="flex h-48 items-center justify-center">
            <Loader2 className="size-6 animate-spin text-muted-foreground" />
          </div>
        ) : (
          <div className="max-h-[60vh] space-y-5 overflow-y-auto pr-1">
            {PERMISSION_GROUPS.map((group) => {
              const items = permissions.filter((p) => p.type === group.type)
              if (items.length === 0) return null
              const allChecked = items.every((p) => checkedIds.has(p.id))
              const someChecked = items.some((p) => checkedIds.has(p.id))
              return (
                <section key={group.type} className="space-y-2">
                  <div className="flex items-center gap-2 border-b pb-1.5">
                    <Checkbox
                      id={`group-${group.type}`}
                      checked={allChecked}
                      // 半选状态：someChecked 且非全选
                      className={!allChecked && someChecked ? 'opacity-50' : undefined}
                      onCheckedChange={(value) => toggleGroup(group.type, value === true)}
                    />
                    <Label htmlFor={`group-${group.type}`} className="font-medium">
                      {group.label}
                      <span className="ml-2 text-xs text-muted-foreground">
                        {items.filter((p) => checkedIds.has(p.id)).length}/{items.length}
                      </span>
                    </Label>
                  </div>
                  <div className="grid grid-cols-1 gap-x-6 gap-y-2 pl-6 sm:grid-cols-2">
                    {items.map((p) => (
                      <div key={p.id} className="flex items-center gap-2">
                        <Checkbox
                          id={`perm-${p.id}`}
                          checked={checkedIds.has(p.id)}
                          onCheckedChange={(value) => toggle(p.id, value === true)}
                        />
                        <Label htmlFor={`perm-${p.id}`} className="font-normal">
                          {p.name}
                          <span className="ml-1.5 text-xs text-muted-foreground">({p.code})</span>
                        </Label>
                      </div>
                    ))}
                  </div>
                </section>
              )
            })}
            {permissions.length === 0 && (
              <p className="py-8 text-center text-sm text-muted-foreground">暂无权限数据</p>
            )}
          </div>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            取消
          </Button>
          <Button onClick={handleSubmit} disabled={loading || submitting}>
            {submitting && <Loader2 className="size-4 animate-spin" />}
            保存
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

export default PermissionAssignDialog
