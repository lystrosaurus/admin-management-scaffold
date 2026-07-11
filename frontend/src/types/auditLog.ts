/**
 * 审计日志相关类型
 * 字段命名与后端 VO 对齐（后端 audit 模块 LoginLogVO / OperationLogVO）
 */

/**
 * 登录日志
 */
export interface LoginLog {
  id: number;
  username: string;
  ip: string;
  success: boolean;
  /** 登录时间 */
  loginAt: string;
  /** User-Agent */
  userAgent: string;
}

/**
 * 操作日志
 */
export interface OperationLog {
  id: number;
  username: string;
  /** 业务模块 */
  module: string;
  /** 动作 */
  action: string;
  /** 业务 ID */
  bizId: string;
  /** 操作时间 */
  opAt: string;
  ip: string;
}

/**
 * 登录日志查询参数
 */
export interface LoginLogQueryParams {
  page?: number;
  size?: number;
  username?: string;
  /** 起始时间（ISO 字符串） */
  startTime?: string;
  /** 结束时间（ISO 字符串） */
  endTime?: string;
}

/**
 * 操作日志查询参数
 */
export interface OperationLogQueryParams {
  page?: number;
  size?: number;
  username?: string;
  /** 起始时间（ISO 字符串） */
  startTime?: string;
  /** 结束时间（ISO 字符串） */
  endTime?: string;
}
