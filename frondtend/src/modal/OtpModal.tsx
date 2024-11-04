import {Box, Button, Modal, TextField, Typography} from "@mui/material";
import { useForm, Controller } from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import React from "react";

interface OtpProps{
    open:boolean;
    onClose:()=>void;
    onSubmitOtp:(otp:string) => void;
}

const otpSchema = yup.object({
    otp:yup.string().required("Vui lòng nhập mã OTP").matches(/^\d{6}$/, "Mã OTP phải có 6 chữ số"),
});

const OtpModal:React.FC<OtpProps> = ({ open, onClose, onSubmitOtp }) => {

    const {
        handleSubmit,
        control,
        reset,
        formState:{errors},
    } = useForm<{otp:string}>({
        resolver:yupResolver(otpSchema)
    })

    const onSubmit = (data:{otp:string})=>{
        onSubmitOtp(data.otp);
        reset();
    }

    return(
        <Modal open={open} onClose={onClose}>
            <Box>
                <Typography variant="h6" component="h2" align="center">
                    Nhập mã OTP
                </Typography>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <Controller name="otp" control={control}
                        render={({field})=>(
                            <TextField
                                {...field}
                                fullWidth
                                label="OTP"
                                variant="outlined"
                                margin="normal"
                                error={!!errors.otp}
                                helperText={errors.otp ? errors.otp.message : ""}
                                inputProps={{maxLength: 6}}
                            />
                        )}
                    />
                    <Box display="flex" justifyContent="space-between" mt={2}>
                        <Button variant="contained" color="primary" type="submit">
                            Xác nhận
                        </Button>
                    </Box>
                </form>
            </Box>
        </Modal>
    )
}

export default OtpModal;