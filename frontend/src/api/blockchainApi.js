import springApi from "./api.js";

export const getAdminBlocks = async (page = 0, size = 6) => {
    const params = { page, size };
    const res = await springApi.get("/admin/blockchain/blocks", { params });
    return res.data;
};