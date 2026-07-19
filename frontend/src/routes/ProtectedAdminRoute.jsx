import {Navigate, Outlet} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

export default function ProtectedAdminRoute({ children }) {
    const { user } = useAuth();
    if(user){
        if(user.role === "ADMIN"){
            return <Outlet />;
        } else {
            // Redirect to a different page if the user is not an "admin"
            return <Navigate to="/unauthorized" />;
        }
    }
    return <Navigate to={"/login"} />;
}