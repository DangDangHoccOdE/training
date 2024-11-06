import React,{ createContext, useContext, useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import { isToken, isTokenExpired, logout } from "../utils/JwtUtils";
import { toast } from "react-toastify";

interface AuthContextType{
    isLoggedIn:boolean;
    setIsLoggedIn:any;
}

interface AUthContextProps{
    children:React.ReactNode;
}

const AuthContext = createContext<AuthContextType|undefined>(undefined)

export const AuthProvider:React.FC<AUthContextProps>=(props)=>{
    const [isLoggedIn,setIsLoggedIn] = useState(isToken());
    const navigate = useNavigate();

    useEffect(()=>{
        const checkRefreshToken = async()=>{
            const refreshToken = localStorage.getItem("refreshToken");

            if(refreshToken && isTokenExpired(refreshToken)){
                toast.warning("Phiên làm việc đã hết, vui lòng đăng nhập lại!");
                logout();
                setIsLoggedIn(false);
                navigate("/login");
                return;
            }
        }
        checkRefreshToken();
    },[navigate]);

    return(
        <AuthContext.Provider value={{isLoggedIn,setIsLoggedIn}}>
            {props.children}
        </AuthContext.Provider>
    )
}

export const useAuth=():AuthContextType=>{
    const context = useContext(AuthContext);
    if(!context){
        throw new Error("Lỗi context");
    }
    return context;
}
