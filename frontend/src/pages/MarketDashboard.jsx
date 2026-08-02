import React, { useState } from "react";
import { Wallet, Lock } from "lucide-react";
import WindyLogo from "../components/common/windy-logo.jsx";
import OrderBook from "../components/order/OrderBook.jsx";
import TradeHistory from "../components/trade/TradeHistory.jsx";
import CandleChart from "../components/market/TradingviewChart.jsx";
import PositionCard from "../components/trade/PositionCard.jsx";
import { useMarketResources } from "../context/MarketResoucesContext.jsx";
import { getChartByTimeFormatResponse } from "../services/marketService.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useUserResources} from "../context/UserResourcesContext.jsx";

const TIME_INTERVALS = [
    { label: "1s", value: "1S" },
    { label: "15mins", value: "15M" },
    { label: "1h", value: "1H" },
    { label: "4h", value: "4H" },
    { label: "1day", value: "1D" },
    { label: "1week", value: "1W" },
];

const positions = [
    // {
    //     type: "Sell order",
    //     price: "67.037,37",
    //     marketPrice: "67.037,37",
    //     amount: "1 W",
    //     roi: "+5%",
    // },
    // {
    //     type: "Buy order",
    //     price: "67.037,37",
    //     marketPrice: "67.037,37",
    //     amount: "1 W",
    //     roi: "-5%",
    // },
    // {
    //     type: "Buy order",
    //     price: "67.037,37",
    //     marketPrice: "67.037,37",
    //     amount: "1 W",
    //     roi: "-10%",
    // },
];

export default function MarketPage() {
    const [orderTab, setOrderTab] = useState("Limit"); // "Limit" | "Market"

    // Extract context resources safely
    const {
        maxPrice24H = "0.00",
        minPrice24H = "0.00",
        volume24H = "0.00",
        time = "1H",
        setTime,
        marketChartData = [],
        marketPrice = "0.00"
    } = useMarketResources();

    const {token} = useAuth();
    const {wallet} = useUserResources();

    console.log(time);


    // Safely format chart data
    const formattedMarketData = getChartByTimeFormatResponse(marketChartData ?? []);

    // Safe interval switch handler
    const handleIntervalChange = (newInterval) => {
        if (typeof setTime === "function") {
            setTime(newInterval);
        } else {
            console.warn("setTime function is not available in MarketResourcesContext");
        }
    };

    return (
        <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6">
            {/* Header */}
            <div className="flex items-center justify-between pb-4 border-b border-gray-700/50">
                <div className="flex items-center gap-3">
                    <div className="font-extrabold tracking-tight text-brand text-emerald-600 text-stroke-1 text-stroke-color-black text-3xl flex items-center justify-center">
                        W
                    </div>
                    <div>
                        <p className="text-sm font-bold text-gray-900 leading-tight">
                            W/USD
                        </p>
                        <p className="text-xs text-gray-600 leading-tight">W COIN</p>
                    </div>
                </div>

                <div className="flex items-center gap-10 text-sm">
                    <div>
                        <p className="text-xs font-semibold text-gray-900">
                            Max price 24h
                        </p>
                        <p className="text-sm text-gray-800">{maxPrice24H} $</p>
                    </div>
                    <div>
                        <p className="text-xs font-semibold text-gray-900">
                            Min price 24h
                        </p>
                        <p className="text-sm text-gray-800">{minPrice24H} $</p>
                    </div>
                    <div>
                        <p className="text-xs font-semibold text-gray-900">Volume 24h</p>
                        <p className="text-sm text-gray-800">{volume24H}</p>
                    </div>
                </div>

                <WindyLogo />
            </div>

            {/* Main content: left (order book/history) + right (chart/trade panel) */}
            <div className="grid grid-cols-[280px_1fr] gap-8 mt-5">
                {/* Left column */}
                <div className="space-y-6">
                    <OrderBook />
                    <div className="border-t border-gray-700/40 pt-4">
                        <TradeHistory />
                    </div>
                </div>

                {/* Right column */}
                <div>
                    {/* Time interval tabs */}
                    <div className="flex items-center gap-5 text-xs font-medium text-gray-700 mb-2">
                        <span className="text-gray-900 font-semibold">Time</span>
                        {TIME_INTERVALS.map((t) => (
                            <button
                                key={t.value}
                                onClick={() => handleIntervalChange(t.value)}
                                className={`hover:text-gray-900 transition-colors ${
                                    time === t.value
                                        ? "text-gray-900 font-semibold"
                                        : "text-gray-600"
                                }`}
                            >
                                {t.label}
                            </button>
                        ))}
                    </div>

                    {/* Candlestick chart */}
                    <CandleChart
                        data={formattedMarketData}
                        height={260}
                    />

                    {/* Limit/Market tabs + wallet info */}
                    <div className="flex items-center justify-between mt-4 mb-3">
                        <div className="flex items-center gap-5 text-sm font-semibold">
                            <button
                                onClick={() => setOrderTab("Limit")}
                                className={`pb-1 border-b-2 transition-colors ${
                                    orderTab === "Limit"
                                        ? "border-gray-900 text-gray-900"
                                        : "border-transparent text-gray-500"
                                }`}
                            >
                                Limit
                            </button>
                            <button
                                onClick={() => setOrderTab("Market")}
                                className={`pb-1 border-b-2 transition-colors ${
                                    orderTab === "Market"
                                        ? "border-gray-900 text-gray-900"
                                        : "border-transparent text-gray-500"
                                }`}
                            >
                                Market
                            </button>
                        </div>

                        <div className="flex items-center gap-6 text-sm text-gray-900">
                            <div className="flex items-center gap-1.5">
                                <Wallet size={15} strokeWidth={2} />
                                <span>
                                    Amount: <span className="font-medium"> {token ? wallet.available_balance : "0"}</span>
                                </span>
                            </div>
                            <div className="flex items-center gap-1.5">
                                <Lock size={15} strokeWidth={2} />
                                <span>
                                    Locked: <span className="font-medium"> {token ? wallet.locked_balance : "0"}</span>
                                </span>
                            </div>
                        </div>
                    </div>

                    {/* Price / Amount inputs */}
                    <div className="grid grid-cols-2 gap-4 mb-3">
                        <div className="flex items-center justify-between px-4 py-2.5 rounded-md border border-gray-700/50 bg-transparent">
                            <span className="text-sm text-gray-500">Price</span>
                            <span className="text-sm font-medium text-gray-900">
                                {marketPrice}
                            </span>
                        </div>
                        <div className="flex items-center justify-between px-4 py-2.5 rounded-md border border-gray-700/50 bg-transparent">
                            <span className="text-sm text-gray-500">Amount</span>
                            <span className="text-sm font-medium text-gray-900">W</span>
                        </div>
                    </div>

                    {/* Long / Short buttons */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <button className="py-2.5 rounded-md bg-[#8FBF6B] hover:bg-[#7FAE5B] text-white text-sm font-semibold tracking-wide transition-colors"
                            {...(!token && { disabled: true })}
                        >
                            {token ? "LONG" : "Login to Trade"}
                        </button>
                        <button className="py-2.5 rounded-md bg-[#E06A5C] hover:bg-[#D0594B] text-white text-sm font-semibold tracking-wide transition-colors"
                            {...(!token && { disabled: true })}
                        >
                            {token ? "SHORT" : "Login to Trade"}
                        </button>
                    </div>

                    {/* Positions list */}
                    <div className="space-y-3">
                        {positions.map((pos, i) => (
                            <PositionCard key={i} {...pos} />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}