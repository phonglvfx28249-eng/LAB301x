import { useState, useEffect } from "react";
import { positionOrder, closeOrder } from "../api/OrderApi.js";

export function useActivePosition(token, refreshWallet) {
    const [activePosition, setActivePosition] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!token) {
            setActivePosition(null);
            return;
        }

        const fetchPosition = async () => {
            try {
                const data = await positionOrder();
                if (data && (data.orderId || data.id)) {
                    setActivePosition(data);
                } else {
                    setActivePosition(null);
                }
            } catch (err) {
                setActivePosition(null);
            }
        };

        fetchPosition();
        const interval = setInterval(fetchPosition, 2000);
        return () => clearInterval(interval);
    }, [token]);

    const handleClosePosition = async () => {
        if (!activePosition) return;
        const orderIdToClose = activePosition.orderId || activePosition.id;

        try {
            setLoading(true);
            await closeOrder({ id: orderIdToClose });
            setActivePosition(null);
            if (refreshWallet) refreshWallet();
        } catch (err) {
            console.error("Failed to close position:", err);
            alert("Failed to close order.");
        } finally {
            setLoading(false);
        }
    };

    return { activePosition, setActivePosition, handleClosePosition, loading, setLoading };
}