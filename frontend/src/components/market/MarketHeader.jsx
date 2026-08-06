import React from "react";
import WindyLogo from "../common/windy-logo.jsx";

export default function MarketHeader({ maxPrice24H, minPrice24H, volume24H }) {
    return (
        <div className="flex items-center justify-between pb-4 border-b border-gray-700/50">
            <div className="flex items-center gap-3">
                <div className="font-extrabold tracking-tight text-brand text-emerald-600 text-stroke-1 text-stroke-color-black text-3xl flex items-center justify-center">
                    W
                </div>
                <div>
                    <p className="text-sm font-bold text-gray-900 leading-tight">W/USD</p>
                    <p className="text-xs text-gray-600 leading-tight">W COIN</p>
                </div>
            </div>

            <div className="flex items-center gap-10 text-sm">
                <div>
                    <p className="text-xs font-semibold text-gray-900">Max price 24h</p>
                    <p className="text-sm text-gray-800">{maxPrice24H} $</p>
                </div>
                <div>
                    <p className="text-xs font-semibold text-gray-900">Min price 24h</p>
                    <p className="text-sm text-gray-800">{minPrice24H} $</p>
                </div>
                <div>
                    <p className="text-xs font-semibold text-gray-900">Volume 24h</p>
                    <p className="text-sm text-gray-800">{volume24H}</p>
                </div>
            </div>

            <WindyLogo />
        </div>
    );
}