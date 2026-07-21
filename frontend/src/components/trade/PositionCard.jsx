import React from "react";
import { ArrowUp, ArrowDown } from "lucide-react";

export default function PositionCard({
     type = "Sell order", // "Sell order" | "Buy order"
     price,
     marketPrice,
     amount,
     duration,
     roi,
 }) {
    const isSell = type === "Sell order";
    const roiPositive = !roi.trim().startsWith("-");
    const labelColor = isSell ? "text-red-500" : "text-green-600";

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
        </div>
    );
}