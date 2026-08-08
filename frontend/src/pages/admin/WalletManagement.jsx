import React, { useEffect, useState, useCallback } from "react";
import { Wallet, Lock } from "lucide-react";
import AdminHeader from "../../components/admin/AdminHeader.jsx";
import Pagination from "../../components/admin/Pagination.jsx";
import SearchBar from "../../components/admin/SearchBar.jsx";
import { getAdminWallets } from "../../api/walletApi.js"; // 👈 Import API function

const PAGE_SIZE = 5;

export default function WalletManagement() {
  const [wallets, setWallets] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchWallets = useCallback(async () => {
    setLoading(true);
    try {
      // Clean call using the new API module
      const data = await getAdminWallets(page, PAGE_SIZE, search);
      setWallets(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to fetch wallets:", err);
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    fetchWallets();
  }, [fetchWallets]);

  useEffect(() => {
    setPage(0);
  }, [search]);

  // Platform-wide totals calculated from page items
  const totalAvailable = wallets.reduce((sum, w) => sum + Number(w.availableBalance || 0), 0);
  const totalLocked = wallets.reduce((sum, w) => sum + Number(w.lockedBalance || 0), 0);

  return (
      <div className="min-h-screen w-full bg-[#f5f2ea]">
        <h1 className="px-8 pt-6 text-lg font-medium text-gray-500 mb-4">
          wallet management
        </h1>

        <div className="mx-8 bg-[#EAE3D5] rounded-md px-8 py-6">
          <AdminHeader
              right={<SearchBar value={search} onChange={setSearch} placeholder="Search user" />}
          />

          <div className="flex items-center gap-8 text-sm text-gray-900 mb-4">
            <div className="flex items-center gap-2">
              <Wallet size={16} strokeWidth={2} />
              <span>
              Amount:{" "}
                <span className="font-medium">
                {totalAvailable.toLocaleString()}$ (USD)
              </span>
            </span>
            </div>
            <div className="flex items-center gap-2">
              <Lock size={16} strokeWidth={2} />
              <span>
              Locked:{" "}
                <span className="font-medium">
                {totalLocked.toLocaleString()}$ (USD)
              </span>
            </span>
            </div>
          </div>

          <table className="w-full text-sm">
            <thead>
            <tr className="text-left text-gray-900 font-semibold border-b border-gray-700/30">
              <th className="py-2 pr-4 font-semibold">Username</th>
              <th className="py-2 pr-4 font-semibold">Wallet</th>
              <th className="py-2 pr-4 font-semibold">Amount</th>
              <th className="py-2 pr-4 font-semibold">Locked</th>
              <th className="py-2 pr-4 font-semibold">Trade History</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-gray-700/10">
            {wallets.map((w) => (
                <tr key={w.userId} className="text-gray-800">
                  <td className="py-3 pr-4">{w.username}</td>
                  <td className="py-3 pr-4 truncate max-w-[140px]">
                    {`wallet-${w.walletId}`}
                  </td>
                  <td className="py-3 pr-4">{Number(w.availableBalance).toLocaleString()}$</td>
                  <td className="py-3 pr-4">{Number(w.lockedBalance).toLocaleString()}$</td>
                  <td className="py-3 pr-4">
                    <a
                        href={w.tradeHistoryUrl}
                        className="text-[#107980] hover:underline"
                    >
                      {`https://phwindy/trade/?user=${w.userId}`}
                    </a>
                  </td>
                </tr>
            ))}

            {!loading && wallets.length === 0 && (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-gray-500">
                    No wallets found.
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