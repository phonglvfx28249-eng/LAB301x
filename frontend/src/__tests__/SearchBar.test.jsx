import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import SearchBar from '../components/admin/SearchBar.jsx';

describe('SearchBar Component', () => {
  it('renders input with value and placeholder', () => {
    render(<SearchBar value="admin" onChange={vi.fn()} placeholder="Search user" />);
    const input = screen.getByPlaceholderText('Search user');
    expect(input).toBeInTheDocument();
    expect(input.value).toBe('admin');
  });

  it('triggers onChange when input text changes', () => {
    const handleChange = vi.fn();
    render(<SearchBar value="" onChange={handleChange} placeholder="Search" />);
    
    const input = screen.getByPlaceholderText('Search');
    fireEvent.change(input, { target: { value: 'john' } });
    
    expect(handleChange).toHaveBeenCalledWith('john');
  });
});
