import type { ComponentType } from 'react'
import { Link } from 'react-router-dom'
import { Construction } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

interface PagePlaceholderProps {
  title: string
}

/**
 * 页面迁移占位组件
 * 用于标记尚未从 Ant Design 版本迁移到 ReUI 的页面。
 */
const PagePlaceholder: ComponentType<PagePlaceholderProps> = ({ title }) => (
  <div className="flex min-h-[60vh] items-center justify-center p-6">
    <Card className="w-full max-w-md text-center">
      <CardHeader>
        <div className="mx-auto mb-2 flex size-12 items-center justify-center rounded-full bg-muted">
          <Construction className="size-6 text-muted-foreground" />
        </div>
        <CardTitle>{title}</CardTitle>
        <CardDescription>
          该页面正在从 Ant Design 版本迁移至 ReUI，将在后续迭代中完成。
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Button variant="outline" asChild>
          <Link to="/dashboard">返回仪表盘</Link>
        </Button>
      </CardContent>
    </Card>
  </div>
)

export default PagePlaceholder
