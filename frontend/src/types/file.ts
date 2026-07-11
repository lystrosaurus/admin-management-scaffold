/**
 * 文件管理相关类型
 * 字段命名与后端 FileVO 对齐
 */

/**
 * 文件信息
 */
export interface FileItem {
  id: number;
  /** 原文件名 */
  originalName: string;
  /** 扩展名（不含点，如 pdf、png） */
  ext: string;
  /** 文件大小（字节） */
  sizeBytes: number;
  /** MIME 类型 */
  mimeType: string;
  /** 业务类型 */
  bizType: string;
  /** 上传人 ID */
  uploaderId: number;
  /** 上传人用户名（后端可能填充，按需可选） */
  uploaderName?: string;
  /** 创建时间 */
  createdAt: string;
  /** 状态（如 READY / DELETED） */
  status: string;
}

/**
 * 文件查询参数
 */
export interface FileQueryParams {
  page?: number;
  size?: number;
  /** 原文件名模糊匹配 */
  originalName?: string;
  /** 业务类型过滤 */
  bizType?: string;
}
