import {Navigate, Outlet} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import MarketResourcesProvider from "../context/MarketResoucesContext.jsx";
import UserResourcesProvider from "../context/UserResourcesContext.jsx";

export default function MarketRoute({ children }) {
    return (
        <MarketResourcesProvider>
            <UserResourcesProvider>
                <Outlet />
            </UserResourcesProvider>
        </MarketResourcesProvider>
    );
}