package utilities;

import org.openqa.selenium.By;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.Property.getProperty;

public class Locator {

    public static List<By> getLocators() throws IOException {
        List<By> locators = new ArrayList();
        int locatorCounter = 0;
        while (true) {
            String locatorType = getProperty("locator" + locatorCounter + ".type");
            String locatorSelector = getProperty("locator" + locatorCounter + ".selector");
            if (locatorType != null) {
                locators.add(defineByElement(locatorType, locatorSelector));
            } else {
                break;
            }
            locatorCounter++;
        }
        return locators;
    }

    /**
     * Determines Selenium's 'By' object using Webdriver
     *
     * @return By: the Selenium object
     */
    private static By defineByElement(String locatorType, String locatorSelector) throws IOException {
        // consider adding strengthening
        By byElement;
        switch (locatorType) { // determine which locator type we are interested in
            case "xpath":
                byElement = By.xpath(locatorSelector);
                break;
            case "id":
                byElement = By.id(locatorSelector);
                break;
            case "name":
                byElement = By.name(locatorSelector);
                break;
            case "className":
                byElement = By.className(locatorSelector);
                break;
            case "cssSelector":
                byElement = By.cssSelector(locatorSelector);
                break;
            case "linkText":
                byElement = By.linkText(locatorSelector);
                break;
            case "partialLinkText":
                byElement = By.partialLinkText(locatorSelector);
                break;
            case "tagName":
                byElement = By.tagName(locatorSelector);
                break;
            default:
                throw new IOException("Locator provided does not match a known locator type");
        }
        return byElement;
    }
}
