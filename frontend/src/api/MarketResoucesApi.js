import springApi from "./api.js";


export const getBuyOrderBook = async () => {
    const res = await  springApi.get("/market/buyorderbook");
    return res.data;
}

export const getSellOrderBook = async () => {
    const res = await  springApi.get("/market/sellorderbook");
    return res.data;
}

export const getTradeHistory = async () => {
    const res = await  springApi.get("/market/tradehistory");
    return res.data;
}

export const getChartByTime = async (time) => {
    const res = await  springApi.get(`/market/chart?time=${time}`);
    return res.data;
}

export const getMaxPrice24H = async () => {
    const res = await  springApi.get("/market/maxprice24h");
    return res.data;
}

export const getMinPrice24H = async () => {
    const res = await  springApi.get("/market/minprice24h");
    return res.data;
}

export const getVolume24H = async () => {
    const res = await  springApi.get("/market/volume24h");
    return res.data;
}