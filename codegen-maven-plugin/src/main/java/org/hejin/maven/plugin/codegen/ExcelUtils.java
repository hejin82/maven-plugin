package org.hejin.maven.plugin.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    public static Workbook open(String fileName) throws IOException {

        InputStream excelFile = new FileInputStream(new File(fileName));
        Workbook workbook = new XSSFWorkbook(excelFile);
        return workbook;
    }
    
}
