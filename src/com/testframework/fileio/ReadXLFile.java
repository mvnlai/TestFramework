package com.testframework.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ReadXLFile {

    public HSSFSheet LoadSpreadsheet(String strPath) {

        try {
            InputStream isInput = new FileInputStream(strPath);
            POIFSFileSystem fsFileSystem = new POIFSFileSystem(isInput);
            HSSFWorkbook wbWorkbook = new HSSFWorkbook(fsFileSystem);
            HSSFSheet shSheet = wbWorkbook.getSheetAt(0);
            return shSheet;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
