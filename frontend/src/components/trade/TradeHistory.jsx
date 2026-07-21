import React from "react";

const rows = [
    { price: "66,6834", amount: "2.232", total: "122K", side: "sell" },
    { price: "66,6834", amount: "2.232", total: "122K", side: "sell" },
    { price: "66,6834", amount: "2.232", total: "122K", side: "buy" },
    { price: "66,6834", amount: "2.232", total: "122K", side: "buy" },
    { price: "66,6834", amount: "2.232", total: "122K", side: "sell" },
];

export default function TradeHistory({ trades = rows }) {
    return (
        <div>
            <div className="flex items-baseline gap-2 mb-4">
        <span className="text-sm font-semibold text-gray-900">
          Trade History
        </span>
                <span className="text-xs font-semibold text-green-600">Buy</span>
                <span className="text-xs text-gray-500">/</span>
                <span className="text-xs font-semibold text-red-500">Sell</span>
            </div>

            <table className="w-full text-xs">
                <thead>
                <tr className="text-gray-600">
                    <th className="text-left font-medium pb-1">Price</th>
                    <th className="text-left font-medium pb-1">Amount</th>
                    <th className="text-left font-medium pb-1">Total</th>
                </tr>
                </thead>
                <tbody>
                {trades.map((row, i) => (
                    <tr key={i} className="text-gray-800">
                        <td
                            className={`py-0.5 font-medium ${
                                row.side === "sell" ? "text-red-500" : "text-green-600"
                            }`}
                        >
                            {row.price}
                        </td>
                        <td className="py-0.5">{row.amount}</td>
                        <td className="py-0.5">{row.total}</td>
                    </tr>
                ))}
                </tbody>
            </table>

            <button className="flex items-center gap-1 text-xs text-gray-800 mt-2 hover:opacity-70 transition-opacity">
                More <span aria-hidden>→</span>
            </button>
        </div>
    );
}