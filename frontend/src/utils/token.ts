const TOKEN_KEY = 'admin_token';

/**
 * 获取 Token
 */
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY);
};

/**
 * 设置 Token
 */
export const setToken = (token: string): void => {
  localStorage.setItem(TOKEN_KEY, token);
};

/**
 * 移除 Token
 */
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY);
};
