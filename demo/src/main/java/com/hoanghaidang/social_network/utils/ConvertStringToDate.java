package com.hoanghaidang.social_network.utils;

import com.hoanghaidang.social_network.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConvertStringToDate {
    public static Date convert(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Bỏ kiểm tra định dạng để tránh lỗi không cần thiết
        java.util.Date dateUtil;

        try {
            // Chuyển đổi chuỗi thành đối tượng Date
            dateUtil = sdf.parse(date);

            // Kiểm tra xem ngày có hợp lệ và là ngày trong quá khứ
            if (!isValidDate(dateUtil)) {
                throw new CustomException("The date of birth must be in the past", HttpStatus.BAD_REQUEST);
            }
        }catch (CustomException e){
            throw e;
        }
        catch (Exception e) {
            throw new CustomException("Date is not valid!", HttpStatus.BAD_REQUEST);
        }

        return new Date(dateUtil.getTime()); // Trả về Date dạng java.sql.Date
    }

    // Hàm kiểm tra xem ngày có hợp lệ và là ngày trong quá khứ
    private static boolean isValidDate(java.util.Date date) {
        Calendar inputDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance(); // Lấy ngày hiện tại

        inputDate.setTime(date);

        // Kiểm tra xem ngày có phải là ngày trong quá khứ không
        if (!inputDate.before(today)) { // Ngày không được là ngày hiện tại hoặc ngày trong tương lai
            return false;
        }

        return true; // Ngày hợp lệ và là ngày trong quá khứ
    }
}
