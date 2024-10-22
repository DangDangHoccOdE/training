package com.hoanghaidang.social_network.utils;

import com.hoanghaidang.social_network.dto.request.ReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelGenerator {
    public static ByteArrayInputStream generateExcel(ReportDto reportDto) throws IOException{
        try(
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream())
            {
            Sheet sheet = workbook.createSheet("Weekly report");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] header = {"Post Count","New Friend","New Likes","New Comments"};
            for (int i=0 ; i<header.length; i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Điền dữ liệu vào bảng
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(reportDto.getPostCount());
            dataRow.createCell(1).setCellValue(reportDto.getNewFriendCount());
            dataRow.createCell(2).setCellValue(reportDto.getNewLikesCount());
            dataRow.createCell(3).setCellValue(reportDto.getNewCommentsCount());

            // Viết dữ liệu vào file
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
