import React from "react";
import PositionCard from "../trade/PositionCard.jsx";

export default function ActivePositionSection({ activePosition, marketPrice, handleClosePosition }) {
    if (!activePosition) return null;

    // 1. Resolve entry price and current market price safely
    const entryPrice = Number(activePosition.entryPrice || activePosition.price || 0);
    const currentPrice = Number(activePosition.currentMarketPrice || marketPrice || 0);
    const side = activePosition.side; // "BUY" or "SELL"

    // 2. Calculate ROI percentage accurately
    let roiPercentage = 0;
    if (entryPrice > 0 && currentPrice > 0) {
        if (side === "BUY") {
            roiPercentage = ((currentPrice - entryPrice) / entryPrice) * 100;
        } else if (side === "SELL") {
            roiPercentage = ((entryPrice - currentPrice) / entryPrice) * 100;
        }
    }

    // 3. Format string with '+' sign for positive ROI
    const formattedRoi = `${roiPercentage >= 0 ? "+" : ""}${roiPercentage.toFixed(2)}%`;

    return (
        <div className="space-y-3">
            <PositionCard
                type={side === "BUY" ? "Buy order" : "Sell order"}
                price={entryPrice}
                marketPrice={currentPrice}
                amount={`${activePosition.quantity} W`}
                roi={formattedRoi}
                status={activePosition.status || "FILLED"}
                onClose={handleClosePosition}
            />
        </div>
    );
}