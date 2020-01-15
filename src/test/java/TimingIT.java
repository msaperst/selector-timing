import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.lang.System.currentTimeMillis;

public class TimingIT {

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
    public void simpleTest(By locator) {
        WebDriverManager.chromedriver().forceCache().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("file:///home/max/workspace/selector-timing/public/index.html");
        long startTime = currentTimeMillis();
        driver.findElement(locator);
        long stopTime = currentTimeMillis();
        Reporter.log("Took " + (stopTime - startTime) + " milliseconds to locate element");
        driver.quit();
    }
}
