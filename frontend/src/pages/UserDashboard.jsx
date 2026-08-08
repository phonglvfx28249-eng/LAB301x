import React from "react";
import TopBar from "../components/common/TopBar.jsx";
import { useAuth } from "../context/AuthContext.jsx";
import WindyLogo from "../components/common/windy-logo.jsx";
import { useUserResources } from "../context/UserResourcesContext.jsx";
import {useMarketResources} from "../context/MarketResoucesContext.jsx";

export default function UserDashboard() {
    const { user } = useAuth();
    const { wallet, loading } = useUserResources();
    const {marketPrice} = useMarketResources();


    // 1. Loading Guard: Wait until initial data is fetched
    if (loading || !wallet) {
        return (
            <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6 flex items-center justify-center">
                <p className="text-gray-700 font-medium">Loading user resources...</p>
            </div>
        );
    }

    // 2. Safely resolve wallet balance (handles camelCase and fallback)
    const availableBalance = wallet.availableBalance ?? wallet.available_balance ?? 0;

// Data for the table
        const marketPriceNum = marketPrice;
        const quantityNum = parseFloat(wallet.locked_balance) || 0;
        const totalAmount = (marketPriceNum * quantityNum).toFixed(2);

        const coins = [
                    {
                id: "wid",
                symbol: "W",
                name: "WID",
                marketPrice: marketPriceNum.toFixed(5),
                quantity: quantityNum,
                amount: totalAmount,
            },
    ];

    return (
        <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6">
            {/* User name */}
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <h1 className="font-serif text-2xl font-bold text-gray-900">
                        {user?.username || "User"}
                    </h1>
                </div>
                <WindyLogo />
            </div>

            {/* Top bar */}
            <TopBar amount={availableBalance} currency="USD" />

            {/* Table */}
            <table className="w-full text-sm mt-6">
                <thead>
                <tr className="text-gray-900 font-semibold border-b border-gray-300">
                    <th className="text-left py-3 font-semibold w-12"></th>
                    <th className="text-left py-3 font-semibold">Coin</th>
                    <th className="text-left py-3 font-semibold">Market price</th>
                    <th className="text-left py-3 font-semibold">Quantity</th>
                    <th className="text-left py-3 font-semibold">Amount</th>
                    <th className="text-left py-3 font-semibold">Action</th>
                </tr>
                </thead>
                <tbody>
                {coins.map((coin) => (
                    <tr key={coin.id} className="text-gray-800 border-b border-gray-200">
                        <td className="py-3">
                            <div className="font-extrabold tracking-tight text-emerald-600 text-xl">
                                {coin.symbol}
                            </div>
                        </td>
                        <td className="py-3">{coin.name}</td>
                        <td className="py-3">{coin.marketPrice}</td>
                        <td className="py-3">{coin.quantity}</td>
                        <td className="py-3">${coin.amount}</td>
                        <td className="py-3">
                            <button className="text-green-600 hover:underline font-medium">
                                Long
                            </button>
                            <span className="text-gray-500"> / </span>
                            <button className="text-red-500 hover:underline font-medium">
                                Short
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}