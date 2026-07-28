import springApi from "./api.js";
import axios from "axios";


export  const getUserWalletResource = async () =>{
    const res = await springApi.get("/user/resources/wallet");
    return res.data;
}

export  const getUserTradeById = async (tradeId) =>{
    const res = await springApi.get(`/user/resources/trade/${tradeId}`);
    return res.data;
}

export const getUserTradePage = async (page) =>{
    const res = await springApi.get(`/user/resources/trade?page=${page}`);
    return res.data;
}

export const getUserOrders = async () =>{
    const res = await springApi.get("/user/resources/order");
    return res.data;
}


export const getTotalCoinsOwned = async () =>{
    const res = await springApi.get("/user/resources/trade/total_quantity");
    return res.data;
}

