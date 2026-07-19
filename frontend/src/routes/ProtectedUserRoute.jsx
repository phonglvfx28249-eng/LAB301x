import {Navigate, Outlet} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

export default function ProtectedUserRoute({ children }) {
    const { user } = useAuth();
    if(user){
        if(user.role === "USER"){
            return <Outlet />;
        } else {
            // Redirect to a different page if the user is not a "user"
            return <Navigate to="/unauthorized" />;
        }
    }
    return <Navigate to={"/login"} />;
}