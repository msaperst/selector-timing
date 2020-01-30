package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class Recorder {

    //utilities.Browser Details
    Browser browser;
    //Recorded Datafile
    File excelOutput = new File("reports.xlsx");
    //Workbook
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    int column = 0;

    public Recorder(Browser browser) throws IOException {
        this.browser = browser;
        // setup our excel file
        if (excelOutput.exists()) {
            FileInputStream file = new FileInputStream(excelOutput);
            workbook = new XSSFWorkbook(file);
        } else {
            workbook = new XSSFWorkbook();
        }
        // setup the worksheet
        if (workbook.getSheet(browser.getDetails()) == null) {
            sheet = workbook.createSheet(browser.getDetails());
            //setup our first row
            sheet.createRow(0);
            Row row = sheet.getRow(0);
            CellStyle style = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            style.setFont(boldFont);
            row.setRowStyle(style);
            //setup our second row
            sheet.createRow(1);
            row = sheet.getRow(1);
            style = workbook.createCellStyle();
            Font italicFont = workbook.createFont();
            italicFont.setItalic(true);
            style.setFont(italicFont);
            row.setRowStyle(style);
        } else {
            sheet = workbook.getSheet(browser.getDetails());
        }
    }

    public void setupColumn(String locator) {
        Row headingRow = sheet.getRow(0);
        Iterator<Cell> cellIterator = headingRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (locator.equals(cell.getStringCellValue())) {
                break;
            }
            column++;
        }
        if (sheet.getRow(0).getCell(column) == null) {
            sheet.getRow(0).createCell(column).setCellValue(locator);
        }
        if (sheet.getRow(1).getCell(column) == null) {
            sheet.getRow(1).createCell(column).setCellFormula("AVERAGE(" + getExcelColumnName(column + 1) + "3:" + getExcelColumnName(column + 1) + "999)");
        }
    }

    public void recordData(long timeTook) {
        int row = 2;
        while (true) {
            Row iteratedRow = sheet.getRow(row);
            if (iteratedRow == null || iteratedRow.getCell(column) == null) {
                break;
            }
            row++;
        }
        if (sheet.getRow(row) == null) {
            sheet.createRow(row);
        }
        sheet.getRow(row).createCell(column).setCellValue(timeTook);
    }

    public void writeToSheet() throws IOException {
        //Write the workbook in file system
        FileOutputStream out = new FileOutputStream(excelOutput);
        workbook.write(out);
        out.close();
    }

    String getExcelColumnName(int number) {
        final StringBuilder sb = new StringBuilder();

        int num = number - 1;
        while (num >= 0) {
            int numChar = (num % 26) + 65;
            sb.append((char) numChar);
            num = (num / 26) - 1;
        }
        return sb.reverse().toString();
    }
}
