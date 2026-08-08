import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Pagination from '../components/admin/Pagination.jsx';

describe('Pagination Component', () => {
  it('returns null if totalPages <= 1', () => {
    const { container } = render(<Pagination page={0} totalPages={1} onPageChange={vi.fn()} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders page buttons correctly for multiple pages', () => {
    render(<Pagination page={0} totalPages={5} onPageChange={vi.fn()} />);
    expect(screen.getByText('1')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  it('disables Previous button on first page', () => {
    render(<Pagination page={0} totalPages={5} onPageChange={vi.fn()} />);
    const prevButton = screen.getByText('‹ Previous');
    expect(prevButton).toBeDisabled();
  });

  it('disables Next button on last page', () => {
    render(<Pagination page={4} totalPages={5} onPageChange={vi.fn()} />);
    const nextButton = screen.getByText('Next ›');
    expect(nextButton).toBeDisabled();
  });

  it('calls onPageChange with new page index when clicked', () => {
    const handlePageChange = vi.fn();
    render(<Pagination page={0} totalPages={5} onPageChange={handlePageChange} />);
    
    const page2Button = screen.getByText('2');
    fireEvent.click(page2Button);
    expect(handlePageChange).toHaveBeenCalledWith(1);
  });
});
