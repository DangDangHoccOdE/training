import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios';
import { Box, Button, Container, Typography } from "@mui/material";

const SendActiveAccount = () => {
    const [isSending, setIsSending] = useState(false);
    const [notice, setNotice] = useState("");
    const navigate = useNavigate();

    const handleSendActive = async () => {
        const email = localStorage.getItem("email");
        setNotice("");
        setIsSending(true);
        try {
            const url:string = `http://localhost:8080/api/user/sendEmailActive`;
            const response = await axios.post(url, { email: email });

            if (response.status === 200) {                  
                setNotice("Đã gửi thành công!");
            }
        } catch (error) {
            console.log("Gửi xác thực thất bại", error);
            const errorDetail = error.response?.data?.status;
            if (errorDetail === 404) {
                setNotice("Không tìm thấy người dùng!");
            } else if (errorDetail === 409) {
                setNotice("Tài khoản của bạn đã được kích hoạt!");
            }
        } finally {
            setIsSending(false);
        }
    }

    return (
        <Container maxWidth="sm">
            <Box 
                display="flex" 
                flexDirection="column" 
                alignItems="center" 
                justifyContent="center" 
                height="100vh" 
                bgcolor="#f5f5f5"
                p={4}
                borderRadius={2}
                boxShadow={3}
                textAlign="center"
            >
                <img
                    src='https://cdn0.iconfinder.com/data/icons/shift-free/32/Error-512.png'
                    alt='fail'
                    width={100}
                />
                <Typography variant="h6" color={"error"} sx={{ mt: 2 }}>
                    Tài khoản của bạn chưa được kích hoạt. Vui lòng ấn gửi mã xác thực!
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button 
                        variant="contained" 
                        color="primary" 
                        sx={{ mt: 3 }} 
                        onClick={handleSendActive}
                        disabled={isSending} 
                    >
                        {!isSending ? "Gửi mã" : "Đang gửi mã"} 
                    </Button>
                    <Button 
                        variant="contained" 
                        color="error" 
                        sx={{ mt: 3 }} 
                        onClick={() => navigate("/login")}
                    >
                        Quay lại
                    </Button>
                </Box>
                <Typography variant="h6" color={notice.includes("thành công") ? "success" : "error"} sx={{ mt: 2 }}>
                    {notice}
                </Typography>
            </Box>
        </Container>
    );
}

export default SendActiveAccount;
