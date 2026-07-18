import React from "react";
import { useForm } from "react-hook-form";
import WindyLogo from "../components/common/windy-logo.jsx";
import Button from "../components/common/Button.jsx";
import {Link} from "react-router-dom"

function GoogleIcon() {
    return (
        <svg width="18" height="18" viewBox="0 0 48 48">
            <path
                fill="#FFC107"
                d="M43.6 20.5H42V20H24v8h11.3c-1.6 4.7-6.1 8-11.3 8-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.8 1.1 8 3l5.7-5.7C34.6 6 29.6 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.7-.4-3.5z"
            />
            <path
                fill="#FF3D00"
                d="M6.3 14.7l6.6 4.8C14.6 15.9 19 13 24 13c3.1 0 5.8 1.1 8 3l5.7-5.7C34.6 6 29.6 4 24 4 16.3 4 9.6 8.3 6.3 14.7z"
            />
            <path
                fill="#4CAF50"
                d="M24 44c5.5 0 10.4-1.9 14.3-5.1l-6.6-5.6c-2 1.4-4.6 2.2-7.7 2.2-5.2 0-9.6-3.3-11.3-7.9l-6.6 5.1C9.5 39.6 16.2 44 24 44z"
            />
            <path
                fill="#1976D2"
                d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.3 4.3-4.2 5.7l6.6 5.6C41.4 36 44 30.6 44 24c0-1.3-.1-2.7-.4-3.5z"
            />
        </svg>
    );
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_REGEX = /^(0|\+84)[0-9]{9,10}$/;

export default function SignInForm() {
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm({ mode: "onBlur" });

    const onSubmit = async (data) => {


        console.log(data);
        // submit to API here, e.g. await api.signUp(data)
    };

    return (
        <div className="min-h-screen w-full flex items-center justify-center bg-[#EAE3D5] p-6">
            <div className="w-xs max-w-sm mx-auto">
                {/* Logo */}
                <div className="flex items-center justify-center mb-4">
                    <WindyLogo/>
                </div>


                {/* Card */}
                <div className="bg-[#EAE3D5] border border-gray-700/70 rounded-md px-8 py-8">
                    <h1 className="text-center font-serif text-2xl font-bold text-gray-900 mb-6">
                        Sign in
                    </h1>

                    <form
                        className="space-y-4"
                        onSubmit={handleSubmit(onSubmit)}
                        noValidate
                    >
                        <div>
                            <input
                                type="email"
                                placeholder="Email"
                                {...register("email", {
                                    required: "Email is required",
                                    pattern: {
                                        value: EMAIL_REGEX,
                                        message: "Enter a valid email address",
                                    },
                                })}
                                className={`w-full px-4 py-2.5 rounded-md border bg-transparent text-sm text-gray-800 placeholder-gray-600 focus:outline-none focus:ring-2 ${
                                    errors.email
                                        ? "border-red-500 focus:ring-red-500"
                                        : "border-gray-700/70 focus:ring-[#107980]"
                                }`}
                            />
                            {errors.email && (
                                <p className="text-xs text-red-600 mt-1 ml-3">
                                    {errors.email.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <input
                                type="password"
                                placeholder="Password"
                                {...register("password", {
                                    required: "Password is required",
                                    minLength: {
                                        value: 8,
                                        message: "Password must be at least 8 characters",
                                    },
                                })}
                                className={`w-full px-4 py-2.5 rounded-md border bg-transparent text-sm text-gray-800 placeholder-gray-600 focus:outline-none focus:ring-2 ${
                                    errors.password
                                        ? "border-red-500 focus:ring-red-500"
                                        : "border-gray-700/70 focus:ring-[#107980]"
                                }`}
                            />
                            {errors.password && (
                                <p className="text-xs text-red-600 mt-1 ml-3">
                                    {errors.password.message}
                                </p>
                            )}
                        </div>

                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            className="w-full py-2.5 rounded-md bg-[#107980] hover:bg-[#0d6469] disabled:opacity-60 disabled:cursor-not-allowed text-white text-sm font-medium transition-colors"
                        >
                            {isSubmitting ? "Đang xử lý..." : "Tiếp tục"}
                        </Button>
                    </form>

                    <div className="flex items-center gap-3 my-5">
                        <div className="flex-1 h-px bg-gray-700/50"/>
                        <span className="text-xs text-gray-700">or</span>
                        <div className="flex-1 h-px bg-gray-700/50"/>
                    </div>

                    <button
                        type="button"
                        className="w-full flex items-center justify-center gap-2 py-2.5 rounded-md border border-gray-700/70 bg-transparent text-sm text-gray-800 hover:bg-black/5 transition-colors"
                    >
                        <GoogleIcon/>
                        <span>Sign up with Google</span>
                    </button>

                    <div className="flex gap-3">

                        <p className="text-center text-xs text-gray-800 underline mt-5 cursor-pointer font-bold">
                             <Link to="/reset_password">Forgot password?</Link>
                        </p>

                        <p className="text-center text-xs text-gray-800 underline mt-5 cursor-pointer font-bold">
                            <Link to="/register">Doesn't have account?</Link>

                        </p>
                    </div>

                </div>
            </div>
        </div>
    );
}