/**
 * 外部身份源（对齐后端 ExtSourceVO）
 */
export interface IntegrationSource {
  id: number;
  code: string;
  name: string;
  sourceType: string;
  tenantKey: string;
  status: string;
  priority: number;
  configJson: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 创建外部身份源请求
 */
export interface CreateIntegrationSourceRequest {
  code: string;
  name: string;
  sourceType: string;
  tenantKey?: string;
  priority?: number;
  configJson?: string;
}

/**
 * 更新外部身份源请求
 */
export interface UpdateIntegrationSourceRequest {
  name?: string;
  sourceType?: string;
  tenantKey?: string;
  status?: string;
  priority?: number;
  configJson?: string;
}
