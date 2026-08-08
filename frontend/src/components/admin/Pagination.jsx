import React from "react";

/**
 * Matches the "Previous 1 2 3 ... 67 68 Next" pattern shown in both
 * mockups. Used by every admin table.
 *
 * page: 0-indexed current page (matches Spring's Pageable convention)
 * totalPages: total page count from PageResponse
 * onPageChange(newPage): called with the 0-indexed target page
 */
export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const current = page + 1; // display as 1-indexed

  const getPageNumbers = () => {
    const pages = [];
    const addPage = (p) => pages.push(p);
    const addEllipsis = () => pages.push("...");

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) addPage(i);
      return pages;
    }

    addPage(1);
    if (current > 4) addEllipsis();

    const start = Math.max(2, current - 1);
    const end = Math.min(totalPages - 1, current + 1);
    for (let i = start; i <= end; i++) addPage(i);

    if (current < totalPages - 3) addEllipsis();
    addPage(totalPages);

    return pages;
  };

  return (
    <div className="flex items-center justify-center gap-1 text-xs text-gray-700 mt-4">
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        className="px-2 py-1 disabled:opacity-40 hover:text-gray-900 transition-colors"
      >
        ‹ Previous
      </button>

      {getPageNumbers().map((p, i) =>
        p === "..." ? (
          <span key={`ellipsis-${i}`} className="px-2 py-1 text-gray-400">
            …
          </span>
        ) : (
          <button
            key={p}
            onClick={() => onPageChange(p - 1)}
            className={`w-6 h-6 rounded flex items-center justify-center transition-colors ${
              p === current
                ? "bg-gray-900 text-white font-semibold"
                : "hover:bg-black/5 text-gray-800"
            }`}
          >
            {p}
          </button>
        )
      )}

      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        className="px-2 py-1 disabled:opacity-40 hover:text-gray-900 transition-colors"
      >
        Next ›
      </button>
    </div>
  );
}
