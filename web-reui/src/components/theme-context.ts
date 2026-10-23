import { createContext, useContext } from 'react'

export type Theme = 'light' | 'dark' | 'system'
export type ResolvedTheme = 'light' | 'dark'
export type ThemeContextValue = {
  /** 用户选择的主题（可能为 system） */
  theme: Theme
  /** 实际生效的主题（已解析 system） */
  resolvedTheme: ResolvedTheme
  setTheme: (theme: Theme) => void
}

/** 主题存储键：需与 index.html 中的首屏内联脚本保持一致 */
export const THEME_STORAGE_KEY = 'admin_theme'

export const ThemeContext = createContext<ThemeContextValue | null>(null)

/** 读取当前主题与切换方法，必须在 ThemeProvider 内使用 */
export function useTheme() {
  const context = useContext(ThemeContext)
  if (!context) {
    throw new Error('useTheme must be used within ThemeProvider')
  }
  return context
}
