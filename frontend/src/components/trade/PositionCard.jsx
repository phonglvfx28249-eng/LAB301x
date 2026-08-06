import React from "react";
import { ArrowUp, ArrowDown, X } from "lucide-react";

export default function PositionCard({
                                         type = "Sell order", // "Sell order" | "Buy order"
                                         price,
                                         marketPrice,
                                         amount,
                                         status = "FILLED", // Added status prop
                                         roi,
                                         onClose,
                                     }) {
    const isSell = type === "Sell order";
    const roiPositive = !roi.trim().startsWith("-");
    const labelColor = isSell ? "text-red-500" : "text-green-600";

    // Helper to style status badges dynamically
    const getStatusStyle = (statusStr) => {
        switch (statusStr?.toUpperCase()) {
            case "FILLED":
                return "bg-green-100 text-green-700 border-green-200";
            case "PARTIAL":
            case "PENDING":
                return "bg-amber-100 text-amber-700 border-amber-200";
            case "CANCELLED":
                return "bg-gray-100 text-gray-600 border-gray-200";
            default:
                return "bg-gray-100 text-gray-700 border-gray-200";
        }
    };

    return (
        <div className="border border-gray-700/50 rounded-md px-5 py-4 flex items-center justify-between">
            <span className={`text-sm font-semibold ${labelColor} w-28`}>
                {type}
            </span>

            <div className="text-center">
                <p className="text-xs text-gray-500">Price</p>
                <p className="text-sm font-medium text-gray-900">{price}</p>
            </div>

            <div className="text-center">
                <p className="text-xs text-gray-500">Market Price</p>
                <p className="text-sm font-medium text-gray-900">{marketPrice}</p>
            </div>

            <div className="text-center">
                <p className="text-xs text-gray-500">Amount</p>
                <p className="text-sm font-medium text-gray-900">{amount}</p>
            </div>

            {/* Added Status Field */}
            <div className="text-center">
                <p className="text-xs text-gray-500 mb-0.5">Status</p>
                <span className={`text-xs font-semibold px-2 py-0.5 rounded border ${getStatusStyle(status)}`}>
                    {status}
                </span>
            </div>

            <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-gray-900">
                    ROI: {roi}
                </span>
                {roiPositive ? (
                    <ArrowUp size={16} className="text-green-600" strokeWidth={2.5} />
                ) : (
                    <ArrowDown size={16} className="text-red-500" strokeWidth={2.5} />
                )}
            </div>

            <button
                onClick={onClose}
                aria-label={`Close ${type}`}
                className="ml-4 p-1.5 rounded-md text-red-500 border border-red-500/40 hover:bg-red-500 hover:text-white transition-colors"
            >
                <X size={14} strokeWidth={2.5} />
            </button>
        </div>
    );
}