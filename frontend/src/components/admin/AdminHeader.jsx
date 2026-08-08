import React from "react";
import WindyLogo from "../common/windy-logo.jsx";

/** The "Admin dashboard [search] WINDY EXCHANGE" bar shown atop every panel. */
export default function AdminHeader({ right }) {
  return (
    <div className="flex items-center justify-between mb-4">
      <span className="text-sm font-semibold text-gray-900">
        Admin dashboard
      </span>
      <div className="flex items-center gap-6">
        {right}
        <WindyLogo />
      </div>
    </div>
  );
}
