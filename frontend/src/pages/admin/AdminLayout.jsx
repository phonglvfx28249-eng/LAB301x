import React from "react";
import { Outlet } from "react-router-dom";
import AdminNavBar from "../../components/admin/AdminNavBar.jsx";


export default function AdminLayout() {
  return (
    <div className="min-h-screen bg-[#f5f2ea]">
      <AdminNavBar />
      <Outlet />
    </div>
  );
}
