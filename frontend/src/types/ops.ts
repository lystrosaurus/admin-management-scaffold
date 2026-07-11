/**
 * 运维 / 系统状态相关类型
 * 字段命名与后端 OpsInfoVO 对齐
 */

/**
 * 应用信息
 */
export interface AppInfo {
  /** 应用名 */
  name: string;
  /** 版本号 */
  version: string;
  /** Java 运行时版本 */
  javaVersion: string;
  /** 运行状态（如 UP / DOWN） */
  status: string;
  /** 操作系统名称 */
  osName?: string;
  /** 操作系统架构 */
  osArch?: string;
  /** 启动时间 */
  startTime?: string;
  /** 运行 profile（如 dev / prod） */
  profile?: string;
}
