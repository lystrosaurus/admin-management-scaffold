/**
 * 配置项信息
 */
export interface ConfigItemVO {
  id: number;
  configKey: string;
  configValue: string;
  configType: string;
  description?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 创建配置项请求
 */
export interface ConfigItemCreateDTO {
  configKey: string;
  configValue: string;
  configType?: string;
  description?: string;
  status?: string;
}

/**
 * 更新配置项请求
 */
export interface ConfigItemUpdateDTO {
  configKey?: string;
  configValue?: string;
  configType?: string;
  description?: string;
  status?: string;
}

/**
 * 配置项查询参数
 */
export interface ConfigItemQuery {
  page?: number;
  size?: number;
  configKey?: string;
}
