import { describe, it, expect, vi, beforeEach } from 'vitest';
import { useAuthStore } from '@/stores/authStore';
import * as authApi from '@/api/auth';

// Mock the auth API
vi.mock('@/api/auth', () => ({
  login: vi.fn(),
  logout: vi.fn(),
  getProfile: vi.fn(),
}));

// Mock token utils
vi.mock('@/utils/token', () => ({
  getToken: vi.fn(() => null),
  setToken: vi.fn(),
  removeToken: vi.fn(),
}));

describe('authStore', () => {
  beforeEach(() => {
    // Reset store state
    useAuthStore.setState({
      token: null,
      profile: null,
      isAuthenticated: false,
      loading: false,
      user: null,
      roles: [],
      permissions: [],
    });
    vi.clearAllMocks();
  });

  describe('login', () => {
    it('should login successfully and fetch profile', async () => {
      const mockLoginResponse = {
        accessToken: 'test-token-123',
        user: { id: 1, username: 'admin', nickname: '管理员', status: 'ENABLED', createdAt: '2026-01-01' },
      };
      const mockProfile = {
        user: { id: 1, username: 'admin', nickname: '管理员', status: 'ENABLED', createdAt: '2026-01-01' },
        roles: ['admin'],
        permissions: ['system:user:view'],
        menus: [],
      };

      vi.mocked(authApi.login).mockResolvedValue(mockLoginResponse as any);
      vi.mocked(authApi.getProfile).mockResolvedValue(mockProfile as any);

      await useAuthStore.getState().login('admin', 'admin123');

      const state = useAuthStore.getState();
      expect(state.isAuthenticated).toBe(true);
      expect(state.token).toBe('test-token-123');
      expect(state.user?.username).toBe('admin');
      expect(state.roles).toEqual(['admin']);
      expect(state.permissions).toEqual(['system:user:view']);
    });

    it('should set loading state during login', async () => {
      let resolveLogin: (value: any) => void;
      const loginPromise = new Promise((resolve) => {
        resolveLogin = resolve;
      });
      vi.mocked(authApi.login).mockReturnValue(loginPromise as any);

      const loginCall = useAuthStore.getState().login('admin', 'admin123');

      expect(useAuthStore.getState().loading).toBe(true);

      resolveLogin!({ accessToken: 'token', user: { id: 1, username: 'admin' } });
      vi.mocked(authApi.getProfile).mockResolvedValue({
        user: { id: 1, username: 'admin' },
        roles: [],
        permissions: [],
        menus: [],
      } as any);

      await loginCall;

      expect(useAuthStore.getState().loading).toBe(false);
    });
  });

  describe('logout', () => {
    it('should clear auth state after logout', async () => {
      useAuthStore.setState({ isAuthenticated: true, token: 'some-token' });
      vi.mocked(authApi.logout).mockResolvedValue(undefined as any);

      await useAuthStore.getState().logout();

      const state = useAuthStore.getState();
      expect(state.isAuthenticated).toBe(false);
      expect(state.token).toBeNull();
      expect(state.user).toBeNull();
    });

    it('should clear auth state even if logout API fails', async () => {
      useAuthStore.setState({ isAuthenticated: true, token: 'some-token' });
      vi.mocked(authApi.logout).mockRejectedValue(new Error('Network error'));

      // logout uses try/finally, so error propagates but state is still cleared
      await useAuthStore.getState().logout().catch(() => {});

      const state = useAuthStore.getState();
      expect(state.isAuthenticated).toBe(false);
      expect(state.token).toBeNull();
    });
  });

  describe('clearAuth', () => {
    it('should reset all auth state', () => {
      useAuthStore.setState({
        isAuthenticated: true,
        token: 'some-token',
        user: { id: 1, username: 'admin' } as any,
        roles: ['admin'],
        permissions: ['system:user:view'],
      });

      useAuthStore.getState().clearAuth();

      const state = useAuthStore.getState();
      expect(state.isAuthenticated).toBe(false);
      expect(state.token).toBeNull();
      expect(state.user).toBeNull();
      expect(state.roles).toEqual([]);
      expect(state.permissions).toEqual([]);
    });
  });
});
