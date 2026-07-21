import React, { useState } from "react";
import { Wallet, Lock, ChevronDown, Filter, ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";
import WindyLogo from "../components/common/windy-logo.jsx";

const trades = [
    {
        id: "wid-1",
        symbol: "W",
        coin: "WID",
        status: "Partial",
        statusColor: "bg-yellow-500",
        position: "Long",
        positionColor: "text-green-600",
        amount: "1,2000,3232",
        time: "2026-06-03 20:00:00",
    },
];

function Dropdown({ label, value }) {
    return (
        <button className="flex items-center justify-between gap-3 px-3 py-2 rounded-md border border-gray-700/50 text-sm text-gray-900 min-w-[140px]">
            <span className="text-gray-500">{label}</span>
            <span className="flex items-center gap-1 font-semibold">
        {value}
                <ChevronDown size={14} strokeWidth={2} />
      </span>
        </button>
    );
}

export default function AccountDashboard() {
    const [wallet, setWallet] = useState("Spot");
    const [currency, setCurrency] = useState("BNB");
    const [status, setStatus] = useState("Filled");

    return (
        <div className="min-h-screen w-full bg-[#EAE3D5] px-8 py-6">
            {/* Header row: name + logo */}
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <Link
                        to="/user/dashboard"
                        className="p-1 rounded-md hover:bg-black/5 transition-colors text-gray-900"
                    >
                        <ArrowLeft size={22} strokeWidth={2} />
                    </Link>
                    <h1 className="font-serif text-2xl font-bold text-gray-900">
                        Wind Lê
                    </h1>
                </div>
                <WindyLogo />
            </div>

            {/* Amount / Locked */}
            <div className="flex items-center justify-between">

                <div className="flex items-center gap-8 text-sm text-gray-900 mb-4">
                    <div className="flex items-center gap-2">
                        <Wallet size={16} strokeWidth={2} />
                        <span>
                Amount: <span className="font-medium">1000$ (USD)</span>
              </span>
                    </div>
                    <div className="flex items-center gap-2">
                        <Lock size={16} strokeWidth={2} />
                        <span>
                Locked: <span className="font-medium">500$ (USD)</span>
              </span>
                    </div>
                </div>

                {/* Filters */}
                <div className="flex items-center gap-4 mb-2 ">
                    <Dropdown label="Wallet" value={wallet} />
                    <Dropdown label="Currency" value={currency} />
                    <Dropdown label="Status" value={status} />
                    <button className="p-2  ml-2">
                        <Filter size={16} strokeWidth={2} />
                    </button>
                </div>
            </div>

            <div className="border-b border-gray-700/60 mb-2" />

            {/* Table */}
            <table className="w-full text-sm">
                <thead>
                <tr className="text-gray-900 font-semibold">
                    <th className="text-left py-3 font-semibold w-12"></th>
                    <th className="text-left py-3 font-semibold">Coin</th>
                    <th className="text-left py-3 font-semibold">Status</th>
                    <th className="text-left py-3 font-semibold">Position</th>
                    <th className="text-left py-3 font-semibold">Amount</th>
                    <th className="text-left py-3 font-semibold">Time</th>
                </tr>
                </thead>
                <tbody>
                {trades.map((trade) => (
                    <tr key={trade.id} className="text-gray-800">
                        <td className="py-3">
                            <div className="font-extrabold tracking-tight text-brand text-emerald-600 text-stroke-1 text-stroke-color-black text-xl">
                                {trade.symbol}
                            </div>
                        </td>
                        <td className="py-3">{trade.coin}</td>
                        <td className="py-3">
                <span className="flex items-center gap-2">
                  <span
                      className={`w-2.5 h-2.5 rounded-full ${trade.statusColor}`}
                  />
                    {trade.status}
                </span>
                        </td>
                        <td className={`py-3 font-medium ${trade.positionColor}`}>
                            {trade.position}
                        </td>
                        <td className="py-3">{trade.amount}</td>
                        <td className="py-3">{trade.time}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}