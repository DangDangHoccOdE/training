package com.luvina.training_final.SpringBootProject.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertStringToDate {
    public static Date convert(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dateUtil ;
        dateUtil = sdf.parse(date);

        if (!dateUtil.before(new java.util.Date())) {
            throw new IllegalAccessException("Date of birth must be a day in the past");
        }

        return new Date(dateUtil.getTime());
    }

    public static LocalDateTime convertToLocalDateTime(String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        try{
            return  LocalDateTime.parse(date,dtf);
        }catch(Exception e){
            throw new RuntimeException(("Không thể convert string sang Date!"));
        }
    }
}
