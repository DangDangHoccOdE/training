import { jwtDecode } from "jwt-decode";

interface JwtPayload{
    userId:number;
    isActive:boolean;
    isUser:boolean;
}

export function isTokenExpired(token:string){
    const decode = jwtDecode(token);
    if(!decode.exp){
        return false;
    }
    const currentTime = Date.now() / 1000;
    return currentTime > decode.exp;
}

export function getEmailByToken(){
    const dataToken = localStorage.getItem('accessToken');
    if(dataToken){
        return jwtDecode(dataToken).sub;
    }
}

export function getUserIdByToken(){
    const dataToken = localStorage.getItem('accessToken');
    if(dataToken){
        const userIdByToken = jwtDecode(dataToken) as JwtPayload;
        return userIdByToken.userId;
    }
}

export function isToken(){
    const token = localStorage.getItem("accessToken");
    if(token){
        return true;
    }
    return false;
}

export function logout(){
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}