import { useEffect, useMemo, useState, type ReactNode } from 'react'
import {
  ThemeContext,
  THEME_STORAGE_KEY,
  type ResolvedTheme,
  type Theme,
  type ThemeContextValue,
} from './theme-context'

export type { ResolvedTheme, Theme } from './theme-context'

const DARK_QUERY = '(prefers-color-scheme: dark)'

function getSystemTheme(): ResolvedTheme {
  return window.matchMedia(DARK_QUERY).matches ? 'dark' : 'light'
}

/** 校验本地存储值，避免脏数据导致主题失效 */
function readStoredTheme(): Theme {
  const stored = window.localStorage.getItem(THEME_STORAGE_KEY)
  return stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system'
}

/**
 * 主题提供者：class 策略切换亮/暗色，与 index.css 中的 .dark 变体配合。
 * 自实现而非引入 next-themes：避免在组件树内注入 <script>（React 19 会告警），
 * 首屏主题由 index.html 的内联脚本提前应用，无闪烁。
 */
export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>(readStoredTheme)
  const [systemTheme, setSystemTheme] = useState<ResolvedTheme>(getSystemTheme)

  // 跟随系统主题变化（仅 system 模式生效）
  useEffect(() => {
    const mql = window.matchMedia(DARK_QUERY)
    const onChange = () => setSystemTheme(mql.matches ? 'dark' : 'light')
    mql.addEventListener('change', onChange)
    return () => mql.removeEventListener('change', onChange)
  }, [])

  const resolvedTheme: ResolvedTheme = theme === 'system' ? systemTheme : theme

  // 同步到 <html>：.dark class 与 color-scheme
  useEffect(() => {
    const root = document.documentElement
    root.classList.toggle('dark', resolvedTheme === 'dark')
    root.style.colorScheme = resolvedTheme
    window.localStorage.setItem(THEME_STORAGE_KEY, theme)
  }, [theme, resolvedTheme])

  const value = useMemo<ThemeContextValue>(
    () => ({ theme, resolvedTheme, setTheme }),
    [theme, resolvedTheme],
  )

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}
