import React, { createContext, useState, useEffect, useContext } from "react";
import {
    getBuyOrderBook,
    getSellOrderBook,
    getChartByTime,
    getMaxPrice24H,
    getMinPrice24H,
    getVolume24H,
    getTradeHistory,
    getMarketPrice
} from "../api/MarketResoucesApi.js";

const MarketResourcesContext = createContext(null);

export default function MarketResourcesProvider({ children }) {
    const [buyOrders, setBuyOrders] = useState([]);
    const [sellOrders, setSellOrders] = useState([]);
    const [tradeHistory, setTradeHistory] = useState([]);
    const [maxPrice24H, setMaxPrice24H] = useState(0);
    const [minPrice24H, setMinPrice24H] = useState(0);
    const [volume24H, setVolume24H] = useState(0);
    const [marketChartData, setMarketChartData] = useState([]);
    const [time, setTime] = useState("1H");
    const [marketPrice,setMarketPrice] = useState(0);

    useEffect(() => {
        // Fetch data immediately when mounted or when time interval changes
        const fetchMarketData = async () => {
            try {
                const [
                    buyOrdersData,
                    sellOrdersData,
                    tradeHistoryData,
                    maxPrice24HData,
                    minPrice24HData,
                    volume24HData,
                    marketChartDataData,
                    marketPriceData
                ] = await Promise.all([
                    getBuyOrderBook(),
                    getSellOrderBook(),
                    getTradeHistory(),
                    getMaxPrice24H(),
                    getMinPrice24H(),
                    getVolume24H(),
                    getChartByTime(time),
                    getMarketPrice(),
                ]);

                setBuyOrders(buyOrdersData);
                setSellOrders(sellOrdersData);
                setTradeHistory(tradeHistoryData);
                setMaxPrice24H(maxPrice24HData);
                setMinPrice24H(minPrice24HData);
                setVolume24H(volume24HData);
                setMarketChartData(marketChartDataData);
                setMarketPrice(marketPriceData);

            } catch (error) {
                console.error("Error fetching market resources:", error);
            }
        };

        fetchMarketData();

        // Poll every 2 seconds
        const intervalId = setInterval(fetchMarketData, 2000);

        return () => clearInterval(intervalId);
    }, [time]); // Refetch data whenever the time interval changes

    return (
        <MarketResourcesContext.Provider
            value={{
                buyOrders,
                sellOrders,
                tradeHistory,
                maxPrice24H,
                minPrice24H,
                volume24H,
                marketChartData,
                time,
                setTime,
                marketPrice
            }}
        >
            {children}
        </MarketResourcesContext.Provider>
    );
}

export function useMarketResources() {
    const context = useContext(MarketResourcesContext);
    if (!context) {
        throw new Error("useMarketResources must be used within a MarketResourcesProvider");
    }
    return context;
}