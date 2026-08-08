import React, { useEffect, useState, useCallback } from "react";
import AdminHeader from "../../components/admin/AdminHeader.jsx";
import Pagination from "../../components/admin/Pagination.jsx";
import SearchBar from "../../components/admin/SearchBar.jsx";
import { getAdminAuditLogs } from "../../api/auditApi.js"; // 👈 Import API function

const PAGE_SIZE = 8;

export default function AuditLogManagement() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAdminAuditLogs(page, PAGE_SIZE, search);
      setLogs(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to fetch audit logs:", err);
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  useEffect(() => {
    setPage(0);
  }, [search]);

  return (
      <div className="min-h-screen w-full bg-[#f5f2ea]">
        <h1 className="px-8 pt-6 text-lg font-medium text-gray-500 mb-4">
          audit log management
        </h1>

        <div className="mx-8 bg-[#EAE3D5] rounded-md px-8 py-6">
          <AdminHeader
              right={<SearchBar value={search} onChange={setSearch} placeholder="Search action" />}
          />

          <table className="w-full text-sm">
            <thead>
            <tr className="text-left text-gray-900 font-semibold border-b border-gray-700/30">
              <th className="py-2 pr-4 font-semibold">Time</th>
              <th className="py-2 pr-4 font-semibold">User ID</th>
              <th className="py-2 pr-4 font-semibold">Action</th>
              <th className="py-2 pr-4 font-semibold">Entity</th>
              <th className="py-2 pr-4 font-semibold">Description</th>
              <th className="py-2 pr-4 font-semibold">IP Address</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-gray-700/10">
            {logs.map((log) => (
                <tr key={log.id} className="text-gray-800">
                  <td className="py-3 pr-4 whitespace-nowrap">
                    {new Date(log.createdAt).toLocaleString()}
                  </td>
                  <td className="py-3 pr-4">{log.userId ?? "—"}</td>
                  <td className="py-3 pr-4 font-medium">{log.action}</td>
                  <td className="py-3 pr-4">
                    {log.entityName}
                    {log.entityId ? ` #${log.entityId}` : ""}
                  </td>
                  <td className="py-3 pr-4 max-w-xs truncate">{log.description}</td>
                  <td className="py-3 pr-4">{log.ipAddress}</td>
                </tr>
            ))}

            {!loading && logs.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-gray-500">
                    No audit log entries found.
                  </td>
                </tr>
            )}
            </tbody>
          </table>

          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>
  );
}