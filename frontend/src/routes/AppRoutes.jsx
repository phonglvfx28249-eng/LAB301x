import {Routes,Route} from "react-router-dom";
import SignInForm from "../pages/SignIn.jsx";
import SignUpForm from "../pages/SignUp.jsx";
import ResetPasswordForm from "../pages/ResetPassword.jsx";
import ProtectedUserRoute from "./ProtectedUserRoute.jsx";
import GuessRoute from "./GuessRoute.jsx";
import ProtectedAdminRoute from "./ProtectedAdminRoute.jsx";
import UserDashboard from "../pages/UserDashboard.jsx";
import AccountDashboard from "../pages/AccountDashboard.jsx";
import MarketDashboard from "../pages/MarketDashboard.jsx";



export default function AppRoutes({isAuthenticated}){

    return (
        <Routes>

            {/*User management*/}
            <Route element={<GuessRoute/>}>
                <Route path="/login" element={<SignInForm/>}/>
                <Route path="/register" element={<SignUpForm/>}/>
                <Route path = "/reset_password" element={<ResetPasswordForm/>}/>
                {/*    Route path alias for register*/}
                <Route path="/" element={<SignUpForm/>}/>
            </Route>


            {/*    User dashboard*/}
            {/*<Route path="/user" element={<ProtectedUserRoute/>}>*/}
                {/*dashboard page with protected routing*/}
                <Route path="/user/dashboard" element={<UserDashboard/>}/>
                <Route path="/user/account" element={<AccountDashboard/>}/>

            {/*</Route>*/}

            <Route path="/admin" element={<ProtectedAdminRoute/>}>
                <Route path="dashboard" element={<div>Admin Dashboard</div>}/>
            </Route>


            <Route path="/market" element={<MarketDashboard/>}/>

        </Routes>
    )

}