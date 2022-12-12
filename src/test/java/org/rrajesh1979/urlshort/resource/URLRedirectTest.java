package org.rrajesh1979.urlshort.resource;

import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.rrajesh1979.urlshort.service.URLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;


class URLRedirectTest {

    Logger log = LoggerFactory.getLogger(URLRedirectTest.class);

    private URLService urlService;
    private URLResource urlResource;

    public MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        URLRepository urlRepository = Mockito.mock(URLRepository.class);
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        urlService = new URLService(urlRepository, mongoTemplate);
        urlResource = new URLResource(urlService);
    }

    //Temporarily disabled
    @Ignore("Test is not fully implemented. Need to mock the URLService. Will be implemented later")
    @Test
    void redirectLongURL() throws Exception {
        //Arrange
        URLRecord urlRecord1 =
                new URLRecord(
                        new ObjectId(),
                        "https://www.google.com",
                        "shortURL1",
                        30,
                        "user1",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        URLRecord urlRecord2 =
                new URLRecord(
                        new ObjectId(),
                        "https://www.mongodb.com/docs/drivers/go/current/quick-start/",
                        "6E2XtLa9",
                        30,
                        "user1",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        Mockito.when(urlService.findOneAndUpdate("6E2XtLa9")).thenReturn(urlRecord2);

        //Act
    }
}
