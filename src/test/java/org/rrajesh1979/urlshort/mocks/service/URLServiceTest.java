package org.rrajesh1979.urlshort.mocks.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.rrajesh1979.urlshort.service.URLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class URLServiceTest {
    Logger log = LoggerFactory.getLogger(URLServiceTest.class);

    private URLService urlService;

    private URLRepository urlRepository;
    public MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        urlRepository = Mockito.mock(URLRepository.class);
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        urlService = new URLService(urlRepository, mongoTemplate);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllURLs() {
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
                        "https://www.mongodb.com",
                        "shortURL2",
                        30,
                        "user1",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        Pageable paging = PageRequest.of(0, 5);
        //Create Page of URLRecords
        Page<URLRecord> pagedResult = Mockito.mock(Page.class);
        Mockito.when(pagedResult.getContent()).thenReturn(Arrays.asList(urlRecord1, urlRecord2));
        Mockito.when(urlRepository.findAll(paging)).thenReturn(pagedResult);

        //Act
        List<URLRecord> urlRecords = urlService.getAllURLs(0, 5);

        //Assert
        assertThat(urlRecords.size()).isEqualTo(2);
        assertThat(urlRecords).contains(urlRecord1);
        assertThat(urlRecords).contains(urlRecord2);
    }

    @Test
    void findURLsByUserID() {
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
                        "https://www.mongodb.com",
                        "shortURL2",
                        30,
                        "user2",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        Pageable paging = PageRequest.of(0, 5);
        //Create Page of URLRecords
        Mockito.when(urlRepository.findByUserID("user1", paging)).thenReturn(List.of(urlRecord1));

        //Act
        List<URLRecord> urlRecords = urlService.getURLsByUserID("user1", 0, 5);

        //Assert
        assertThat(urlRecords.size()).isEqualTo(1);
        assertThat(urlRecords).contains(urlRecord1);
        assertThat(urlRecords).doesNotContain(urlRecord2);
    }

    @Test
    void findURLByShortURL() {
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
                        "https://www.mongodb.com",
                        "shortURL2",
                        30,
                        "user2",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        //Create Page of URLRecords
        Mockito.when(urlRepository.findByShortURL("shortURL1")).thenReturn(List.of(urlRecord1));

        //Act
        URLRecord urlRecord = urlService.findURLByShortURL("shortURL1");

        //Assert
        assertThat(urlRecord).isEqualTo(urlRecord1);
        assertThat(urlRecord).isNotEqualTo(urlRecord2);
    }

    @Test
    void findOneAndUpdate() {
    }

    @Test
    void deleteURL() {
    }

    @Test
    void updateURL() {
    }

    @Test
    void createURL() {
    }
}