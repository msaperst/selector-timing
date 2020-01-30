import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class Recorder {

    //Browser Details
    DesiredCapabilities desiredCapabilities;
    //Recorded Datafile
    File excelOutput = new File("reports.xlsx");
    //Workbook
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    int column = 0;

    public Recorder(DesiredCapabilities desiredCapabilities) throws IOException {
        this.desiredCapabilities = desiredCapabilities;
        // setup our excel file
        if (excelOutput.exists()) {
            FileInputStream file = new FileInputStream(excelOutput);
            workbook = new XSSFWorkbook(file);
        } else {
            workbook = new XSSFWorkbook();
        }
        // setup the worksheet
        if (workbook.getSheet(getDetails()) == null) {
            sheet = workbook.createSheet(getDetails());
            sheet.createRow(0);
            Row row = sheet.getRow(0);
            CellStyle style = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            style.setFont(boldFont);
            row.setRowStyle(style);
        } else {
            sheet = workbook.getSheet(getDetails());
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
    }

    public void recordData(long timeTook) {
        int row = 1;
        while( true ) {
            Row iteratedRow = sheet.getRow(row);
            if (iteratedRow == null || iteratedRow.getCell(column) == null) {
                break;
            }
            row++;
        }
        if( sheet.getRow(row) == null) {
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


    /**
     * Retrieves a pretty formatted browser name, including version and platform. If headless or
     * screensizes are indicated, they are also displayed. If no browser is used, that will be
     * stated, and platform will be appended
     *
     * @return String: the friendly string of the device capabilities
     */
    public String getDetails() {
        StringBuilder stringBuilder = new StringBuilder(desiredCapabilities.getBrowserName());
        if (desiredCapabilities.getVersion() != null) {
            stringBuilder.append(" ").append(desiredCapabilities.getVersion());
        }
        if (desiredCapabilities.getPlatform() != null) {
            String platformName = desiredCapabilities.getPlatform().getPartOfOsName()[0];
            if ("".equals(platformName)) {
                platformName = desiredCapabilities.getPlatform().toString().toLowerCase();
            }
            stringBuilder.append(" on ").append(platformName);
        }
        return stringBuilder.toString();
    }
}
