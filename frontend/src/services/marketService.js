import {
    getChartByTime,
    getMaxPrice24H,
    getMinPrice24H,
    getSellOrderBook,
    getTradeHistory,
    getVolume24H
} from "../api/MarketResoucesApi.js";
import dayjs from 'dayjs';


export const getOrderBookFormatResponse = (data) => {
    return data.map((order) => {
        return {
            "price": order.price,
            "remaining_quantity": order.remaining_quantity,
            "total": order.price * order.remaining_quantity
        }
    });
}


export const getTradeHistoryFormatResponse = (data) => {
    return data.map((trade) => {
        return {
            "price": trade.price,
            "quantity": trade.quantity,
            "total": trade.price * trade.quantity,
            "side": trade.side
        }
    });
}

export const getChartByTimeFormatResponse = (data) => {
    return data.map((candle) => {
        return {
            "time": dayjs(candle.time).unix(),
            "open": candle.openPrice,
            "high": candle.highPrice,
            "low": candle.lowPrice,
            "close": candle.closePrice,
        }
    });
}

export const getMaxPrice24HFormatResponse = async () => {
    const data = await getMaxPrice24H();
    console.log(data);
}

export const getMinPrice24HFormatResponse = async () => {
    const data = await getMinPrice24H();
    console.log(data);
}

export const getVolume24HFormatResponse = async () => {
    const data = await getVolume24H();
    console.log(data);
}

