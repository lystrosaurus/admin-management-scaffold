import { describe, it, expect, vi, beforeEach } from 'vitest';
import * as userApi from '@/api/user';
import { get, post, put, del } from '@/api/client';

// Mock the client module
vi.mock('@/api/client', () => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
  default: {
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  },
}));

describe('user API', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('listUsers', () => {
    it('should call GET /app/users with params and map results', async () => {
      const mockResponse = {
        items: [
          { id: 1, username: 'admin', nickname: '管理员', status: 'ENABLED' },
          { id: 2, username: 'user1', nickname: '用户一', status: 'DISABLED' },
        ],
        total: 2,
        page: 1,
        size: 10,
      };

      vi.mocked(get).mockResolvedValue(mockResponse);

      const result = await userApi.listUsers({ page: 1, size: 10 });

      expect(get).toHaveBeenCalledWith('/app/users', { params: { page: 1, size: 10 } });
      expect(result.items).toHaveLength(2);
      expect(result.items[0].status).toBe('ACTIVE');
      expect(result.items[1].status).toBe('INACTIVE');
    });
  });

  describe('createUser', () => {
    it('should call POST /app/users with mapped data', async () => {
      const mockResponse = { id: 3, username: 'newuser', nickname: '新用户', status: 'ENABLED' };
      vi.mocked(post).mockResolvedValue(mockResponse);

      const result = await userApi.createUser({
        username: 'newuser',
        nickname: '新用户',
        password: 'pass123',
        status: 'ACTIVE',
      });

      expect(post).toHaveBeenCalledWith('/app/users', {
        username: 'newuser',
        nickname: '新用户',
        password: 'pass123',
        status: 'ENABLED',
      });
      expect(result.status).toBe('ACTIVE');
    });
  });

  describe('updateUser', () => {
    it('should call PUT /app/users/:id', async () => {
      const mockResponse = { id: 1, username: 'admin', nickname: '更新后', status: 'ENABLED' };
      vi.mocked(put).mockResolvedValue(mockResponse);

      const result = await userApi.updateUser(1, { nickname: '更新后' });

      expect(put).toHaveBeenCalledWith('/app/users/1', { nickname: '更新后' });
      expect(result.nickname).toBe('更新后');
    });
  });

  describe('deleteUser', () => {
    it('should call DELETE /app/users/:id', async () => {
      vi.mocked(del).mockResolvedValue(undefined as any);

      await userApi.deleteUser(1);

      expect(del).toHaveBeenCalledWith('/app/users/1');
    });
  });
});
