package org.rrajesh1979.urlshort;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.rrajesh1979.urlshort.utils.ShortenURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Testcontainers
@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class URLShortenerApplicationTests {

    private static final Logger log = Logger.getLogger(URLShortenerApplicationTests.class.getName());

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.1");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    @Autowired
    private URLRepository urlRepository;

    @After
    public void cleanUp() {
        this.urlRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateURLs(){
        String userID = "rrajesh1979";
        String longURL1 = "https://www.google.com";
        int expirationDays1 = 15;
        URLRecord newURL1 = new URLRecord(
                new ObjectId(),
                longURL1,
                ShortenURL.shortenURL(longURL1, userID),
                expirationDays1,
                userID,
                "ACTIVE",
                0,
                LocalDateTime.now().plusDays(expirationDays1),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        String longURL2 = "https://www.yahoo.com";
        int expirationDays2 = 25;
        URLRecord newURL2 = new URLRecord(
                new ObjectId(),
                longURL2,
                ShortenURL.shortenURL(longURL2, userID),
                expirationDays2,
                userID,
                "ACTIVE",
                0,
                LocalDateTime.now().plusDays(expirationDays2),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        String longURL3 = "https://www.bing.com";
        int expirationDays3 = 15;
        URLRecord newURL3 = new URLRecord(
                new ObjectId(),
                longURL3,
                ShortenURL.shortenURL(longURL3, userID),
                expirationDays3,
                userID,
                "ACTIVE",
                0,
                LocalDateTime.now().plusDays(expirationDays3),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assert mongoDBContainer.isRunning();
        this.urlRepository.insert(newURL1);
        this.urlRepository.insert(newURL2);
        this.urlRepository.insert(newURL3);
    }

    @Test
    @Order(2)
    void testFindAllURLs() {
        Pageable paging = PageRequest.of(0, 5);
        Page<URLRecord> pageURLs = this.urlRepository.findAll(paging);
        List<URLRecord> urls = pageURLs.getContent();
        assert urls.size() == 3;
    }

    @Test
    @Order(3)
    void testFindByShortURL() {
        log.info("testFindByShortURL");
        assert mongoDBContainer.isRunning();
        assert this.urlRepository.findByShortURL("1BtYYyQ").size() == 1;
    }

}
