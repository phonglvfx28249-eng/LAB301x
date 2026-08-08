import React from "react";
import { Search, Menu } from "lucide-react";

/** Matches the "Search user" pill input shown in the User management mockup. */
export default function SearchBar({ value, onChange, placeholder = "Search" }) {
  return (
    <div className="flex items-center gap-2 px-3 py-1.5 rounded-md border border-gray-700/40 bg-[#EAE3D5] w-64">
      <Menu size={14} className="text-gray-500" strokeWidth={2} />
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="flex-1 bg-transparent text-sm text-gray-800 placeholder-gray-500 focus:outline-none"
      />
      <Search size={14} className="text-gray-500" strokeWidth={2} />
    </div>
  );
}
