import React from "react";
import { NavLink } from "react-router-dom";
import { Users, Wallet, ScrollText, Blocks, ArrowLeftRight, LogOut } from "lucide-react";

const NAV_ITEMS = [
  { to: "/admin/users", label: "User management", icon: Users },
  { to: "/admin/wallets", label: "Wallet management", icon: Wallet },
  { to: "/admin/audit-logs", label: "Audit log management", icon: ScrollText },
  { to: "/admin/blockchain", label: "Blockchain", icon: Blocks },
  { to: "/admin/trades", label: "Trade management", icon: ArrowLeftRight },
];

/** Top nav for the whole admin area — one place to switch between all 5 modules. */
export default function AdminNavBar() {
  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className="flex items-center justify-between px-8 py-3 bg-[#EAE3D5] border-b border-gray-700/50">
      <nav className="flex items-center gap-6 text-sm font-medium text-gray-800">
        {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-1.5 pb-1 border-b-2 transition-colors ${
                isActive
                  ? "border-[#107980] text-gray-900 font-semibold"
                  : "border-transparent hover:text-gray-900"
              }`
            }
          >
            <Icon size={15} strokeWidth={2} />
            {label}
          </NavLink>
        ))}
      </nav>

      <button
        onClick={handleLogout}
        className="flex items-center gap-1.5 text-sm text-red-500 hover:opacity-70 transition-opacity"
      >
        Logout <LogOut size={15} strokeWidth={2} />
      </button>
    </div>
  );
}
