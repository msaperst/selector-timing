package utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriverException;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sauce {

    private static final Logger log = Logger.getLogger(Sauce.class);

    private static String url = "https://wiki-assets.saucelabs.com/";
    private static String timestamp = "1581512900";

    public static List<Browser> allBrowsers() throws IOException {
        List<Browser> browserList = new ArrayList();

        JsonObject seleniumObjects = get(url + "data/selenium_." + timestamp + ".json").getAsJsonObject();
        JsonArray desktops = seleniumObjects.getAsJsonArray("list").get(0).getAsJsonObject().getAsJsonArray("list");
        // get each desktop and operating system, then get each browser (latest and latest -1) for each
        for (int i = 0; i < desktops.size(); i++) {
            JsonObject desktop = desktops.get(i).getAsJsonObject();
            String desktopName = desktop.get("name").getAsString();
            JsonArray operatingSystems = desktop.getAsJsonArray("list").get(0).getAsJsonObject().getAsJsonArray("list");
            for (int j = 0; j < operatingSystems.size(); j++) {
                String operatingSystem = operatingSystems.get(j).getAsJsonObject().get("name").getAsString();
                JsonObject browserObjects = get(url + "data/selenium_" + desktopName.toLowerCase() + "_" + operatingSystem.toLowerCase().replace(" ", "-").replace(".", "-") + "." + timestamp + ".json").getAsJsonObject();
                JsonArray browsers = browserObjects.getAsJsonArray("list");
                for (int k = 0; k < browsers.size(); k++) {
                    JsonObject browser = browsers.get(k).getAsJsonObject();
                    String browserName = browser.get("name").getAsString();
                    JsonArray browserVersions = browser.getAsJsonArray("list");
                    // find the latest two versions
                    int browserVersionCount = 0;
                    for (int l = 0; l < browserVersions.size() && browserVersionCount < 2; l++) {
                        String browserVersion = browserVersions.get(l).getAsJsonObject().get("name").getAsString();
                        String platformName = browserVersions.get(l).getAsJsonObject().getAsJsonArray("list").get(1).getAsJsonObject().get("api").getAsString();
                        if (!browserVersion.startsWith("latest") && !browserVersion.startsWith("dev") && !browserVersion.startsWith("beta")) {
                            browserVersionCount++;
                            try {
                                browserList.add(new Browser(browserName, browserVersion, operatingSystem.replaceAll("macOS ", "")));
                            } catch (WebDriverException e) {
                                try {
                                    browserList.add(new Browser(browserName, browserVersion, platformName));
                                } catch (WebDriverException f) {
                                    log.error("Unable to add browser combination. " + e);
                                    log.error("Unable to add browser combination. " + f);
                                    log.error("    name:     " + browserName);
                                    log.error("    version:  " + browserVersion);
                                    log.error("    platform: " + platformName);
                                    log.error("    os:       " + operatingSystem);
                                }
                            }
                        }
                    }
                }
            }
        }
        return browserList;
    }

    private static JsonElement get(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return new JsonParser().parse(EntityUtils.toString(entity));
            }
        }
        return null;
    }
}
