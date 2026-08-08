import springApi from "./api.js";

export const getAdminTrades = async (page = 0, size = 8) => {
    const params = { page, size };
    const res = await springApi.get("/admin/trades", { params });
    return res.data;
};