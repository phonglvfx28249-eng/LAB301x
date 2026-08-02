import React from "react";
import { useMarketResources } from "../../context/MarketResoucesContext.jsx";
import { getOrderBookFormatResponse } from "../../services/marketService.js";

// Presentational Table Component
function OrderBookTable({ rows = [], priceColor }) {
    return (
        <table className="w-full text-xs">
            <thead>
            <tr className="text-gray-600">
                <th className="text-left font-medium pb-1">Price</th>
                <th className="text-left font-medium pb-1">Amount</th>
                <th className="text-left font-medium pb-1">Total</th>
            </tr>
            </thead>
            <tbody>
            {rows.map((row, i) => (
                <tr key={row.id || i} className="text-gray-800">
                    <td className={`py-0.5 font-medium ${priceColor}`}>
                        {row.price}
                    </td>
                    <td className="py-0.5">{row.quantity}</td>
                    <td className="py-0.5">{row.total}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

// Container Component
export default function OrderBook({ sellRowsProps, buyRowsProps }) {
    const { buyOrders, sellOrders } = useMarketResources();

    // Use props if passed explicitly; otherwise, fallback to context data
    const sellRows = sellRowsProps ?? getOrderBookFormatResponse(sellOrders ?? []);
    const buyRows = buyRowsProps ?? getOrderBookFormatResponse(buyOrders ?? []);

    return (
        <div className="space-y-4">
            <div>
                <div className="flex items-baseline gap-2 mb-4">
                    <span className="text-sm font-semibold text-gray-900">
                        Order book
                    </span>
                    <span className="text-xs font-semibold text-red-500">
                        Sell order
                    </span>
                </div>
                <OrderBookTable rows={sellRows} priceColor="text-red-500" />
            </div>

            <div>
                <div className="flex items-baseline gap-2 mb-4">
                    <span className="text-sm font-semibold text-gray-900">
                        Order book
                    </span>
                    <span className="text-xs font-semibold text-green-600">
                        Buy order
                    </span>
                </div>
                <OrderBookTable rows={buyRows} priceColor="text-green-600" />
            </div>
        </div>
    );
}