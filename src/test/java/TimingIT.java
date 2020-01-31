import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utilities.Browser;
import utilities.Locator;
import utilities.Property;
import utilities.Recorder;

import java.io.IOException;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class TimingIT {

    Recorder recorder;
    WebDriver driver;

    @BeforeMethod
    public void setupSpreadsheet(Object[] dataProvider) throws IOException {
        Browser browser = (Browser) dataProvider[0];
        driver = browser.setupDriver();
        recorder = new Recorder(browser);
        recorder.setupColumn(dataProvider[1].toString());
    }

    @AfterMethod(alwaysRun = true)
    public void closeOutSpreadsheet(Object[] dataProvider) throws IOException {
        recorder.writeToSheet();
        driver.quit();
    }

    @DataProvider(name = "locators", parallel = false)
    public Object[][] locators() throws IOException {
        List<By> locators = Locator.getLocators();
        List<Browser> browsers = Property.getBrowsers();
        Object[][] testData = new Object[locators.size() * browsers.size()][];
        int counter = 0;
        for (Browser browser : browsers) {
            for (By locator : locators) {
                testData[counter] = new Object[]{browser, locator};
                counter++;
            }
        }
        return testData;
    }

    @Test(dataProvider = "locators")
    public void simpleTest(Browser browser, By locator) throws IOException {
        driver.get("file:///" + System.getProperty("user.dir") + "/public/index.html");
        long startTime = currentTimeMillis();
        driver.findElement(locator);
        long stopTime = currentTimeMillis();
        Reporter.log("Took " + (stopTime - startTime) + " milliseconds to locate element");
        recorder.recordData((stopTime - startTime));
    }
}
