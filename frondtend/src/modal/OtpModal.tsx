import { Box, Button, Modal, TextField, Typography } from "@mui/material";
import {useForm, Controller, useWatch} from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import React, {useEffect, useState} from "react";
import axios from "axios";
import {useAuth} from "../context/AuthContext";
import {useNavigate} from "react-router-dom";

interface OtpProps {
    open: boolean;
    onClose: () => void;
}

const otpSchema = yup.object({
    otp: yup.string().required("Vui lòng nhập mã OTP").matches(/^\d{6}$/, "Mã OTP phải có 6 chữ số"),
});

const OtpModal: React.FC<OtpProps> = ({ open, onClose }) => {
    const {setIsLoggedIn} = useAuth();
    const [otpNotice, setOtpNotice] = useState(""); // State để quản lý otpNotice
    const navigate = useNavigate();

    const {
        handleSubmit,
        control,
        reset,
        formState: { errors }
    } = useForm<{ otp: string }>({
        resolver: yupResolver(otpSchema)
    });

    const otpValue = useWatch({
        control,
        name: "otp"
    });

    useEffect(() => {
        if (otpValue) {
            setOtpNotice("");
        }
    }, [otpValue]);

    const onSubmit=async(data:{otp:string})=>{
        const otp:string = data?.otp;
        try {
            const email = localStorage?.getItem("email");
            const response = await axios.post("http://localhost:8080/api/user/validate_otp", {
                email,
                otp,
            });
            if(response.status === 200) {
                const {accessToken, refreshToken} = response?.data?.data;
                localStorage.setItem("accessToken", accessToken);
                localStorage.setItem("refreshToken", refreshToken);
                setIsLoggedIn(true);
                setOtpNotice("");
                reset();

                setTimeout(() => {
                    const redirectPath = localStorage.getItem("redirectPath") || "/";
                    localStorage.removeItem("redirectPath");
                    navigate(redirectPath, {replace: true});
                }, 2000);
            }
        } catch (error) {
            setOtpNotice("OTP không chính xác. Vui lòng thử lại.");
        }
    }

    const handleClose = () => {
        setOtpNotice("");
        reset();
        onClose();
    };

    return (
        <Modal open={open} onClose={handleClose}>
            <Box
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    width: 400,
                    bgcolor: "background.paper",
                    borderRadius: 2,
                    boxShadow: 24,
                    p: 4,
                    textAlign: "center"
                }}
            >
                <Typography variant="h6" component="h2" gutterBottom>
                    Nhập mã OTP
                </Typography>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <Controller
                        name="otp"
                        control={control}
                        render={({ field }) => (
                            <TextField
                                {...field}
                                fullWidth
                                label="OTP"
                                variant="outlined"
                                margin="normal"
                                error={!!errors.otp}
                                helperText={errors.otp ? errors.otp.message : ""}
                            />
                        )}
                    />
                    {otpNotice && (
                        <Typography color={otpNotice.includes("không") ? "error" : "success"} sx={{ mt: 2 }}>
                            {otpNotice}
                        </Typography>
                    )}
                    <Box display="flex" justifyContent="center" mt={2}>
                        <Button variant="contained" color="primary" type="submit">
                            Xác nhận
                        </Button>
                        <Button onClick={handleClose} sx={{ ml: 2 }} color="secondary">
                            Đóng
                        </Button>
                    </Box>
                </form>
            </Box>
        </Modal>
    );
};

export default OtpModal;
