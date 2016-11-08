package uk.gov.digital.ho.proving.financial.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Home Office Digital.
 */
public class UrlConnectionTester {

    private static Logger LOGGER = LoggerFactory.getLogger(UrlConnectionTester.class);

    public int getResponseCodeFor(String uri, int timeout) {

        try {

            LOGGER.debug("Getting response code from uri: {}", uri);
            URL url = new URL(uri);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            connection.connect();

            return connection.getResponseCode();

        } catch (Exception e) {
            LOGGER.warn("Exception while getting response: {}", e.getMessage());
            LOGGER.warn("Exception", e);
            return 0;
        }
    }
}
