package org.rrajesh1979.urlshort.testcontainer;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.rrajesh1979.urlshort.model.URLCreateRequest;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.model.URLUpdateRequest;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.rrajesh1979.urlshort.resource.URLResource;
import org.rrajesh1979.urlshort.service.URLService;
import org.rrajesh1979.urlshort.utils.ShortenURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Testcontainers
@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class URLShortenerResourceIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(URLShortenerServiceIntegrationTest.class);

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

    private URLService urlService;
    private URLResource urlResource;

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

        this.urlService = new URLService(this.urlRepository, this.mongoTemplate);
        this.urlResource = new URLResource(this.urlService);

        log.info("Test data setup complete");
    }

    @AfterEach
    void tearDown() {
        log.info("Test data cleanup complete");
    }

    @Test
    @Order(1)
    void testGetAllURLs() {
        log.info("Test testGetAllURLs started");

        //Test getting all 6 URLs in the repository
        ResponseEntity<Map<String, Object>>  response =
                this.urlResource.getAllURLs(1, 10);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals(6, response.getBody().get("results"));
        Assertions.assertEquals("success", response.getBody().get("status"));

        //Test by limiting the results to 3
        response = this.urlResource.getAllURLs(1, 3);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals(3, response.getBody().get("results"));
        Assertions.assertEquals("success", response.getBody().get("status"));

        log.info("Test testGetAllURLs complete");
    }

    @Test
    @Order(2)
    void testGetURLsByUserID() {
        log.info("Test testGetURLsByUserID started");

        //Test for user that exists
        ResponseEntity<Map<String, Object>>  response =
                this.urlResource.getAllURLs("rrajesh1979", 1, 10);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals(3, response.getBody().get("results"));
        Assertions.assertEquals("success", response.getBody().get("status"));

        //Test for user that does not exist
        response = this.urlResource.getAllURLs("rrajesh1978", 1, 10);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals(0, response.getBody().get("results"));
        Assertions.assertEquals("success", response.getBody().get("status"));

        log.info("Test testGetURLsByUserID complete");
    }

    @Test
    @Order(3)
    void testGetURLByShortURL() {
        log.info("Test testGetURLByShortURL started");

        //Test for short URL that exists
        ResponseEntity<Map<String, Object>>  response =
                this.urlResource.getLongURL("1BtYYyQ");
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("success", response.getBody().get("status"));
        Assertions.assertEquals("https://www.google.com", response.getBody().get("data"));

        //Test for short URL that does not exist
        response = this.urlResource.getLongURL("doesNotExist");
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("URL not found", response.getBody().get("status"));

        log.info("Test testGetURLByShortURL complete");
    }

    @Test
    @Order(4)
    void testCreateURL() {
        log.info("Test testCreateURL started");

        //Create URLCreateRequest
        URLCreateRequest urlCreateRequest = new URLCreateRequest("https://www.testing.com", 15, "rrajesh1979");

        //Test for short URL that exists
        ResponseEntity<Map<String, Object>>  response =
                this.urlResource.createURL(urlCreateRequest);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("success", response.getBody().get("status"));
        Assertions.assertNotEquals("", response.getBody().get("data"));

        //Query and validate the new URL is created
        URLRecord urlRecord = this.urlService.findURLByShortURL("8kC1GSF");
        Assertions.assertNotNull(urlRecord);
        Assertions.assertEquals("https://www.testing.com", urlRecord.longURL());

        log.info("Test testCreateURL complete");
    }

    @Test
    @Order(5)
    void testUpdateURL() {
        log.info("Test testUpdateURL started");

        //Create URLUpdateRequest
        URLUpdateRequest urlUpdateRequest = new URLUpdateRequest("https://www.google.com", 123, "rrajesh1979");

        //Test for short URL that exists
        ResponseEntity<Map<String, Object>>  response =
                this.urlResource.updateURL(urlUpdateRequest);
        log.info("Response: {}", response);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("success", response.getBody().get("status"));
        Assertions.assertEquals("URL updated", response.getBody().get("data"));
        Assertions.assertEquals(1L, response.getBody().get("results"));

        //TODO: Add a resource to get the full URL object back and compare for updates.

        log.info("Test testUpdateURL complete");
    }


}
