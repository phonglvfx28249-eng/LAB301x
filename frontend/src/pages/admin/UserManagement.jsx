import React, { useEffect, useState, useCallback } from "react";
import AdminHeader from "../../components/admin/AdminHeader.jsx";
import Pagination from "../../components/admin/Pagination.jsx";
import SearchBar from "../../components/admin/SearchBar.jsx";
import { getAdminUsers, deleteUser } from "../../api/userApi.js"; // 👈 Import API functions

const PAGE_SIZE = 5;

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAdminUsers(page, PAGE_SIZE, search);
      setUsers(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to fetch users:", err);
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  // Reset to first page whenever search term changes
  useEffect(() => {
    setPage(0);
  }, [search]);

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this user? This cannot be undone.")) return;
    try {
      await deleteUser(id);
      fetchUsers();
    } catch (err) {
      console.error("Failed to delete user:", err);
      alert("Could not delete user.");
    }
  };

  return (
      <div className="min-h-screen w-full bg-[#f5f2ea]">
        <h1 className="px-8 pt-6 text-lg font-medium text-gray-500 mb-4">
          User management
        </h1>

        <div className="mx-8 bg-[#EAE3D5] rounded-md px-8 py-6">
          <AdminHeader />

          <table className="w-full text-sm">
            <thead>
            <tr className="text-left text-gray-900 font-semibold border-b border-gray-700/30">
              <th className="py-2 pr-4 font-semibold">No</th>
              <th className="py-2 pr-4 font-semibold">Username</th>
              <th className="py-2 pr-4 font-semibold">Email</th>
              <th className="py-2 pr-4 font-semibold">Country</th>
              <th className="py-2 pr-4 font-semibold">Role</th>
              <th className="py-2 pr-4 font-semibold">Action</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-gray-700/10">
            {users.map((u, i) => (
                <tr key={u.id} className="text-gray-800">
                  <td className="py-3 pr-4">{page * PAGE_SIZE + i + 1}</td>
                  <td className="py-3 pr-4">{u.username}</td>
                  <td className="py-3 pr-4">{u.email}</td>
                  <td className="py-3 pr-4">{u.country}</td>
                  <td className="py-3 pr-4 capitalize">{u.role?.toLowerCase()}</td>
                  <td className="py-3 pr-4">
                    <div className="flex items-center gap-2">
                      <button className="px-4 py-1.5 rounded-md bg-[#8FBF6B] hover:bg-[#7FAE5B] text-white text-xs font-semibold transition-colors">
                        Detail
                      </button>
                      <button
                          onClick={() => handleDelete(u.id)}
                          className="px-4 py-1.5 rounded-md bg-[#E06A5C] hover:bg-[#D0594B] text-white text-xs font-semibold transition-colors"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
            ))}

            {!loading && users.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-gray-500">
                    No users found.
                  </td>
                </tr>
            )}
            </tbody>
          </table>

          <div className="flex items-center justify-between mt-4">
            <SearchBar value={search} onChange={setSearch} placeholder="Search user" />
            <div className="flex-1">
              <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
            </div>
          </div>
        </div>
      </div>
  );
}