package utilities;

import org.testng.log4testng.Logger;

import java.io.IOException;
import java.io.InputStream;
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
    protected static String getProperty(String property) {
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
}
