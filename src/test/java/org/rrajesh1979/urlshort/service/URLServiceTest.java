package org.rrajesh1979.urlshort.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class URLServiceTest {
    @Mock
    private URLRepository urlRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private URLService urlService;

    @Test
    @DisplayName("Should return null when the shorturl is not found")
    void findOneAndUpdateWhenShortURLIsNotFoundThenReturnNull() {
        String shortURL = "abc";
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(URLRecord.class)))
                .thenReturn(null);

        URLRecord urlRecord = urlService.findOneAndUpdate(shortURL);

        assertNull(urlRecord);
    }

    @Test
    @DisplayName("Should return the urlrecord when the shorturl is found")
    void findOneAndUpdateWhenShortURLIsFoundThenReturnTheURLRecord() {
        String shortURL = "http://localhost:8080/abc";
        URLRecord urlRecord =
                new URLRecord(
                        new ObjectId(),
                        "http://www.google.com",
                        shortURL,
                        30,
                        "user1",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(URLRecord.class)))
                .thenReturn(urlRecord);

        URLRecord result = urlService.findOneAndUpdate(shortURL);

        assertEquals(urlRecord, result);
    }
}