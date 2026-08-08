import React, { useEffect, useState, useCallback } from "react";
import { Link2 } from "lucide-react";
import AdminHeader from "../../components/admin/AdminHeader.jsx";
import Pagination from "../../components/admin/Pagination.jsx";
import { getAdminBlocks } from "../../api/blockchainApi.js"; // 👈 Import API function

const PAGE_SIZE = 6;

function truncateHash(hash) {
  if (!hash) return "—";
  return hash.length > 14 ? `${hash.slice(0, 8)}…${hash.slice(-6)}` : hash;
}

/** One block card in the visual chain. */
function BlockCard({ block }) {
  return (
      <div className="min-w-[180px] rounded-md border border-gray-700/50 bg-[#f5f2ea] px-4 py-3 shrink-0">
        <p className="text-xs font-semibold text-gray-500 mb-1">
          Block #{block.blockIndex}
        </p>
        <p className="text-xs text-gray-800 font-mono truncate" title={block.currentHash}>
          {truncateHash(block.currentHash)}
        </p>
        <p className="text-[11px] text-gray-500 mt-2">
          {block.transactionCount} tx{block.transactionCount === 1 ? "" : "s"}
        </p>
        <p className="text-[11px] text-gray-500">
          {new Date(block.createdAt).toLocaleString()}
        </p>
      </div>
  );
}

export default function BlockchainManagement() {
  const [blocks, setBlocks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchBlocks = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAdminBlocks(page, PAGE_SIZE);
      setBlocks(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Failed to fetch blocks:", err);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchBlocks();
  }, [fetchBlocks]);

  return (
      <div className="min-h-screen w-full bg-[#f5f2ea]">
        <h1 className="px-8 pt-6 text-lg font-medium text-gray-500 mb-4">
          blockchain management
        </h1>

        <div className="mx-8 bg-[#EAE3D5] rounded-md px-8 py-6">
          <AdminHeader />

          {/* Visual chain — blocks connected by a link icon, newest first */}
          <div className="flex items-center gap-3 overflow-x-auto pb-3 mb-6">
            {blocks.map((block, i) => (
                <React.Fragment key={block.id}>
                  <BlockCard block={block} />
                  {i < blocks.length - 1 && (
                      <Link2
                          size={16}
                          className="text-gray-400 shrink-0"
                          strokeWidth={2}
                      />
                  )}
                </React.Fragment>
            ))}
            {!loading && blocks.length === 0 && (
                <p className="text-sm text-gray-500 py-4">No blocks found.</p>
            )}
          </div>

          {/* Detail table */}
          <table className="w-full text-sm">
            <thead>
            <tr className="text-left text-gray-900 font-semibold border-b border-gray-700/30">
              <th className="py-2 pr-4 font-semibold">Index</th>
              <th className="py-2 pr-4 font-semibold">Current Hash</th>
              <th className="py-2 pr-4 font-semibold">Previous Hash</th>
              <th className="py-2 pr-4 font-semibold">Merkle Root</th>
              <th className="py-2 pr-4 font-semibold">Nonce</th>
              <th className="py-2 pr-4 font-semibold">Tx Count</th>
              <th className="py-2 pr-4 font-semibold">Time</th>
            </tr>
            </thead>
            <tbody className="divide-y divide-gray-700/10">
            {blocks.map((block) => (
                <tr key={block.id} className="text-gray-800">
                  <td className="py-3 pr-4">{block.blockIndex}</td>
                  <td className="py-3 pr-4 font-mono" title={block.currentHash}>
                    {truncateHash(block.currentHash)}
                  </td>
                  <td className="py-3 pr-4 font-mono" title={block.previousHash}>
                    {truncateHash(block.previousHash)}
                  </td>
                  <td className="py-3 pr-4 font-mono" title={block.merkleRoot}>
                    {truncateHash(block.merkleRoot)}
                  </td>
                  <td className="py-3 pr-4">{block.nonce}</td>
                  <td className="py-3 pr-4">{block.transactionCount}</td>
                  <td className="py-3 pr-4 whitespace-nowrap">
                    {new Date(block.createdAt).toLocaleString()}
                  </td>
                </tr>
            ))}

            {!loading && blocks.length === 0 && (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-gray-500">
                    No blocks found.
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