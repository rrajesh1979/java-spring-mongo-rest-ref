package org.rrajesh1979.urlshort.testcontainer;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.rrajesh1979.urlshort.service.URLService;
import org.rrajesh1979.urlshort.utils.ShortenURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Testcontainers
@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class URLShortenerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(URLShortenerIntegrationTest.class);

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.1");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    @Autowired
    private URLRepository urlRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private record URLData(String userID, String longURL, int expirationDays) {
    }

    @BeforeEach
    void setUp() {
        log.info("Starting test");

        List<URLData> urlTestDataSet = new ArrayList<>();
        urlTestDataSet.add(new URLData("rrajesh1979", "https://www.google.com", 15));
        urlTestDataSet.add(new URLData("rrajesh1979", "https://www.yahoo.com", 30));
        urlTestDataSet.add(new URLData("rrajesh1979", "https://www.bing.com", 45));
        urlTestDataSet.add(new URLData("robert", "https://www.amazon.com", 60));
        urlTestDataSet.add(new URLData("robert", "https://www.ebay.com", 75));
        urlTestDataSet.add(new URLData("robert", "https://www.walmart.com", 90));

        assert mongoDBContainer.isRunning();

        for (URLData urlData : urlTestDataSet) {
            URLRecord urlRecord = new URLRecord(
                    new ObjectId(),
                    urlData.longURL,
                    ShortenURL.shortenURL(urlData.longURL, urlData.userID),
                    urlData.expirationDays,
                    urlData.userID,
                    "ACTIVE",
                    0,
                    LocalDateTime.now().plusDays(urlData.expirationDays),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            this.urlRepository.insert(urlRecord);
        }

        log.info("Test data setup complete");
    }

    @AfterEach
    void tearDown() {
        log.info("Ending test");
    }

    @Test
    @Order(1)
    @DisplayName("Should return all URLs in repository for a given page and size")
    void testGetAllURLs() {
        log.info("Starting test testGetAllURLs for page 0 and size 4");
        URLService urlService = new URLService(urlRepository, mongoTemplate);

        // Get all URLs for page 0 and size 3
        Assertions.assertEquals(3, urlService.getAllURLs(0, 3).size());

        // Get all URLs for next page and size 3
        Assertions.assertEquals(3, urlService.getAllURLs(1, 3).size());

        // Get all URLs for second page and size 10. We only have 6 URLs in the repository
        Assertions.assertEquals(0, urlService.getAllURLs(1, 10).size());

        log.info("End test testGetAllURLs for page 0 and size 4");
    }

    @Test
    @Order(2)
    @DisplayName("Should return all URLs in repository for a given user, page and size")
    void testGetAllURLsForUser() {
        log.info("Starting test testGetAllURLsForUser for user robert, page 0 and size 2");
        URLService urlService = new URLService(urlRepository, mongoTemplate);

        //Test for valid user and limit page and size
        Assertions.assertEquals(2, urlService.getURLsByUserID("robert", 0, 2).size());

        //Test for valid user and all URLs
        Assertions.assertNotEquals(2, urlService.getURLsByUserID("robert", 0, 5).size());

        // Test for invalid user
        Assertions.assertEquals(0, urlService.getURLsByUserID("john", 1, 2).size());

        log.info("End test testGetAllURLsForUser for user robert, page 0 and size 4");
    }

}
