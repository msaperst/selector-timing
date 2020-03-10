package utilities;

import org.testng.log4testng.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Property {

    private static final Logger log = Logger.getLogger(Property.class);
    private static final String CONDITIONS = "conditions.properties";

    /**
     * Retrieves the specified program property. if it exists from the system properties, that is returned, overridding
     * all other values. Otherwise, if it exists from the properties file, that is returned, otherwise, null is returned
     *
     * @param property - what property value to return
     * @return String: the property value, null if unset
     */
    public static String getProperty(String property) {
        if (System.getProperty(property) != null) {
            return System.getProperty(property).trim();
        }
        Properties prop = new Properties();
        try (InputStream input = Property.class.getClassLoader().getResourceAsStream(CONDITIONS)) {
            prop.load(input);
        } catch (NullPointerException | IOException e) {
            log.info(e);
        }
        String fullProperty = prop.getProperty(property);
        if (fullProperty != null) {
            fullProperty = fullProperty.trim();
        }
        return fullProperty;
    }

    public static List<Browser> getBrowsers() throws IOException {
        if ("all-sauce".equals(getProperty("browser0.name")) && getProperty("hub") != null) {
            return Sauce.allBrowsers();
        }
        List<Browser> browsers = new ArrayList();
        int browserCounter = 0;
        while (true) {
            String browserName = getProperty("browser" + browserCounter + ".name");
            String browserVersion = getProperty("browser" + browserCounter + ".version");
            String browserPlatform = getProperty("browser" + browserCounter + ".platform");
            if (browserName != null) {
                browsers.add(new Browser(browserName, browserVersion, browserPlatform));
            } else {
                break;
            }
            browserCounter++;
        }
        return browsers;
    }
}
