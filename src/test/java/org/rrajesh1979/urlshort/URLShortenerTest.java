package org.rrajesh1979.urlshort;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.rrajesh1979.urlshort.utils.ShortenURL;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class URLShortenerTest {
    private static class URLTestRecord {
        String userID;
        String longURL;
        String shortURL;
    }

    @Test
    @Order(1)
    public void testShortenURL() {
        //Array of UserID, LongURL, ShortURL
        List<URLTestRecord> urlTestMatrix = new ArrayList<>();
        urlTestMatrix.add(new URLTestRecord() {{
            userID = "rrajesh1979";
            longURL = "https://www.mongodb.com";
            shortURL = "1DEE3F5";
        }});

        urlTestMatrix.add(new URLTestRecord() {{
            userID = "rrajesh1979";
            longURL = "https://www.github.com";
            shortURL = "55nQArh";
        }});

        urlTestMatrix.add(new URLTestRecord() {{
            userID = "rrajesh1979";
            longURL = "https://www.yahoo.com";
            shortURL = "1HCuGBS";
        }});

        for (URLTestRecord urlTestRecord : urlTestMatrix) {
            assert (ShortenURL.shortenURL(urlTestRecord.longURL, urlTestRecord.userID).equals(urlTestRecord.shortURL));
        }
    }
}
