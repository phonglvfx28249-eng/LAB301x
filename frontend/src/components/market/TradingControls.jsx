import React, { useState, useEffect } from "react";
import { Wallet, Lock } from "lucide-react";
import { sendOrder, positionOrder } from "../../api/OrderApi.js";

export default function TradingControls({
                                            token,
                                            wallet,
                                            marketPrice,
                                            activePosition,
                                            setActivePosition,
                                            refreshWallet,
                                            loading,
                                            setLoading
                                        }) {
    const [orderTab, setOrderTab] = useState("Limit"); // "Limit" | "Market"
    const [priceInput, setPriceInput] = useState("");
    const [amountInput, setAmountInput] = useState("");

    useEffect(() => {
        if (orderTab === "Market") {
            setPriceInput(marketPrice ? marketPrice.toString() : "");
        }
    }, [orderTab, marketPrice]);

    const handlePlaceOrder = async (side) => {
        if (!token) return;

        if (activePosition) {
            alert("You already have an active order/position. Please close it before placing a new one.");
            return;
        }

        const executionPrice = orderTab === "Market" ? marketPrice : parseFloat(priceInput);
        const executionQuantity = parseFloat(amountInput);

        if (!executionPrice || executionPrice <= 0) {
            alert("Please enter a valid price.");
            return;
        }

        if (!executionQuantity || executionQuantity <= 0) {
            alert("Please enter a valid quantity.");
            return;
        }

        const payload = {
            id: null,
            side: side,
            type: orderTab.toUpperCase(),
            price: executionPrice,
            quantity: executionQuantity
        };

        try {
            setLoading(true);
            await sendOrder(payload);
            setAmountInput("");
            if (orderTab === "Limit") setPriceInput("");

            const updatedPosition = await positionOrder();
            if (updatedPosition && (updatedPosition.orderId || updatedPosition.id)) {
                setActivePosition(updatedPosition);
            }
            if (refreshWallet) refreshWallet();
        } catch (err) {
            console.error("Order execution failed:", err);
            alert(err.response?.data || "Failed to place order.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            {/* Limit/Market tabs + Wallet info */}
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
                        <Wallet size={15} strokeWidth={2}/>
                        <span>
                            Amount: <span
                            className="font-medium">{token ? wallet?.available_balance ?? "0" : "0"}</span>
                        </span>
                    </div>
                    <div className="flex items-center gap-1.5">
                        <Lock size={15} strokeWidth={2}/>
                        <span>
                            Locked: <span className="font-medium">{token ? wallet?.locked_balance ?? "0" : "0"}</span>
                        </span>
                    </div>
                </div>
            </div>

            {/* Price / Amount inputs */}
            <div className="grid grid-cols-2 gap-4 mb-3">
                <div
                    className="flex items-center justify-between px-4 py-2.5 rounded-md border border-gray-700/50 bg-transparent">
                    <span className="text-sm text-gray-500">Price</span>
                    {orderTab === "Market" ? (
                        <span className="text-sm font-medium text-gray-900">{marketPrice}</span>
                    ) : (
                        <input
                            type="number"
                            placeholder={marketPrice?.toString() || "0.00"}
                            value={priceInput}
                            onChange={(e) => setPriceInput(e.target.value)}
                            className="text-right text-sm font-medium text-gray-900 bg-transparent outline-none w-full ml-2"
                        />
                    )}
                </div>
                <div
                    className="flex items-center justify-between px-4 py-2.5 rounded-md border border-gray-700/50 bg-transparent">
                    <span className="text-sm text-gray-500">Amount</span>
                    <div className="flex items-center gap-1">
                        <input
                            type="number"
                            placeholder="0.00"
                            value={amountInput}
                            onChange={(e) => setAmountInput(e.target.value)}
                            className="text-right text-sm font-medium text-gray-900 bg-transparent outline-none w-full"
                        />
                        <span className="text-sm font-medium text-gray-900">W</span>
                    </div>
                </div>
            </div>

            {/* Long / Short buttons */}
            <div className="grid grid-cols-2 gap-4 mb-6">
                <button
                    onClick={() => handlePlaceOrder("BUY")}
                    disabled={!token || loading || !!activePosition}
                    className="py-2.5 rounded-md bg-[#8FBF6B] hover:bg-[#7FAE5B] disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-semibold tracking-wide transition-colors"
                >
                    {!token ? "Login to Trade" : activePosition ? "Position Active" : "LONG"}
                </button>
                <button
                    onClick={() => handlePlaceOrder("SELL")}
                    disabled={!token || loading || !!activePosition}
                    className="py-2.5 rounded-md bg-[#E06A5C] hover:bg-[#D0594B] disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-semibold tracking-wide transition-colors"
                >
                    {!token ? "Login to Trade" : activePosition ? "Position Active" : "SHORT"}
                </button>
            </div>
        </div>
    );
}