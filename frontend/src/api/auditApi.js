import springApi from "./api.js";

export const getAdminAuditLogs = async (page = 0, size = 8, search = "") => {
    const params = {
        page,
        size,
        ...(search ? { search } : {}),
    };
    const res = await springApi.get("/admin/audit-logs", { params });
    return res.data;
};