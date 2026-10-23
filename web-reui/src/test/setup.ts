import '@testing-library/jest-dom/vitest'

/**
 * jsdom 不实现 window.matchMedia，shadcn sidebar（use-mobile）等组件依赖它。
 * 这里补一个最小 polyfill，返回值结构与 MediaQueryList 对齐。
 */
if (typeof window !== 'undefined' && !window.matchMedia) {
  window.matchMedia = (query: string) =>
    ({
      matches: false,
      media: query,
      onchange: null,
      addListener: () => {},
      removeListener: () => {},
      addEventListener: () => {},
      removeEventListener: () => {},
      dispatchEvent: () => false,
    }) as MediaQueryList
}
