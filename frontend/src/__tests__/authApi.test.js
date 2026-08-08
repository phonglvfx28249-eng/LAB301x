import { describe, it, expect, vi } from 'vitest';
import { loginUser, signupUser } from '../api/authApi.js';
import springApi from '../api/api.js';

vi.mock('../api/api.js', () => ({
  default: {
    post: vi.fn()
  }
}));

describe('authApi module', () => {
  it('loginUser posts credentials to /auth/login', async () => {
    springApi.post.mockResolvedValueOnce({ data: { token: 'mock-jwt-token' } });

    const result = await loginUser('test@example.com', 'pass123');

    expect(springApi.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      password: 'pass123'
    });
    expect(result).toEqual({ token: 'mock-jwt-token' });
  });

  it('signupUser posts payload to /auth/signup', async () => {
    const payload = { email: 'new@example.com', password: 'pass', fullName: 'New' };
    springApi.post.mockResolvedValueOnce({ data: { token: 'new-token' } });

    const result = await signupUser(payload);

    expect(springApi.post).toHaveBeenCalledWith('/auth/signup', payload);
    expect(result).toEqual({ token: 'new-token' });
  });
});
