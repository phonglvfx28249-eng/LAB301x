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
import HistoryDashboard from "../pages/HistoryDashboard.jsx";
import UserResourcesProvider from "../context/UserResourcesContext.jsx";
import MarketRoute from "./MarketRoute.jsx";
import AdminLayout from "../pages/admin/AdminLayout.jsx";
import UserManagement from "../pages/admin/UserManagement.jsx";
import WalletManagement from "../pages/admin/WalletManagement.jsx";
import AuditLogManagement from "../pages/admin/AuditLogManagement.jsx";
import BlockchainManagement from "../pages/admin/BlockchainManagement.jsx";
import TradeManagement from "../pages/admin/TradeManagement.jsx";




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
            <Route path="/user" element={<ProtectedUserRoute/>}>
                {/*dashboard page with protected routing*/}
                    <Route path="/user/dashboard" element={<UserDashboard/>}/>
                    <Route path="/user/account" element={<AccountDashboard/>}/>
                    <Route path="/user/history" element={<HistoryDashboard/>}/>

            </Route>

            <Route path="/admin" element={<ProtectedAdminRoute/>}>
                    <Route element={<AdminLayout />}>
                        <Route path="users" element={<UserManagement />} />
                        <Route path="wallets" element={<WalletManagement />} />
                        <Route path="audit-logs" element={<AuditLogManagement />} />
                        <Route path="blockchain" element={<BlockchainManagement />} />
                        <Route path="trades" element={<TradeManagement />} />
                    </Route>
            </Route>

            <Route element={<MarketRoute/>}>
                <Route path="/market" element={<MarketDashboard/>}/>
            </Route>

        </Routes>
    )

}