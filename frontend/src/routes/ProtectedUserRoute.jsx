import {Navigate, Outlet} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import UserResourcesProvider from "../context/UserResourcesContext.jsx";

export default function ProtectedUserRoute({ children }) {
    const { user } = useAuth();
    if(user){
        if(user.role === "USER"){

            return (
                <UserResourcesProvider>
                    <Outlet />
                </UserResourcesProvider>
            );
        } else {
            // Redirect to a different page if the user is not a "user"
            return <Navigate to="/unauthorized" />;
        }
    }
    return <Navigate to={"/login"} />;
}