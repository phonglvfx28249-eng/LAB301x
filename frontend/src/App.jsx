import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import WindyLogo from './components/common/windy-logo.jsx';
import SignUpForm from "./pages/SignUp.jsx";
import SignInForm from "./pages/SignIn.jsx";
import {BrowserRouter} from "react-router-dom";
import AppRoutes from "./routes/AppRoutes.jsx";

function App() {
    return (
        <BrowserRouter>
            <AppRoutes />
        </BrowserRouter>
    );
}



export default App
