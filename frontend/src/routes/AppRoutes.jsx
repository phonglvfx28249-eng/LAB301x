import {Routes,Route} from "react-router-dom";
import SignInForm from "../pages/SignIn.jsx";
import SignUpForm from "../pages/SignUp.jsx";
import ResetPasswordForm from "../pages/ResetPassword.jsx";


export default function AppRoutes({isAuthenticated}){

    return (
        <Routes>

            {/*User management*/}
            <Route path="/login" element={<SignInForm/>}/>
            <Route path="/register" element={<SignUpForm/>}/>
            <Route path = "/reset_password" element={<ResetPasswordForm/>}/>

            {/*    Route path alias for register*/}
            <Route path="/" element={<SignUpForm/>}/>

            {/*    User dashboard*/}
        </Routes>
    )

}