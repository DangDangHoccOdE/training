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

            // Kiểm tra xem ngày có hợp lệ, là ngày trong quá khứ và người dùng phải lớn hơn 13 tuổi
            if (!isValidDate(dateUtil)) {
                throw new CustomException("The date of birth must be in the past and you must be older than 13 years old", HttpStatus.BAD_REQUEST);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Date is not valid!", HttpStatus.BAD_REQUEST);
        }

        return new Date(dateUtil.getTime()); // Trả về Date dạng java.sql.Date
    }

    // Hàm kiểm tra xem ngày có hợp lệ, là ngày trong quá khứ và người dùng phải lớn hơn 13 tuổi
    private static boolean isValidDate(java.util.Date date) {
        Calendar inputDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance(); // Lấy ngày hiện tại
        Calendar minAgeDate = Calendar.getInstance(); // Để kiểm tra tuổi tối thiểu

        inputDate.setTime(date);

        // Thiết lập ngày tối thiểu (13 năm trước ngày hiện tại)
        minAgeDate.add(Calendar.YEAR, -13);

        // Kiểm tra ngày không phải là ngày hiện tại hoặc ngày trong tương lai
        // và ngày phải trước ít nhất 13 năm so với hiện tại
        return inputDate.before(today) && inputDate.before(minAgeDate);
    }
}
