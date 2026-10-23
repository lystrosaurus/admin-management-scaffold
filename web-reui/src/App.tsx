import { ThemeProvider } from '@/components/ThemeProvider'
import { Toaster } from '@/components/ui/sonner'
import { TooltipProvider } from '@/components/ui/tooltip'
import AppRoutes from '@/routes'

export default function App() {
  return (
    <ThemeProvider>
      <TooltipProvider>
        <AppRoutes />
        <Toaster richColors position="top-center" />
      </TooltipProvider>
    </ThemeProvider>
  )
}
