import React from "react";
import { Wallet, UserCircle2, FileClock, LineChart } from "lucide-react";
import {Link} from "react-router-dom";

export default function TopBar({ amount = 1000, currency = "USD" }) {
    return (
        <div className="flex items-center justify-between border-b border-gray-700/60 pb-3 mb-2">
            <div className="flex items-center gap-2 text-sm text-gray-900">
                <Wallet size={16} strokeWidth={2} />
                <span>
          Amount:{" "}
                    <span className="font-medium">
            {amount}$ ({currency})
          </span>
        </span>
            </div>

            <nav className="flex items-center gap-10 text-sm font-semibold text-gray-900">
                <Link to="/user/account">
                    <button className="flex items-center gap-1.5 hover:opacity-70 transition-opacity">
                        <span>Account</span>
                        <UserCircle2 size={16} strokeWidth={2} />
                    </button>
                </Link>
                <Link to="/user/history">
                    <button className="flex items-center gap-1.5 hover:opacity-70 transition-opacity">
                        <span>History trade</span>
                        <FileClock size={16} strokeWidth={2} />
                    </button>
                </Link>
                <Link to="/market">
                    <button className="flex items-center gap-1.5 hover:opacity-70 transition-opacity">
                        <span>Market</span>
                        <LineChart size={16} strokeWidth={2} />
                    </button>
                </Link>
            </nav>
        </div>
    );
}