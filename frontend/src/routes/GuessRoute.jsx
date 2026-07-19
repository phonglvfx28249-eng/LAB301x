import {useAuth} from "../context/AuthContext.jsx";
import {Navigate, Outlet, useNavigate} from "react-router-dom";

export default function GuessRoute() {
    const {user} = useAuth();
    if(user){
        if(user.role === "ADMIN"){
            return <Navigate to={"/admin/dashboard"} />;
        } else{
            return <Navigate to={"/user/dashboard"} />;
        }
    }
    return <Outlet />;
}