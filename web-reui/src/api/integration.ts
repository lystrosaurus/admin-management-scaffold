import { get, post, put, del } from './client';
import type {
  SourceVO,
  SourceCreateRequest,
  SourceUpdateRequest,
  PrincipalVO,
  PrincipalCreateRequest,
  PrincipalUpdateRequest,
  PrincipalQueryParams,
  LinkCandidateVO,
} from '@/types/integration';

/**
 * 外部身份源相关接口
 */

/** 查询所有外部身份源（返回扁平列表，非分页） */
export const listSources = async (): Promise<SourceVO[]> =>
  get<SourceVO[]>('/app/integration/sources');

/** 获取外部身份源详情 */
export const getSource = async (id: number): Promise<SourceVO> =>
  get<SourceVO>(`/app/integration/sources/${id}`);

/** 创建外部身份源 */
export const createSource = async (data: SourceCreateRequest): Promise<SourceVO> =>
  post<SourceVO>('/app/integration/sources', data);

/** 更新外部身份源 */
export const updateSource = async (id: number, data: SourceUpdateRequest): Promise<SourceVO> =>
  put<SourceVO>(`/app/integration/sources/${id}`, data);

/** 删除外部身份源 */
export const deleteSource = async (id: number): Promise<void> =>
  del<void>(`/app/integration/sources/${id}`);

/**
 * 外部主体相关接口
 */

/** 查询外部主体列表（支持 sourceId / linkStatus 过滤） */
export const listPrincipals = async (
  params?: PrincipalQueryParams,
): Promise<PrincipalVO[]> => get<PrincipalVO[]>('/app/integration/principals', { params });

/** 获取外部主体详情 */
export const getPrincipal = async (id: number): Promise<PrincipalVO> =>
  get<PrincipalVO>(`/app/integration/principals/${id}`);

/** 创建外部主体 */
export const createPrincipal = async (data: PrincipalCreateRequest): Promise<PrincipalVO> =>
  post<PrincipalVO>('/app/integration/principals', data);

/** 更新外部主体 */
export const updatePrincipal = async (
  id: number,
  data: PrincipalUpdateRequest,
): Promise<PrincipalVO> => put<PrincipalVO>(`/app/integration/principals/${id}`, data);

/** 删除外部主体 */
export const deletePrincipal = async (id: number): Promise<void> =>
  del<void>(`/app/integration/principals/${id}`);

/**
 * 身份匹配候选相关接口
 */

/** 查询待处理候选记录列表 */
export const listPendingLinkCandidates = async (): Promise<LinkCandidateVO[]> =>
  get<LinkCandidateVO[]>('/app/integration/link-candidates/pending');

/** 根据外部主体 ID 查询候选记录列表 */
export const listLinkCandidatesByPrincipal = async (
  principalId: number,
): Promise<LinkCandidateVO[]> =>
  get<LinkCandidateVO[]>(`/app/integration/link-candidates/by-principal/${principalId}`);

/** 确认候选记录 */
export const confirmLinkCandidate = async (id: number): Promise<void> =>
  post<void>(`/app/integration/link-candidates/${id}/confirm`);

/** 拒绝候选记录 */
export const rejectLinkCandidate = async (id: number): Promise<void> =>
  post<void>(`/app/integration/link-candidates/${id}/reject`);
