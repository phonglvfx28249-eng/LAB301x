import React from "react";
import TopBar from "../components/common/TopBar.jsx";
import {useAuth} from "../context/AuthContext.jsx";


const coins = [
    {
        id: "wid",
        symbol: "W",
        name: "WID",
        marketPrice: "1,00322",
        quantity: "0",
        amount: "0",
    },
];

export default function UserDashboard() {

    const user = useAuth();

    return (
        <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6">
            {/* User name */}
            <h1 className="font-serif text-2xl font-bold text-gray-900 mb-4">
                {user.username}
            </h1>

            {/* Top bar */}
            <TopBar amount={1000} currency="USD" />

            {/* Table */}
            <table className="w-full text-sm">
                <thead>
                <tr className="text-gray-900 font-semibold">
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
                    <tr key={coin.id} className="text-gray-800">
                        <td className="py-3">
                            <div className="font-extrabold tracking-tight text-brand text-emerald-600 text-stroke-1 text-stroke-color-black text-xl">
                                {coin.symbol}
                            </div>
                        </td>
                        <td className="py-3">{coin.name}</td>
                        <td className="py-3">{coin.marketPrice}</td>
                        <td className="py-3">{coin.quantity}</td>
                        <td className="py-3">{coin.amount}</td>
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