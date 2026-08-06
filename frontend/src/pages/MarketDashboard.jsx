import React from "react";
import OrderBook from "../components/order/OrderBook.jsx";
import TradeHistory from "../components/trade/TradeHistory.jsx";
import CandleChart from "../components/market/TradingviewChart.jsx";
import MarketHeader from "../components/market/MarketHeader.jsx";
import TradingControls from "../components/market/TradingControls.jsx";
import ActivePositionSection from "../components/market/ActivePositionSection.jsx";

import { useMarketResources } from "../context/MarketResoucesContext.jsx";
import { getChartByTimeFormatResponse } from "../services/marketService.js";
import { useAuth } from "../context/AuthContext.jsx";
import { useUserResources } from "../context/UserResourcesContext.jsx";
import { useActivePosition } from "../hooks/useActivePosition.js";
import dayjs from "dayjs";

const TIME_INTERVALS = [
    { label: "1s", value: "1S" },
    { label: "15mins", value: "15M" },
    { label: "1h", value: "1H" },
    { label: "4h", value: "4H" },
    { label: "1day", value: "1D" },
    { label: "1week", value: "1W" },
];

export default function MarketPage() {
    const {
        maxPrice24H = "0.00",
        minPrice24H = "0.00",
        volume24H = "0.00",
        time = "1H",
        setTime,
        marketChartData = [],
        marketPrice = 1
    } = useMarketResources();

    const { token } = useAuth();
    const { wallet, refreshWallet } = useUserResources();

    const {
        activePosition,
        setActivePosition,
        handleClosePosition,
        loading,
        setLoading
    } = useActivePosition(token, refreshWallet);

    const formattedMarketData = getChartByTimeFormatResponse(marketChartData ?? [
        {
            "time": dayjs().unix(),
            "open": marketPrice,
            "high": marketPrice,
            "low": marketPrice,
            "close": marketPrice,
        }
    ]);

    const handleIntervalChange = (newInterval) => {
        if (typeof setTime === "function") {
            setTime(newInterval);
        } else {
            console.warn("setTime function is not available in MarketResourcesContext");
        }
    };

    return (
        <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6">
            <MarketHeader
                maxPrice24H={maxPrice24H}
                minPrice24H={minPrice24H}
                volume24H={volume24H}
            />

            <div className="grid grid-cols-[280px_1fr] gap-8 mt-5">
                {/* Left Column */}
                <div className="space-y-6">
                    <OrderBook />
                    <div className="border-t border-gray-700/40 pt-4">
                        <TradeHistory />
                    </div>
                </div>

                {/* Right Column */}
                <div>
                    {/* Time Tabs */}
                    <div className="flex items-center gap-5 text-xs font-medium text-gray-700 mb-2">
                        <span className="text-gray-900 font-semibold">Time</span>
                        {TIME_INTERVALS.map((t) => (
                            <button
                                key={t.value}
                                onClick={() => handleIntervalChange(t.value)}
                                className={`hover:text-gray-900 transition-colors ${
                                    time === t.value ? "text-gray-900 font-semibold" : "text-gray-600"
                                }`}
                            >
                                {t.label}
                            </button>
                        ))}
                    </div>

                    {/* Chart */}
                    <CandleChart data={formattedMarketData} height={260} />

                    {/* Order Inputs & Controls */}
                    <TradingControls
                        token={token}
                        wallet={wallet}
                        marketPrice={marketPrice}
                        activePosition={activePosition}
                        setActivePosition={setActivePosition}
                        refreshWallet={refreshWallet}
                        loading={loading}
                        setLoading={setLoading}
                    />

                    {/* Active Positions */}
                    <ActivePositionSection
                        activePosition={activePosition}
                        marketPrice={marketPrice}
                        handleClosePosition={handleClosePosition}
                    />
                </div>
            </div>
        </div>
    );
}