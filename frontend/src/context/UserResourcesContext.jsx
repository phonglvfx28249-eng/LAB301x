import {createContext, useContext, useEffect, useState} from "react";
import springApi from "../api/api.js";
import {getUserWalletResource,getUserOrders,getTotalCoinsOwned,getTradeHistory} from "../api/resourcesApi.js";


const userResourcesContext = createContext(null);

export default function UserResourcesProvider({ children }) {
    const [wallet, setWallet] = useState(null);
    const [orders, setOrders] = useState(null);
    const [totalCoinsOwned, setTotalCoinsOwned] = useState(0);
    const [tradeHistory,setTradeHistory] = useState([]);

    const getWallet = async () => {
        const data = await getUserWalletResource();
        setWallet(data);
        return data;
    }

    const getTotalCoin = async () => {
        const data = await getTotalCoinsOwned();
        setTotalCoinsOwned(data);
        return data;
    }

    const getTrades =async () => {
        const data = await getTradeHistory();
        setTradeHistory(data);
        return data;
    }

    useEffect(() => {
        const intervalId = setInterval(async () => {
            try{
                const walletData = await getWallet();
                const totalCoinsData = await getTotalCoin();
                const tradesHistoryData = await getTrades();

            } catch (error) {
                console.error("Error fetching user resources:", error);
            }
        }, 2000); // Update every 5 seconds

        return () => clearInterval(intervalId);
    }, []);



    return (
        <userResourcesContext.Provider value={{ wallet, orders, totalCoinsOwned,tradeHistory }}>
            {children}
        </userResourcesContext.Provider>
    );
}

export function useUserResources() {
    const context = useContext(userResourcesContext);
    if (!context) {
        throw new Error("useUserResources must be used within a UserResourcesProvider");
    }
    return context;
}