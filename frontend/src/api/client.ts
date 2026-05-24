import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios';
import { getToken, removeToken } from '@/utils/token';
import type { ApiResponse } from '@/types/api';

/**
 * 创建 axios 实例
 */
const client = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * 请求拦截器：添加 Token 到请求头
 */
client.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * 响应拦截器：处理响应数据和错误
 */
client.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<unknown>>) => {
    const { data } = response;
    // 业务成功
    if (data.code === 200) {
      return data.data as AxiosResponse;
    }
    // 业务错误
    return Promise.reject(new Error(data.message || '请求失败'));
  },
  (error) => {
    // 401 未授权：清除 Token 并跳转登录页
    if (error.response?.status === 401) {
      removeToken();
      // 如果当前不在登录页，则跳转
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
      return Promise.reject(new Error('登录已过期，请重新登录'));
    }
    // 其他错误
    const message = error.response?.data?.message || error.message || '网络错误';
    return Promise.reject(new Error(message));
  }
);

/**
 * GET 请求
 */
export const get = <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return client.get<ApiResponse<T>, T>(url, config);
};

/**
 * POST 请求
 */
export const post = <T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> => {
  return client.post<ApiResponse<T>, T>(url, data, config);
};

/**
 * PUT 请求
 */
export const put = <T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> => {
  return client.put<ApiResponse<T>, T>(url, data, config);
};

/**
 * DELETE 请求
 */
export const del = <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return client.delete<ApiResponse<T>, T>(url, config);
};

export default client;
