import React, { useEffect, useState, useCallback } from "react";
import AdminHeader from "../../components/admin/AdminHeader.jsx";
import Pagination from "../../components/admin/Pagination.jsx";
import { getAdminTrades } from "../../api/tradeApi.js"; // 👈 Import API function

const PAGE_SIZE = 8;

export default function TradeManagement() {
  const [trades, setTrades] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchTrades = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAdminTrades(page, PAGE_SIZE);
      setTrades(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to fetch trades:", err);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchTrades();
  }, [fetchTrades]);

  return (
      <div className="min-h-screen w-full bg-[#f5f2ea]">
        <h1 className="px-8 pt-6 text-lg font-medium text-gray-500 mb-4">
          trade management
        </h1>

        <div className="mx-8 bg-[#EAE3D5] rounded-md px-8 py-6">
          <AdminHeader />

          <table className="w-full text-sm">
            <thead>
            <tr className="text-left text-gray-900 font-semibold border-b border-gray-700/30">
              <th className="py-2 pr-4 font-semibold">Trade ID</th>
              <th className="py-2 pr-4 font-semibold">Buyer</th>
              <th className="py-2 pr-4 font-semibold">Seller</th>
              <th className="py-2 pr-4 font-semibold">Price</th>
              <th className="py-2 pr-4 font-semibold">Quantity</th>
              <th className="py-2 pr-4 font-semibold">Total</th>
              <th className="py-2 pr-4 font-semibold">Time</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-gray-700/10">
            {trades.map((t) => (
                <tr key={t.id} className="text-gray-800">
                  <td className="py-3 pr-4">#{t.id}</td>
                  <td className="py-3 pr-4">{`user-${t.buyerId}`}</td>
                  <td className="py-3 pr-4">{`user-${t.sellerId}`}</td>
                  <td className="py-3 pr-4">{Number(t.tradePrice).toLocaleString()}</td>
                  <td className="py-3 pr-4">{Number(t.quantity).toLocaleString()}</td>
                  <td className="py-3 pr-4">{Number(t.totalAmount).toLocaleString()}$</td>
                  <td className="py-3 pr-4 whitespace-nowrap">
                    {new Date(t.createdAt).toLocaleString()}
                  </td>
                </tr>
            ))}

            {!loading && trades.length === 0 && (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-gray-500">
                    No trades found.
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