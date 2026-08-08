import React from "react";
import { Wallet, UserCircle2, FileClock, LineChart, LogOut } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import {useUserResources} from "../../context/UserResourcesContext.jsx";
import {useAuth} from "../../context/AuthContext.jsx";

export default function TopBar({ amount = 1000, currency = "USD" }) {
    const navigate = useNavigate();
    const {logout} = useAuth();

    const handleLogout = () => {

        logout();
        navigate("/login");
    };

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
                <button
                    type="button"
                    onClick={handleLogout}
                    className="flex items-center gap-1.5 text-red-500 hover:opacity-70 transition-opacity"
                >
                    <span>Logout</span>
                    <LogOut size={16} strokeWidth={2} />
                </button>
            </nav>
        </div>
    );
}