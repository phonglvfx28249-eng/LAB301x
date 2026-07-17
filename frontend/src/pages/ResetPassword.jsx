import React from "react";
import { useForm } from "react-hook-form";
import Button from "../components/common/Button.jsx";
import WindyLogo from "../components/common/windy-logo.jsx";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_REGEX = /^(0|\+84)[0-9]{9,10}$/;

export default function ResetPasswordForm() {
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm({ mode: "onBlur" });

    const onSubmit = async (data) => {
        console.log(data);
        // submit to API here, e.g. await api.requestPasswordReset(data)
    };

    return (
        <div className="min-h-screen w-full flex items-center justify-center bg-[#EAE3D5] p-6">
            <div className="w-full max-w-sm">
                {/* Logo */}
                <div className="flex items-center justify-center mb-4">
                    <WindyLogo/>
                </div>

                {/* Card */}
                <div className="bg-[#EAE3D5] border border-gray-700/70 rounded-md px-8 py-8">
                    <h1 className="text-center font-serif text-2xl font-bold text-gray-900 mb-6">
                        Reset password
                    </h1>

                    <form
                        className="space-y-4"
                        onSubmit={handleSubmit(onSubmit)}
                        noValidate
                    >
                        <div>
                            <input
                                type="text"
                                placeholder="Email / sdt"
                                {...register("emailOrPhone", {
                                    required: "Email or phone number is required",
                                    validate: (value) =>
                                        EMAIL_REGEX.test(value) || PHONE_REGEX.test(value)
                                            ? true
                                            : "Enter a valid email or phone number",
                                })}
                                className={`w-full px-4 py-2.5 rounded-md border bg-transparent text-sm text-gray-800 placeholder-gray-600 focus:outline-none focus:ring-2 ${
                                    errors.emailOrPhone
                                        ? "border-red-500 focus:ring-red-500"
                                        : "border-gray-700/70 focus:ring-[#107980]"
                                }`}
                            />
                            {errors.emailOrPhone && (
                                <p className="text-xs text-red-600 mt-1 ml-3">
                                    {errors.emailOrPhone.message}
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
                </div>
            </div>
        </div>
    );
}