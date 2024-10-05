package com.luvina.training_final.Spring.boot.project.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ConvertStringToDate {
    public static Date convert(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dateUtil = null;
        dateUtil = sdf.parse(date);

        if (!dateUtil.before(new java.util.Date())) {
            throw new IllegalAccessException("Date of birth must be a day in the past");
        }

        return new Date(dateUtil.getTime());
    }
}
