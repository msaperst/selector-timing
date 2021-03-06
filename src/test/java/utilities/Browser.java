package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

public class Browser {

    public enum BrowserName {
        FIREFOX, CHROME, INTERNETEXPLORER, EDGE, OPERA, SAFARI
    }

    private BrowserName name;
    private String version;
    private Platform platform;
    private DesiredCapabilities desiredCapabilities;

    public Browser(String name, String version, String platform) throws IOException {
        this.name = lookup(name);
        setupDesiredCapabilities();
        if (version != null) {
            this.version = version;
            this.desiredCapabilities.setVersion(this.version);
        }
        if (platform != null) {
            this.platform = Platform.fromString(platform);
            this.desiredCapabilities.setPlatform(this.platform);
        }
    }

    /**
     * allows the browser selected to be passed in with a case insensitive name
     *
     * @param b - the string name of the browser
     * @return utilities.Browser: the enum version of the browser
     */
    public static BrowserName lookup(String b) throws IOException {
        if ("IE".equalsIgnoreCase(b)) {
            return BrowserName.INTERNETEXPLORER;
        }
        if ("MS Edge".equalsIgnoreCase(b)) {
            return BrowserName.EDGE;
        }
        for (BrowserName browser : BrowserName.values()) {
            if (browser.name().equalsIgnoreCase(b)) {
                return browser;
            }
        }
        throw new IOException("Browser name doesn't map to any supported browser");
    }

    public WebDriver setupDriver(String buildName, String locator) throws MalformedURLException {
        WebDriver driver;
        if (Property.getProperty("hub") != null) {
            desiredCapabilities.setCapability("name", locator + " on " + getDetails());
            desiredCapabilities.setCapability("tags", Collections.singletonList(locator));
            desiredCapabilities.setCapability("build", buildName);
            driver = new RemoteWebDriver(new URL(Property.getProperty("hub")), this.desiredCapabilities);
        } else {
            driver = setupLocalDriver();
        }
        return driver;
    }

    public void setupDesiredCapabilities() {
        switch (name) {
            case FIREFOX:
                desiredCapabilities = DesiredCapabilities.firefox();
                break;
            case INTERNETEXPLORER:
                desiredCapabilities = DesiredCapabilities.internetExplorer();
                break;
            case EDGE:
                desiredCapabilities = DesiredCapabilities.edge();
                break;
            case SAFARI:
                desiredCapabilities = DesiredCapabilities.safari();
                break;
            case OPERA:
                desiredCapabilities = DesiredCapabilities.operaBlink();
                break;
            case CHROME:
            default:
                desiredCapabilities = DesiredCapabilities.chrome();
                break;
        }
    }

    /**
     * this creates the webdriver object, which will be used to interact with
     * for all browser web tests
     *
     * @return WebDriver: the driver to interact with for the test
     */
    public WebDriver setupLocalDriver() {
        WebDriver driver;
        // check the browser
        switch (name) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().forceCache().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions(desiredCapabilities);
                firefoxOptions.setHeadless(true);
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case INTERNETEXPLORER:
                WebDriverManager.iedriver().forceCache().setup();
                InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions(desiredCapabilities);
                driver = new InternetExplorerDriver(internetExplorerOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().forceCache().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions = edgeOptions.merge(desiredCapabilities);
                driver = new EdgeDriver(edgeOptions);
                break;
            case SAFARI:
                SafariOptions safariOptions = new SafariOptions(desiredCapabilities);
                driver = new SafariDriver(safariOptions);
                break;
            case OPERA:
                WebDriverManager.operadriver().forceCache().setup();
                OperaOptions operaOptions = new OperaOptions();
                operaOptions = operaOptions.merge(desiredCapabilities);
                driver = new OperaDriver(operaOptions);
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().forceCache().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions = chromeOptions.merge(desiredCapabilities);
                chromeOptions.setHeadless(true);
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        return driver;
    }

    /**
     * Retrieves a pretty formatted browser name, including version and platform. If headless or
     * screensizes are indicated, they are also displayed. If no browser is used, that will be
     * stated, and platform will be appended
     *
     * @return String: the friendly string of the device capabilities
     */
    public String getDetails() {
        StringBuilder stringBuilder = new StringBuilder(name.toString());
        if (version != null) {
            stringBuilder.append(" ").append(version);
        }
        if (platform != null) {
            String platformName = platform.getPartOfOsName()[0];
            if ("".equals(platformName)) {
                platformName = platform.toString().toLowerCase();
            }
            stringBuilder.append(" on ").append(platformName);
        }
        return stringBuilder.toString();
    }
}
