import {createContext, useContext, useEffect, useState} from "react";
import springApi from "../api/api.js";
import {loginUser,signupUser} from "../api/authApi.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [user, setUser] = useState(null); // Optional: Store the verified user profile data
    const [isLoading, setIsLoading] = useState(!!token); // Loading is true ONLY if a token exists locally

    useEffect(() => {
        const verifyToken = async () => {
            // Case 1: No token in localStorage -> App is ready, go straight to login
            if (!token) {
                setIsLoading(false);
                return;
            }

            // Case 2: A token string exists -> Let's check if it's real or fake with the backend
            try {
                // Fire a request to the secure endpoint.
                // Manual header addition ensures it carries the token even if interceptors haven't mounted yet.
                const response = await springApi.get("/auth/me", {
                    headers: { Authorization: `Bearer ${token}` }
                });

                // Backend sent back a 200 OK! Token is valid.
                setUser(response.data);
                setIsLoading(false);
            } catch (error) {
                // Backend sent back a 401/403! Token is expired or tampered with.
                console.error("Token verification failed. Wiping local storage session.", error);
                localStorage.removeItem("token");
                setToken(null);
                setUser(null);
                setIsLoading(false);
            }
        };

        verifyToken();
    }, [token]);



    const login = async (email, password) => {
        const  data  = await loginUser(email, password);
        console.log(data);
        localStorage.setItem("token", data.token);
        setToken(data.token);
    };

    const logout = () => {
        localStorage.removeItem("token");
        setToken(null);
    };

    return (
        <AuthContext.Provider value={{ token, login, logout, user }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}