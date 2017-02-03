package com.testframework.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.DocumentOutputStream;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class WriteToExcel {

    public static HSSFSheet CreateSpreadsheet(String strPath) {

        try {

            HSSFWorkbook wbWorkbook = new HSSFWorkbook();
            HSSFSheet shSheet = wbWorkbook.createSheet("Summary");

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            //creates cell styles
            HSSFFont fontB = wbWorkbook.createFont();
            fontB.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            HSSFCellStyle style1 = wbWorkbook.createCellStyle();
            HSSFCellStyle style2 = wbWorkbook.createCellStyle();
            HSSFCellStyle styleBorder = wbWorkbook.createCellStyle();
            HSSFCellStyle AlignCentre = wbWorkbook.createCellStyle();
            style1.setFont(fontB);
            style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // Need both to change cell colour
            style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            styleBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            AlignCentre.setFont(fontB);
            AlignCentre.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            //Increases the width of column B and C
            shSheet.setColumnWidth(1, 4000);
            shSheet.setColumnWidth(2, 4000);

            //Merges cells B2 with C2 and B8 with C8
            shSheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));//Merges cells B2 and C2 togther
            shSheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 2));//Merges cells B8 and C8 togther 

            String[] SummaryTable = {"SUMMARY", "Start Time", "End Time", "Total Test Steps", "Total Errors"};
            String BreakdownHeader = "BREAKDOWN";
            String[] BreakdownColumnNames = {"Message Code", "Occurrences"};
            int BreakdownCode = 1000;

            //Summary Table
            for (int i = 0; i < 5; i++) {
                HSSFRow row = shSheet.getRow(i + 1);
                if (row == null) {
                    row = shSheet.createRow(i + 1);
                }
                HSSFCell cell = row.getCell(1);
                if (cell == null) {
                    cell = row.createCell(1);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(SummaryTable[i]);
                cell.setCellStyle(styleBorder);
                if (i == 0) {
                    cell.setCellStyle(AlignCentre);
                }
            }
            //Summary Tabe contents
            for (int i = 1; i < 5; i++) {
                HSSFRow row = shSheet.getRow(i + 1);
                if (row == null) {
                    row = shSheet.createRow(i + 1);
                }
                HSSFCell cell = row.getCell(2);
                if (cell == null) {
                    cell = row.createCell(2);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                if (i == 1) {
                    cell.setCellValue(sdf.format(date));
                    cell.setCellStyle(styleBorder);
                }
                if (i == 2) {
                    cell.setCellValue(sdf.format(date));
                    cell.setCellStyle(styleBorder);
                }
                if (i == 3) {
                    cell.setCellFormula("SUM(C19,C25)");
                    cell.setCellStyle(styleBorder);
                }
                if (i == 4) {
                    cell.setCellFormula("SUM(C10,C11,C12,C13,C26)");
                    cell.setCellStyle(styleBorder);
                }
            }

            //Breakdown Table Title
            HSSFRow row = shSheet.getRow(7);
            if (row == null) {
                row = shSheet.createRow(7);
            }
            HSSFCell cell = row.getCell(1);
            if (cell == null) {
                cell = row.createCell(1);
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(BreakdownHeader);
            cell.setCellStyle(AlignCentre);

            //Breakdown Column Headers
            for (int i = 0; i < 2; i++) {
                row = shSheet.getRow(8);
                if (row == null) {
                    row = shSheet.createRow(8);
                }
                cell = row.getCell(i + 1);
                if (cell == null) {
                    cell = row.createCell(i + 1);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(BreakdownColumnNames[i]);
                cell.setCellStyle(AlignCentre);
            }

            //Breakdown Message Codes
            for (int i = 0; i < 17; i++) {
                row = shSheet.getRow(i + 9);
                if (row == null) {
                    row = shSheet.createRow(i + 9);
                }
                cell = row.getCell(1);
                if (cell == null) {
                    cell = row.createCell(1);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(BreakdownCode + i);
                if ((i % 2) == 0) {
                    cell.setCellStyle(style2);
                }
                cell = row.getCell(2);
                if (cell == null) {
                    cell = row.createCell(2);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(BreakdownCode + i);
                if ((i % 2) == 0) {
                    cell.setCellStyle(style2);
                }
            }

            HSSFSheet OutputSheet = wbWorkbook.getSheet("LogOutput");
            if (OutputSheet == null) {
                OutputSheet = wbWorkbook.createSheet("LogOutput");
            }

            OutputSheet.setColumnWidth(0, 40000);//Makes the first column wider

            FileOutputStream fileOut = new FileOutputStream(strPath);
            wbWorkbook.write(fileOut);
            fileOut.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static HSSFSheet UpdateSpreadsheet(int iTotalCountMsg, int[] iCountMsg, String strOutput, String strPath) {

        try {

            InputStream isInput = new FileInputStream(strPath);
            POIFSFileSystem fsFileSystem = new POIFSFileSystem(isInput);
            HSSFWorkbook wbWorkbook = new HSSFWorkbook(fsFileSystem);
            HSSFSheet shSheet = wbWorkbook.getSheet("Summary");
            if (shSheet == null) {
                shSheet = wbWorkbook.createSheet("Summary");
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            SimpleDateFormat StepTimeFormat = new SimpleDateFormat("HH:mm:ss");
            //Adds current date and time to End Date Field
            HSSFRow row = shSheet.getRow(3);
            if (row == null) {
                row = shSheet.createRow(3);
            }
            HSSFCell cell = row.getCell(2);
            if (cell == null) {
                cell = row.createCell(2);
            }
            cell.setCellValue(sdf.format(date));

            //Adds number of occurrences of ech msg code into the Breakdown table.
            for (int i = 0; i < 17; i++) {
                row = shSheet.getRow(i + 9);
                if (row == null) {
                    row = shSheet.createRow(i + 9);
                }
                cell = row.getCell(2);
                if (cell == null) {
                    cell = row.createCell(2);
                }
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(iCountMsg[i]);
            }

            //creates new sheet 
            HSSFSheet OutputSheet = wbWorkbook.getSheet("LogOutput");
            if (OutputSheet == null) {
                OutputSheet = wbWorkbook.createSheet("LogOutput");
            }

            //creating another cell style for highlighting errors
            HSSFCellStyle styleError = wbWorkbook.createCellStyle();
            styleError.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleError.setFillForegroundColor(HSSFColor.RED.index);

            //Adds the log output into the log tab
            row = OutputSheet.getRow(iTotalCountMsg);
            if (row == null) {
                row = OutputSheet.createRow(iTotalCountMsg);
            }
            cell = row.getCell(0);
            if (cell == null) {
                cell = row.createCell(0);
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(strOutput);
            if (strOutput.contains("ERROR")) {
                cell.setCellStyle(styleError); //Highlights the cell red if it contains the word "ERROR"
            }
            //adds date into next column
            cell = row.getCell(1);
            if (cell == null) {
                cell = row.createCell(1);
            }
            cell.setCellValue(StepTimeFormat.format(date));

            FileOutputStream fileOut = new FileOutputStream(strPath);
            wbWorkbook.write(fileOut);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
