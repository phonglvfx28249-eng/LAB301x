// src/api/authApi.js
import springApi from "./api.js";

export const loginUser = async (email, password) => {
    const response = await springApi.post("/auth/login", { email, password });
    return response.data; // returns { token: "..." }
};

export const signupUser = async (userData) => {
    const response = await springApi.post("/auth/signup", userData);
    return response.data;
};