import { describe, it, expect, vi } from 'vitest';
import { getDashboardStats } from '@/api/dashboard';
import { get } from '@/api/client';

vi.mock('@/api/client', () => ({
  get: vi.fn(),
}));

describe('dashboard API', () => {
  it('should fetch dashboard stats from /app/dashboard/stats', async () => {
    const mockStats = {
      userCount: 128,
      roleCount: 12,
      employeeCount: 96,
      orgUnitCount: 8,
    };

    vi.mocked(get).mockResolvedValue(mockStats);

    const result = await getDashboardStats();

    expect(get).toHaveBeenCalledWith('/app/dashboard/stats');
    expect(result).toEqual(mockStats);
  });
});
