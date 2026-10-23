/**
 * 外部身份源 VO —— 字段名严格对应后端 ExtSourceVO record
 */
export interface SourceVO {
  id: number;
  code: string;
  name: string;
  sourceType: string;
  tenantKey: string;
  status: string;
  priority: number;
  configJson?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 创建外部身份源请求 —— 对应 ExtSourceCreateDTO
 */
export interface SourceCreateRequest {
  code: string;
  name: string;
  sourceType: string;
  tenantKey?: string;
  priority?: number;
  configJson?: string;
}

/**
 * 更新外部身份源请求 —— 对应 ExtSourceUpdateDTO
 */
export interface SourceUpdateRequest {
  name?: string;
  sourceType?: string;
  tenantKey?: string;
  status?: string;
  priority?: number;
  configJson?: string;
}

/**
 * 外部主体标识符 VO —— 对应 ExtPrincipalIdentifierVO
 */
export interface PrincipalIdentifierVO {
  id: number;
  idType: string;
  idValue: string;
  isPrimary: boolean;
}

/**
 * 外部主体 VO —— 字段名严格对应后端 ExtPrincipalVO record
 */
export interface PrincipalVO {
  id: number;
  sourceId: number;
  sourceName?: string;
  principalType: string;
  externalKey: string;
  displayName: string;
  status: string;
  lastSyncAt?: string;
  canonicalType?: string;
  canonicalId?: number;
  linkStatus?: string;
  createdAt: string;
  updatedAt: string;
  identifiers?: PrincipalIdentifierVO[];
}

/**
 * 外部主体标识符项 —— 对应 ExtPrincipalCreateDTO.IdentifierItem
 */
export interface PrincipalIdentifierItem {
  idType: string;
  idValue: string;
  isPrimary?: boolean;
}

/**
 * 创建外部主体请求 —— 对应 ExtPrincipalCreateDTO
 */
export interface PrincipalCreateRequest {
  sourceId: number;
  principalType: string;
  externalKey: string;
  displayName: string;
  rawPayloadJson?: string;
  identifiers?: PrincipalIdentifierItem[];
}

/**
 * 更新外部主体请求 —— 对应 ExtPrincipalUpdateDTO
 */
export interface PrincipalUpdateRequest {
  displayName?: string;
  status?: string;
  canonicalType?: string;
  canonicalId?: number;
  linkStatus?: string;
}

/**
 * 外部主体查询参数 —— 对应 IntegrationPrincipalController.list 的 sourceId / linkStatus
 */
export interface PrincipalQueryParams {
  sourceId?: number;
  linkStatus?: string;
}

/**
 * 身份匹配候选 VO —— 字段名严格对应后端 IdentityLinkCandidateVO record
 */
export interface LinkCandidateVO {
  id: number;
  sourcePrincipalId: number;
  candidateType: string;
  candidateId: number;
  score?: number;
  reason?: string;
  status: string;
  createdAt?: string;
  handledBy?: string;
  handledAt?: string;
}
