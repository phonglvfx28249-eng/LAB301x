import React from "react";
import { getTradeHistoryFormatResponse } from "../../services/marketService.js";
import { useMarketResources } from "../../context/MarketResoucesContext.jsx";

export default function TradeHistory({ tradesProps }) {
    const { tradeHistory } = useMarketResources();

    // Use props if explicitly passed; otherwise, fall back to context data
    const trades = tradesProps ?? getTradeHistoryFormatResponse(tradeHistory ?? []);

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
                    <tr key={row.id || i} className="text-gray-800">
                        <td
                            className={`py-0.5 font-medium ${
                                row.side === "SELL" ? "text-red-500" : "text-green-600"
                            }`}
                        >
                            {row.price}
                        </td>
                        <td className="py-0.5">{row.quantity}</td>
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