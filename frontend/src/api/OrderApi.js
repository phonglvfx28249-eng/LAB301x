
import springApi from "./api.js";
import axios from "axios";


export const sendOrder = async (data) => {
    const res = await springApi.post("/order/send_order", data);
    return res.data;
};

export const closeOrder = async (data) => {
    const res = await springApi.post(`/order/close_order`, data);
    return res.data;
};

export const positionOrder = async () => {
    const res = await springApi("/order/get_order");
    return res.data;
}

