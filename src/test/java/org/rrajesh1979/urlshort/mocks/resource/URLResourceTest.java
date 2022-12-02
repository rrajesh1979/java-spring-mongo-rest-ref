package org.rrajesh1979.urlshort.mocks.resource;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.resource.URLResource;
import org.rrajesh1979.urlshort.service.URLService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("URLResource")
class URLResourceTest {
    @Mock
    private URLService urlService;

    @InjectMocks
    private URLResource urlResource;

    @Test
    @DisplayName(
            "Should return a response with status url not found when the shorturl is not found")
    void getLongURLWhenShortURLIsNotFoundThenReturnResponseWithStatusUrlNotFound() {
        String shortURL = "abcdefg";
        when(urlService.findURLByShortURL(shortURL)).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = urlResource.getLongURL(shortURL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("URL not found", response.getBody().get(URLResource.RESPONSE_STATUS));
    }

    @Test
    @DisplayName("Should return a response with status success and data when the shorturl is found")
    void getLongURLWhenShortURLIsFoundThenReturnResponseWithStatusSuccessAndData() {
        String shortURL = "abcdefg";
        URLRecord urlRecord =
                new URLRecord(
                        new ObjectId(),
                        "https://www.google.com",
                        shortURL,
                        30,
                        "user1",
                        "ACTIVE",
                        0,
                        LocalDateTime.now().plusDays(30),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        when(urlService.findURLByShortURL(shortURL)).thenReturn(urlRecord);

        ResponseEntity<Map<String, Object>> response = urlResource.getLongURL(shortURL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("https://www.google.com", response.getBody().get("data"));

        verify(urlService).findURLByShortURL(shortURL);
    }
}