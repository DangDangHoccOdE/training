import React,{ Button, Container, Paper, Typography } from "@mui/material";
import { getEmailByToken, logout } from "../utils/JwtUtils";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const HomePage =()=>{
    const {isLoggedIn,setIsLoggedIn} = useAuth();
    const email = getEmailByToken();
    const navigate = useNavigate();

    useEffect(()=>{
        if(!isLoggedIn){
            navigate("/login",{replace:true});
        }

        const email = localStorage.getItem("email");
        if(email!==null){
            localStorage.removeItem("email");
        }
    })

    const handleLogout=()=>{
        logout();
        setIsLoggedIn(false);
    }

    return(
        <Container maxWidth='sm'>
            <Paper elevation={3} sx={{padding:4,marginTop:8}}>
                <Typography variant="h4" gutterBottom>
                     Chào mừng bạn đến với hệ thống!
                </Typography>
                <Typography variant="body1">
                    Bạn đã đăng nhập thành công với email:
                </Typography>
                <Typography variant="h6" color="primary" sx={{mt:2}}>
                    {email}
                </Typography>

                <Button
                type="submit"
                variant="contained"
                fullWidth sx={{mt:1,width:'50%'}}
                color='error'
                onClick={handleLogout}>
                    Đăng xuất
                </Button>
            </Paper>
        </Container>
    )
}

export default HomePage;