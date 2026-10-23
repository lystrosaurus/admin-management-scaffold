import { columnSizingFeature, rowExpandingFeature, tableFeatures } from '@tanstack/react-table'

/**
 * 项目级共享的 TanStack Table v9 特性包。
 * 管理后台表格均为服务端分页，表格本身只做当前页渲染；
 * columnSizingFeature 提供列宽（size/minSize/maxSize）支持；
 * rowExpandingFeature 提供树形表格能力（菜单管理等层级数据）。
 * 注意：features 对象必须保持稳定引用（模块作用域），按官方指引创建。
 */
export const coreTableFeatures = tableFeatures({ columnSizingFeature, rowExpandingFeature })

export type CoreTableFeatures = typeof coreTableFeatures
