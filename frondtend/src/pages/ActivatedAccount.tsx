import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from 'axios';
import { Box, Button, Container, Typography } from "@mui/material";

const ValidOtp=()=>{
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    const email = query.get("email");
    const token = query.get("token");

    const [isActivated,setIsActivated] = useState(false);
    const navigate = useNavigate();
    const [notice,setNotice] = useState("");

    useEffect(()=>{
        const validToken = async()=>{
            try{
                const url:string = `http://localhost:8080/api/user/active_account`;
                const response = await axios.put(url,{
                    email:email,
                    token:token
                });

                if(response.status === 200){
                    setIsActivated(true);
                    setNotice("Kích hoạt tài khoản thành công. Vui lòng đăng nhập để tiếp tục sử dụng!");
                }
            }catch(error){
                console.log("Kích hoạt tài khoản không thành công",error);
                const errorDetail = error.response.data.status;
                if(errorDetail === 404){
                    setNotice("Không tìm thấy người dùng!");
                }else if(errorDetail === 400){
                    setNotice("Mã otp không chính xác hoặc bị hết hạn!");
                }else if(errorDetail === 409){
                    setNotice("Tài khoản của bạn đã được kích hoạt");
                }
                setIsActivated(false);
        }
    }
        validToken();
    },[email, token]);

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
            {isActivated ? (
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
            <Typography variant="h6" color={isActivated ? "primary" : "error"} sx={{ mt: 2 }}>
                {notice}
            </Typography>
        
                <Button 
                    variant="contained" 
                    color="primary" 
                    sx={{ mt: 3 }} 
                    onClick={() => navigate("/login")}
                >
                    Quay về trang đăng nhập
                </Button>
        </Box>
    </Container>
      
    )
}
export default ValidOtp;