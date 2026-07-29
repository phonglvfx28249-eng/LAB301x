import React, { createContext, useState, useEffect } from "react";

const marketResourcesContext = createContext(null);

export default function marketResourcesProvider({children}) {

    const [buyOrders, setBuyOrders] = useState([]);
    const [sellOrders, setSellOrders] = useState([]);
    const [tradeHistory, setTradeHistory] = useState([]);
    const [maxPrice24H, setMaxPrice24H] = useState(0);
    const [minPrice24H, setMinPrice24H] = useState(0);
    const [volume24H, setVolume24H] = useState(0);



}