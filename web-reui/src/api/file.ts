import { get, post, del } from './client';
import { getToken } from '@/utils/token';
import type { PageResult } from '@/types/api';
import type { FileItem, FileQueryParams } from '@/types/file';

/**
 * 上传文件
 * @param file 待上传的文件
 * @param bizType 业务类型（可选）
 */
export const uploadFile = async (file: File, bizType?: string): Promise<FileItem> => {
  const formData = new FormData();
  formData.append('file', file);
  if (bizType) {
    formData.append('bizType', bizType);
  }
  return post<FileItem>('/app/files', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

/**
 * 获取文件列表（分页）
 */
export const listFiles = async (params?: FileQueryParams): Promise<PageResult<FileItem>> => {
  return get<PageResult<FileItem>>('/app/files', { params });
};

/**
 * 删除文件
 */
export const deleteFile = (id: number): Promise<void> => {
  return del<void>(`/app/files/${id}`);
};

/**
 * 获取文件下载 URL（供 window.open 直接访问）
 * 注意：浏览器直接导航不携带 Authorization 头，
 * 后端需支持查询参数 / cookie 形式的令牌认证方可下载。
 */
export const getDownloadUrl = (id: number): string => `/api/app/files/${id}/download`;

/**
 * 下载文件并以附件形式触发浏览器下载
 * 使用 fetch 注入 Bearer 令牌（走 client 的 baseURL），
 * 规避 window.open / 普通 <a> 不携带认证头导致 401 的问题。
 */
export const downloadFile = async (id: number, fileName: string): Promise<void> => {
  const token = getToken();
  const res = await fetch(`/api/app/files/${id}/download`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });
  if (!res.ok) {
    throw new Error(`下载失败（${res.status}）`);
  }
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
};
