import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from 'axios';
import { useAuth } from "../context/AuthContext";
import { Box, Button, Container, Typography } from "@mui/material";

const ValidOtp=()=>{
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    const email = query.get("email");
    const otp = query.get("otp");

    const [isLoading,setIsLoading] = useState(false);
    const navigate = useNavigate();
    const {setIsLoggedIn} = useAuth();
    const [notice,setNotice] = useState("");

    useEffect(()=>{
        const validOtp = async()=>{
            try{
                const url:string = `http://localhost:8080/api/user/validate_otp`;
                const response = await axios.post(url,{
                    email:email,
                    otp:otp
                });

                if(response.status === 200){
                    const { accessToken, refreshToken } = response.data.data;
                    localStorage.setItem("accessToken", accessToken);
                    localStorage.setItem("refreshToken", refreshToken);
                    setIsLoading(true);
                    setIsLoggedIn(true);
                    setNotice("Xác thực thành công!");

                //    Delay navigation to ensure UI updates
                    setTimeout(() => {
                        const redirectPath = localStorage.getItem("redirectPath") || "/";
                        localStorage.removeItem("redirectPath");
                        navigate(redirectPath, { replace: true });
                    }, 5000); 
                }
            }catch(error){
                console.log("Đăng nhập không thành công",error);
                const errorDetail = error.response.data.status;
                if(errorDetail === 404){
                    setNotice("Không tìm thấy người dùng!");
                }else if(errorDetail === 400){
                    setNotice("Mã otp không chính xác hoặc bị hết hạn, vui lòng đăng nhập lại!");
                }
                setIsLoading(false);

            }
        }

        validOtp();
    },[email, navigate, otp, setIsLoggedIn])

    return(
        <Container maxWidth="sm">
        <Box 
            display="flex" 
            flexDirection="column" 
            alignItems="center" 
            justifyContent="center" 
            height="100vh" 
            bgcolor="#f9f9f9"
            p={4}
            borderRadius={2}
            boxShadow={3}
            textAlign="center"
        >
            {isLoading ? (
                <img
                    src='https://cdn0.fahasa.com/skin/frontend/base/default/images/order_status/ico_successV2.svg?q=10311'
                    alt='success'
                    width={100}
                />
            ) : (
                <img
                    src='https://cdn0.iconfinder.com/data/icons/shift-free/32/Error-512.png'
                    alt='fail'
                    width={100}
                />
            )}
            <Typography variant="h6" color={isLoading ? "primary" : "error"} sx={{ mt: 2 }}>
                {notice}
            </Typography>
            {!isLoading && (
                <Button 
                    variant="contained" 
                    color="primary" 
                    sx={{ mt: 3 }} 
                    onClick={() => navigate("/login")}
                >
                    Quay về trang đăng nhập
                </Button>
            )}
        </Box>
    </Container>
      
    )
}
export default ValidOtp;