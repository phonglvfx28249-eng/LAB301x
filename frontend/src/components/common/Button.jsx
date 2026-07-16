function Button({ children, ...props }) {
    return (
        <button
            className="bg-[#107980] hover:bg-[#0d6469] text-white font-medium px-6 py-2 rounded-[10px] transition-colors"
            {...props}
        >
            {children}
        </button>
    );
}

export default Button;