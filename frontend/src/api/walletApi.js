import springApi from "./api.js";

export const getAdminWallets = async (page = 0, size = 5, search = "") => {
    const params = {
        page,
        size,
        ...(search ? { search } : {}),
    };
    const res = await springApi.get("/admin/wallets", { params });
    return res.data;
};