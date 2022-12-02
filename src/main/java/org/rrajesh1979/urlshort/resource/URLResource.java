package org.rrajesh1979.urlshort.resource;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rrajesh1979.urlshort.model.URLCreateRequest;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.model.URLUpdateRequest;
import org.rrajesh1979.urlshort.service.URLService;
import org.rrajesh1979.urlshort.utils.ShortenURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping( "${api.default.path}" )
@Log4j2
public class URLResource {
    private static final String ACCEPT_APPLICATION_JSON = "Accept=application/json";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_RESULTS = "results";
    public static final String RESPONSE_DATA = "data";
    public static final String SUCCESS = "success";

    public final URLService urlService;

    @Autowired
    public URLResource(URLService urlService) {
        this.urlService = urlService;
    }

    private static Map<String, Object> buildResponse(List<URLRecord> urls) {
        log.info("buildResponse: urls={}", urls);
        Map<String, Object> response = new HashMap<>();
        response.put(RESPONSE_DATA, urls);
        response.put(RESPONSE_RESULTS, urls.size());
        response.put(RESPONSE_STATUS, SUCCESS);
        return response;
    }

    @GetMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getAllURLs(@RequestParam int page, @RequestParam int limit) {
        log.info("getAllURLs called with page: {} and limit: {}", () -> page, () -> limit);
        List<URLRecord> urls = urlService.getAllURLs(page-1, limit);
        Map<String, Object> response = buildResponse(urls);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{userID}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getAllURLs(@PathVariable String userID, @RequestParam int page, @RequestParam int limit) {
        log.info("getAllURLs called with userID: {}, page: {} and limit: {}", () -> userID, () -> page, () -> limit);
        List<URLRecord> urls = urlService.getURLsByUserID(userID, page-1, limit);
        log.info("getAllURLs: urls={}", urls);
        Map<String, Object> response = new HashMap<>();
        response.put(RESPONSE_DATA, urls);
        response.put(RESPONSE_RESULTS, urls.size());
        response.put(RESPONSE_STATUS, SUCCESS);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/get/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getLongURL(@PathVariable String shortURL) {
        log.info("getLongURL called with shortURL: {}", () -> shortURL);
        URLRecord url = urlService.findURLByShortURL(shortURL);
        Map<String, Object> response = new HashMap<>();
        if (url != null) {
            response.put(RESPONSE_STATUS, "success");
            response.put(RESPONSE_DATA, url.longURL());
        } else {
            response.put(RESPONSE_STATUS, "URL not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/redirect/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public RedirectView redirectLongURL(@PathVariable String shortURL) {
        log.info("redirectLongURL called with shortURL: {}", () -> shortURL);
        URLRecord url = urlService.findOneAndUpdate(shortURL);
        if (url != null) {
            return new RedirectView(url.longURL());
        } else {
            return new RedirectView("404 page"); //TODO
        }
    }

    @DeleteMapping(value = "/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> deleteURL(@PathVariable String shortURL) {
        log.info("deleteURL called with shortURL: {}", () -> shortURL);
        Long deletedCount = urlService.deleteURL(shortURL);
        Map<String, Object> response = new HashMap<>();
        if (deletedCount == 0) {
            response.put(RESPONSE_STATUS, "error");
            response.put(RESPONSE_DATA, "URL not found");
        } else {
            response.put(RESPONSE_STATUS, "success");
            response.put(RESPONSE_RESULTS, deletedCount);
            response.put(RESPONSE_DATA, "URL deleted");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> createURL(@RequestBody URLCreateRequest url) {
        log.info("createURL called with longURL: {}", url::longURL);
        URLRecord newURL = new URLRecord(
                new ObjectId(),
                url.longURL(),
                ShortenURL.shortenURL(url.longURL(), url.userID()),
                url.expirationDays(),
                url.userID(),
                "ACTIVE",
                0,
                LocalDateTime.now().plusDays(url.expirationDays()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        InsertOneResult result = urlService.createURL(newURL);
        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put(RESPONSE_STATUS, "success");
            response.put(RESPONSE_DATA, Objects.requireNonNull(result.getInsertedId()).toString());
        } else {
            response.put(RESPONSE_STATUS, "error");
            response.put(RESPONSE_DATA, "URL not created");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> updateURL(@RequestBody URLUpdateRequest url) {
        log.info("updateURL called with longURL: {}", url::longURL);
        URLRecord updatedURL = new URLRecord(
                null,
                url.longURL(),
                ShortenURL.shortenURL(url.longURL(), url.userID()),
                url.expirationDays(),
                url.userID(),
                null,
                0,
                null,
                null,
                null
        );

        UpdateResult result = urlService.updateURL(updatedURL);
        Map<String, Object> response = new HashMap<>();
        if (result == null) {
            response.put(RESPONSE_STATUS, "error");
            response.put(RESPONSE_DATA, "URL not found");
        } else {
            response.put(RESPONSE_STATUS, "success");
            response.put(RESPONSE_RESULTS, result.getModifiedCount());
            response.put(RESPONSE_DATA, "URL updated");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
