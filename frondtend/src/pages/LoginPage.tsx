import React,{ useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Box, Button, Container, Divider, Paper, TextField, Typography } from "@mui/material";
import axios from 'axios';

const LoginPage=()=>{
    const navigate = useNavigate();
    const {isLoggedIn} = useAuth();
    const [notice,setNotice] = useState("");
    const [isLoading,setIsLoading] = useState(false);

    const [formData,setFormData] = useState({
        email:'',
        password:'',
    })

    console.log("is" +isLoggedIn)
    useEffect(()=>{
        if(isLoggedIn){
            const redirectPath = localStorage.getItem('redirectPath') || "/";
            navigate(redirectPath,{replace:true});
        }
    },[isLoggedIn,navigate]);

    const handleSubmit = async(e:React.FormEvent)=>{
        e.preventDefault();

        setNotice("");
        setNoticeEmail("");
        setNoticePassword("");

        const emailValid = !checkEmailValid();
        const passwordValid = !checkPasswordValid();

        if(emailValid && passwordValid){
            setIsLoading(true);
            try{
                const url:string = "http://localhost:8080/api/user/login";
                const response = await axios.post(url,{
                    email:formData.email,
                    password:formData.password,
                }
                , {
                    headers: {
                        'Content-type': 'application/json',
                    },
                })
    
                if(response.status === 200){
                    setNotice("Đăng nhập thành công!");
                    setNotice("Vui lòng vào gmail để xác nhận tài khoản!");
                }
            }catch(error){
                console.log("Đăng nhập không thành công",error);
                const errorDetail = error.response.data.status;
                if(errorDetail === 403){
                    localStorage.setItem("email",formData.email);
                    setNotice("Đăng nhập thành công!"); // not activated

                    setTimeout(()=>{
                        navigate("/send_active_account");
                    },2000);

                }else if(errorDetail === 404){
                    setNotice("Tài khoản hoặc mật khẩu không chính xác!");
                }
            }finally{
                setIsLoading(false);
            }
        }
    }

    // email
    const [noticeEmail,setNoticeEmail] = useState("");
    const checkEmailValid = () =>{
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if(!emailRegex.test(formData.email)){
            setNoticeEmail("Email không hợp lệ!");
            return true;
        }else{
            setNoticeEmail("");
            return false;
        }
    }

    const [noticePassword,setNoticePassword] = useState("");
    const checkPasswordValid = () =>{
      if(formData.password.trim().length === 0){
            setNoticePassword("Mật khẩu không được bỏ trống!");
            return true;
        }
        else{
            setNoticePassword("");
            return false;
        }
    }

    const handleEnter = (e:React.KeyboardEvent<HTMLInputElement>)=>{
        if(e.key === 'Enter'){
            handleSubmit(e);
        }
    }

    const handleChange = (event)=>{
        const {name,value} = event.target;
        if(name ==="email"){
            setNoticeEmail("");
        }else{
            setNoticePassword("");
        }
        setFormData(prevData=>({
            ...prevData,
            [name]:value
        }))
    }

    const handleSignUp=()=>{
        navigate("/signUp");
        return;
    }

    return(
        <Container maxWidth="sm">
            <Typography variant="h3" sx={{fontWeight:'bold',mt:2}} component='h1' align="center" color="#1877F2">
                facebook
            </Typography>
            <Typography variant="h5" sx={{mt:2}} component='h3' align="center" color='black'>
            Facebook giúp bạn kết nối và chia sẻ với mọi người trong cuộc sống của bạn.
            </Typography>
            <Paper elevation={3} sx={{p:1,mt:1}}>
                <form onSubmit={handleSubmit}>
                    <TextField size="small"
                    fullWidth
                    name="email"
                    label="email"
                    variant="outlined"
                    value={formData.email}
                    onChange={handleChange}
                    onKeyDown={handleEnter}
                    sx={{mt:2}}
                    ></TextField>
                    {
                        noticeEmail &&
                        <Typography color="error" sx={{fontSize:'0.875rem',mt:1}}>
                            {noticeEmail}
                        </Typography>   
                    }
                   

                    <TextField size="small"
                    fullWidth
                    type="password"
                    name="password"
                    label="password"
                    variant="outlined"
                    value={formData.password}
                    onChange={handleChange}
                    onKeyDown={handleEnter}
                    sx={{mt:2}}
                    >
                    </TextField>
                    {
                        noticePassword &&
                        <Typography color="error" sx={{fontSize:'0.875rem',mt:1}}>
                            {noticePassword}
                        </Typography>   
                    }

                    {notice && (
                        <Typography variant="body2" color={notice.includes('Tài khoản') ? 'error' : 'success'} sx={{textAlign:'center'}}>
                            {notice}
                        </Typography>
                    )}
                    <Button
                    type='submit'
                    variant='contained'
                    fullWidth
                    disabled={isLoading}
                    sx={{
                        width: '50%',
                        bgcolor:"#1877F2",
                        textAlign:'center',
                        margin:'0 auto',
                        display:'block',
                        mt:2
                    }}
                    >
                         {isLoading ? "Đang xử lý" : "Đăng nhập"}
                    </Button>

                    <Box sx={{textAlign:'center',mt:1,mb:2}}>
                        <Link to="#" style={{textDecoration:'none'}}>
                            Quên mật khẩu?
                        </Link>
                    </Box>

                    <Divider sx={{mb:2}}/>

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        onClick={handleSignUp}
                        sx={{
                          width: '50%',
                          bgcolor:'#00a400',
                          textAlign:'center',
                          margin:' auto',
                          display:'block'
                        }}>
                        Tạo tài khoản
                    </Button>
                </form>
            </Paper>

            <Typography variant="subtitle1" sx={{mt:2}} component='h5' align="center" color='black'>
                Tạo Trang dành cho người nổi tiếng, thương hiệu hoặc doanh nghiệp.
            </Typography>
          
        </Container>
    )
}

export default LoginPage;