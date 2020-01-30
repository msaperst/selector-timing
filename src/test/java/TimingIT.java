import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class TimingIT {

    Recorder recorder;
    WebDriver driver;

    @BeforeMethod
    public void setupSpreadsheet(Object[] dataProvider) throws IOException {
        Browser browser = new Browser("firefox");
        driver = browser.setupDriver();
        recorder = new Recorder(browser);
        recorder.setupColumn(dataProvider[0].toString());
    }

    @AfterMethod(alwaysRun = true)
    public void closeOutSpreadsheet(Object[] dataProvider) throws IOException {
        recorder.writeToSheet();
        driver.quit();
    }

    @DataProvider(name = "locators", parallel = false)
    public Object[][] locators() {
        return new Object[][]{
                new Object[]{By.id("myAnchorID")},
                new Object[]{By.name("myAnchorName")},
                new Object[]{By.className("link")},
                new Object[]{By.cssSelector("a")},
                new Object[]{By.cssSelector("a.link")},
                new Object[]{By.cssSelector("a#myAnchorID")},
                new Object[]{By.cssSelector("a[name='myAnchorName']")},
                new Object[]{By.cssSelector("a[href='http://www.coveros.com']")},
                new Object[]{By.linkText("Coveros Website")},
                new Object[]{By.partialLinkText("Coveros Website")},
                new Object[]{By.partialLinkText("Coveros")},
                new Object[]{By.partialLinkText("Website")},
                new Object[]{By.partialLinkText("ros Web")},
                new Object[]{By.tagName("a")},
                new Object[]{By.xpath("//html/body/a")},
                new Object[]{By.xpath("//a")},
                new Object[]{By.xpath("//a[@name='myAnchorName']")},
                new Object[]{By.xpath("//a[@href='http://www.coveros.com']")},
                new Object[]{By.xpath("//a[text()='Coveros Website']")},
        };
    }

    @Test(dataProvider = "locators")
    public void simpleTest(By locator) throws IOException {
        driver.get("file:///" + System.getProperty("user.dir") + "/public/index.html");
        long startTime = currentTimeMillis();
        driver.findElement(locator);
        long stopTime = currentTimeMillis();
        Reporter.log("Took " + (stopTime - startTime) + " milliseconds to locate element");
        recorder.recordData((stopTime - startTime));
    }
}
