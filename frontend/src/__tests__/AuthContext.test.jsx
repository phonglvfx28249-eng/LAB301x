import React from 'react';
import { render, screen, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AuthProvider, useAuth } from '../context/AuthContext.jsx';
import springApi from '../api/api.js';
import * as authApi from '../api/authApi.js';

vi.mock('../api/api.js', () => ({
  default: {
    get: vi.fn()
  }
}));

vi.mock('../api/authApi.js', () => ({
  loginUser: vi.fn(),
  signupUser: vi.fn()
}));

const TestComponent = () => {
  const { user, token, login, logout } = useAuth();
  return (
    <div>
      <span data-testid="token">{token || 'no-token'}</span>
      <span data-testid="username">{user ? user.username : 'no-user'}</span>
      <button onClick={() => login('test@example.com', 'pass')}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it('provides empty user and token when localStorage has no token', async () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId('token')).toHaveTextContent('no-token');
    expect(screen.getByTestId('username')).toHaveTextContent('no-user');
  });

  it('verifies token and sets user when valid token exists in localStorage', async () => {
    localStorage.setItem('token', 'existing-token');
    springApi.get.mockResolvedValueOnce({ data: { username: 'john_doe' } });

    await act(async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );
    });

    expect(springApi.get).toHaveBeenCalledWith('/auth/me');
    expect(screen.getByTestId('username')).toHaveTextContent('john_doe');
  });

  it('logs out and removes token from localStorage', async () => {
    localStorage.setItem('token', 'existing-token');
    springApi.get.mockResolvedValueOnce({ data: { username: 'john_doe' } });

    await act(async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );
    });

    const logoutBtn = screen.getByText('Logout');
    await act(async () => {
      logoutBtn.click();
    });

    expect(localStorage.getItem('token')).toBeNull();
    expect(screen.getByTestId('token')).toHaveTextContent('no-token');
  });
});
