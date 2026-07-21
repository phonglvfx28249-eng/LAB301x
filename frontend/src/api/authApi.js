// src/api/authApi.js
import springApi from "./api.js";
import axios from "axios";

export const loginUser = async (email, password) => {
    const response = await springApi.post("/auth/login", { email, password });
    return response.data; // returns { token: "..." }
};

export const signupUser = async (userData) => {
    const response = await springApi.post("/auth/signup", userData);
    return response.data;
};

export const resetPassword = async (email) => {
    const response = await axios.post("http://localhost:8080/api/auth/reset-password", { email });
    return response.data;
}