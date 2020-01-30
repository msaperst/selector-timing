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
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Browser {

    public enum BrowserName {
        FIREFOX, CHROME, INTERNETEXPLORER, EDGE, OPERA, SAFARI
    }

    public static final String NAME_INPUT = "name";
    public static final String VERSION_INPUT = "version";
    public static final String PLATFORM_INPUT = "platform";

    private String browserInput;
    private BrowserName name;
    private String version;
    private Platform platform;
    private DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

    public Browser(String browserInput) throws IOException {
        this.browserInput = browserInput;
        if (!areBrowserDetailsSet()) {
            this.name = lookup(browserInput);
        } else {
            Map<String, String> browserDetails = parseMap();
            if (!browserDetails.containsKey(NAME_INPUT)) {
                throw new IOException("name must be included in browser details");
            }
            this.name = lookup(browserDetails.get(NAME_INPUT));
            if (browserDetails.containsKey(VERSION_INPUT)) {
                this.version = browserDetails.get(VERSION_INPUT);
                desiredCapabilities.setVersion(this.version);
            }
            if (browserDetails.containsKey(PLATFORM_INPUT)) {
                this.platform = Platform.fromString(browserDetails.get(PLATFORM_INPUT));
                desiredCapabilities.setPlatform(this.platform);
            }
        }
    }

    /**
     * allows the browser selected to be passed in with a case insensitive name
     *
     * @param b - the string name of the browser
     * @return Browser: the enum version of the browser
     */
    public static BrowserName lookup(String b) {
        if ("IE".equalsIgnoreCase(b)) {
            return BrowserName.INTERNETEXPLORER;
        }
        for (BrowserName browser : BrowserName.values()) {
            if (browser.name().equalsIgnoreCase(b)) {
                return browser;
            }
        }
        return BrowserName.CHROME;
    }

    /**
     * this creates the webdriver object, which will be used to interact with
     * for all browser web tests
     *
     * @return WebDriver: the driver to interact with for the test
     */
    public WebDriver setupDriver() {
        WebDriver driver;
        // check the browser
        switch (name) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().forceCache().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions(desiredCapabilities);
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
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        return driver;
    }

    /**
     * determines if the browser information provided has details, or just the
     * browser name
     *
     * @return Boolean: are there details associated with the browser, such as
     * version, os, etc
     */
    private boolean areBrowserDetailsSet() {
        return browserInput != null && !browserInput.matches("^[a-zA-Z,]+$");
    }

    /**
     * Breaks up a string, and places it into a map. ampersands (&) are used to
     * split into key value pairs, while equals (=) are used to assign key vs
     * values
     *
     * @return Map: a map with values
     */
    private Map<String, String> parseMap() {
        final Map<String, String> map = new HashMap();
        for (String pair : browserInput.split("&")) {
            String[] kv = pair.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
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
