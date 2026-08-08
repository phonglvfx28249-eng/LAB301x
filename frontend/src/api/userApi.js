import springApi from "./api.js";

export const getAdminUsers = async (page = 0, size = 5, search = "") => {
    const params = {
        page,
        size,
        ...(search ? { search } : {}),
    };
    const res = await springApi.get("/admin/users", { params });
    return res.data;
};

export const deleteUser = async (id) => {
    const res = await springApi.delete(`/admin/users/${id}`);
    return res.data;
};