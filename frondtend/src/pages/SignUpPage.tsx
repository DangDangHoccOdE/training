import React, {useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Box, Button, Container, Divider, FormControl, FormControlLabel, IconButton, MenuItem, Paper, Radio, RadioGroup, Select, TextField, Typography } from "@mui/material";
import { Info } from 'lucide-react';
import axios from 'axios';
import { parse, isValid, isFuture, differenceInYears } from 'date-fns';

const SignUpPage=()=>{
    const {isLoggedIn} = useAuth();
    const navigate = useNavigate();
    const [notice,setNotice] = useState("");
    const [isLoading,setIsLoading] = useState(false);

    useEffect(()=>{
        if(isLoggedIn){
            navigate("/",{replace:true})
        }
    },[isLoggedIn,navigate])

    const [formData,setFormData] = useState({
        userId:0,
        firstName:'',
        lastName:'',
        day:'',
        month:'',
        year:'',
        gender:'',
        email:'',
        password:'',
        isActive:0,
    });

    const handleSubmit = async(e:React.FormEvent)=>{
        e.preventDefault();
        setNotice("");
        
        setNoticeDateOfBirth("");
        setNoticeEmail("");
        setNoticeGender("");
        setNoticePassword("");
        setNoticeName("");

        const isNameValid = ! checkValidName();
        const isDateOfBirthValid = ! checkValidDateOfBirth();
        const isPasswordValid = ! checkValidPassword();
        const isGenderValid = ! checkValidGender();
        const isEmailValid = !checkValidEmail() ;
        
        if(isDateOfBirthValid && isEmailValid && isGenderValid && isNameValid && isPasswordValid){
            try{
                setIsLoading(true);
                const url:string = "http://localhost:8080/api/user/register";
                const response = await axios.post(url, {
                    firstName: formData.firstName,
                    lastName: formData.lastName,
                    dateOfBirth: `${formData.day}/${formData.month}/${formData.year}`,
                    gender: formData.gender,
                    email: formData.email,
                    password: formData.password,
                }, {
                    headers: {
                        'Content-type': 'application/json',
                    },
                });

                if(response?.status === 200){
                    setNotice("Đăng ký tài khoản thành công");
                }

            }catch(error){
                if(error?.status === 409){
                    setNotice("Tài khoản email đã tồn tại!");
                }else{
                    setNotice("Đăng ký thất bại, đã xảy ra lỗi trong quá trình đăng ký tài khoản!")
                }
                console.log({error})
            }finally{
                setIsLoading(false);
            }
        }
    }

    // firstname
    const [noticeName,setNoticeName] = useState("");
    const checkValidName = () =>{
        if(formData.firstName.length > 20 || formData.lastName.length > 20){
            setNoticeName("Tên hoặc họ trên Facebook không được quá 20 ký tự!");
            return true;
        }else if(formData.firstName.trim() === '' || formData.lastName.trim() === '' ){
            setNoticeName("Tên hoặc họ trên Facebook không bỏ trống!");
            return true;
        }else{
            setNoticeName("");
            return false;
        }
    }

    // date of birth
    const [noticeDateOfBirth, setNoticeDateOfBirth] = useState("");

    const checkValidDateOfBirth = () => {
        const { day, month, year } = formData;

        if (!day || !month || !year) {
            setNoticeDateOfBirth("Vui lòng chọn ngày, tháng và năm sinh!");
            return true;
        }

        // Tạo đối tượng ngày sinh từ chuỗi ngày, tháng, năm
        const birthDate = parse(`${year}-${month}-${day}`, 'yyyy-MM-dd', new Date());

        // Kiểm tra ngày sinh hợp lệ
        if (!isValid(birthDate)) {
            setNoticeDateOfBirth("Ngày, tháng, năm không hợp lệ. Vui lòng nhập lại.");
            return true;
        }

        // Kiểm tra ngày sinh không phải là ngày trong tương lai
        if (isFuture(birthDate)) {
            setNoticeDateOfBirth("Hình như bạn đã nhập sai thông tin. Hãy nhớ dùng đúng ngày sinh nhật thật của mình nhé.");
            return true;
        }

        // Kiểm tra tuổi tối thiểu (ví dụ: 13 tuổi)
        const ageLimit = 13;
        if (differenceInYears(new Date(), birthDate) < ageLimit) {
            setNoticeDateOfBirth(`Bạn phải đủ ${ageLimit} tuổi để sử dụng dịch vụ.`);
            return true;
        }

        // Nếu hợp lệ, xóa thông báo lỗi
        setNoticeDateOfBirth("");
        return false;
    };

    // password
    const [noticePassword,setNoticePassword] = useState("");
    const checkValidPassword = () =>{
        const regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        if(!regexp.test(formData.password)){
            setNoticePassword("Mật khẩu phải chứa ít nhất 8 ký tự và có chứa 1 ký tự đặc biệt (@$!%*?&).");
            return true;
        }else if(formData.password.length>20){
            setNoticePassword("Mật khẩu không được vượt quá 20 ký tự!");
            return true;
        }
        else{
            setNoticePassword("");
            return false;
        }
    }

    // email
    const [noticeEmail,setNoticeEmail] = useState("");
    const checkValidEmail = () =>{
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        if(!emailRegex.test(formData.email)){
            setNoticeEmail("Email không hợp lệ!");
            return true;
        }else if(formData.email.length > 30){
            setNoticeEmail("Email không được vượt quá 30 ký tự!");
            return true;
        }else{
            setNoticeEmail("");
            return false;
        }
    }

    // gender 
    const [noticeGender,setNoticeGender] = useState("");
    const checkValidGender = () =>{
        if(formData.gender.trim() === ''
            || formData.gender.length === 0){
            setNoticeGender("Vui lòng chọn giới tính!");
            return true;
        }else{
            setNoticeGender("");
            return false;
        }
    }

    function formatNumber(num) {
        return num < 10 ? '0' + num : num.toString();
    }

    const handleChange = (event) => {
        const { name, value } = event.target;

        setNotice("");
        if(name === "firstName" || name==="lastName"){
            setNoticeName("");
        }else if(name === "gender"){
            setNoticeGender("");
        }else if(name==="email"){
            setNoticeEmail("");
        }else if(name==="password"){
            setNoticePassword("");
        }else{
            setNoticeDateOfBirth("");
        }

        setFormData((prevData) => ({
                ...prevData,
                [name]: value
        }));  
    };

    // arrays day, month, year
    const days = Array.from({length:31}, (_,i)=>formatNumber(i+1));
    const months = Array.from({length:12},(_,i) => formatNumber(i+1));
    const years = Array.from({length:100},(_,i)=>2024-i);

    return(
        <Container maxWidth="sm">
            <Typography variant="h3" sx={{fontWeight:'bold', mt:2}} component="h1" align="center"color="#1877F2">
                facebook
            </Typography>
            <Paper elevation={3} sx={{p:1,mt:1}}>
                <Typography variant="h5" sx={{fontWeight:'bold',mb:1}} component="h2" align="center" gutterBottom>
                    Tạo tài khoản mới
                </Typography>

                <Typography variant="subtitle1" align="center" sx={{mb:1}}>
                    Nhanh chóng và dễ dàng.
                </Typography>

                <Divider/>
                <form onSubmit={handleSubmit}>
                    <Box sx={{mb:2,mt:1}}>
                        <Box sx={{display:'flex',gap:2}}>
                            <TextField
                            error={!!noticeName}
                            size="small"
                            fullWidth
                            name="lastName"
                            label="Họ"
                            variant="outlined"
                            value={formData.lastName}
                            onChange={handleChange}
                            />
                            <TextField
                            error={!!noticeName}
                            size="small"
                            fullWidth
                            name="firstName"
                            label="Tên"
                            variant="outlined"
                            value={formData.firstName}
                            onChange={handleChange}
                            />
                        </Box>
                        {noticeName && (
                                <Typography color="error" sx={{ mt: 1, fontSize: '0.875rem' }}>
                                    {noticeName}
                                </Typography>
                            )}
                    </Box>
                   

                    <Box sx={{mb:2}}>
                        <Typography variant="subtitle2" sx={{display:"flex",alignItems:'center',mb:1,mr:1}}>
                            Ngày sinh
                            <IconButton size="small">
                                <Info size={16} />
                            </IconButton>
                        </Typography>

                        <Box sx={{display:'flex',gap:2}}>
                            <FormControl fullWidth size="small">
                                <Select name="day"
                                value={formData.day}
                                error={!!noticeDateOfBirth}
                                onChange={handleChange}
                                displayEmpty>
                                    <MenuItem value="">
                                    Ngày
                                    </MenuItem>
                                {days.map(day=>(
                                    <MenuItem key={day} value={day}>{day}</MenuItem>
                                ))}
                                </Select>
                            </FormControl>

                            <FormControl fullWidth size="small">
                                <Select name="month"
                                value={formData.month}
                                error={!!noticeDateOfBirth}
                                onChange={handleChange}
                                displayEmpty>
                                    <MenuItem value="">
                                        Tháng
                                    </MenuItem>
                                    {months.map(month=>(
                                        <MenuItem key={month} value={month}>{month}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            <FormControl fullWidth size="small">
                                    <Select name="year"
                                    value={formData.year}
                                    error={!!noticeDateOfBirth}
                                    onChange={handleChange}
                                    displayEmpty>
                                        <MenuItem value="">
                                            Năm
                                        </MenuItem>
                                        {years.map(year=>(
                                            <MenuItem key={year} value={year}>{year}</MenuItem>
                                        ))}
                                    </Select>
                            </FormControl>
                        </Box>
                        {noticeDateOfBirth && 
                            <Typography color="error">
                                {noticeDateOfBirth}
                            </Typography>
                        }
                    </Box>

                    <Box sx={{mb:2}}>
                        <Typography variant="subtitle2" sx={{display:'flex',alignItems:"center",mb:1,mr:1}}>
                            Giới tính
                            <IconButton size="small">
                                <Info size={16}/>
                            </IconButton>
                        </Typography>
                        {/* <FormControl error={!!noticeGender} variant="standard"> */}
                            <RadioGroup
                                row
                                name="gender"
                                value={formData.gender}
                                onChange={handleChange}>
                                    <FormControlLabel
                                    value="FEMALE"
                                    label="Nữ"
                                    control={<Radio size="small"/>}
                                    sx={{
                                        flex:1,
                                        border:'1px solid #ccc',
                                        borderRadius:1,
                                        m:0,
                                        mr:2
                                    }}/>
                                    <FormControlLabel
                                    value="MALE"
                                    control={<Radio size="small"/>}
                                    label="Nam"
                                    sx={{
                                        flex:1,
                                    border:'1px solid #ccc',
                                        borderRadius:1,
                                        m:0,
                                        mr:0
                                    }}
                                    />
                            </RadioGroup>
                        {/* </FormControl> */}
                        {noticeGender && 
                            <Typography color="error" sx={{mt:1,fontSize:'0.875rem'}}>
                                {noticeGender}
                            </Typography>
                        }           
                    </Box>

                   
                    <TextField fullWidth
                        label='Nhập email'
                        name='email'
                        variant="outlined"
                        error={!!noticeEmail}
                        value={formData.email}
                        onChange={handleChange}
                        size="small"
                    />
                    {noticeEmail && 
                            <Typography color="error" sx={{mt:1,fontSize:'0.875rem'}}>
                                {noticeEmail}
                            </Typography>
                    }
                
                    <TextField fullWidth type="password"
                        label='Mật khẩu mới'
                        name='password'
                        error={!!noticePassword}
                        variant="outlined"
                        value={formData.password}
                        onChange={handleChange} sx={{mt:2}}
                        size="small"
                    />
                    {noticePassword && 
                            <Typography color="error" sx={{mt:1,fontSize:'0.875rem'}}>
                                {noticePassword}
                            </Typography>
                     }

                    <Typography variant="caption" sx={{display:'block',mb:2,mt:2}}>
                        Những người dùng dịch vụ của chúng tôi có thể đã tải thông tin liên hệ của bạn lên Facebook.{' '}
                        <Link to="#" style={{ textDecoration: 'none'}}>Tìm hiểu thêm</Link>.
                    </Typography>

                    <Typography variant="caption" sx={{ display: 'block', mb: 3 }}>
                        Bằng cách nhấp vào Đăng ký, bạn đồng ý với{' '}
                        <Link to="#" style={{ textDecoration: 'none'}}>Điều khoản</Link>,{' '}
                        <Link to="#" style={{ textDecoration: 'none'}}>Chính sách quyền riêng tư</Link> và{' '}
                        <Link to="#" style={{ textDecoration: 'none'}}>Chính sách cookie</Link> của chúng tôi.
                        Bạn có thể nhận được thông báo của chúng tôi qua SMS và hủy nhận bất kỳ lúc nào.
                    </Typography>

                    {notice && (
                        <Typography variant="body2" color={notice.includes('thành công') ? 'success' : 'error'} sx={{textAlign:'center'}}>
                            {notice}
                        </Typography>
                    )}

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        disabled={isLoading}
                        sx={{
                          width: '50%',
                          bgcolor:'#1877F2',
                          textAlign:'center',
                          margin:'0 auto',
                          display:'block'
                        }}>
                            {isLoading ? "Đang xử lý" : "Đăng ký"}
                    </Button>
                
                    <Box sx={{textAlign:'center',mt:1}}>
                        <Link to="/login" style={{textDecoration:'none'}}>
                            Bạn đã có tài khoản rồi ư?
                        </Link>
                    </Box>
                </form>
            </Paper>
        </Container>
    )
}

export default SignUpPage;