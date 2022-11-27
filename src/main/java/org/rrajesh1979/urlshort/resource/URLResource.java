package org.rrajesh1979.urlshort.resource;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.rrajesh1979.urlshort.model.URLRecord;
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

    public final URLService urlService;

    @Autowired
    public URLResource(URLService urlService) {
        this.urlService = urlService;
    }

    private static Map<String, Object> buildResponse(List<URLRecord> urls) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", urls);
        response.put("results", urls.size());
        response.put("status", "success");
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
        List<URLRecord> urls = urlService.findURLsByUserID(userID, page-1, limit);
        Map<String, Object> response = buildResponse(urls);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/get/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getLongURL(@PathVariable String shortURL) {
        URLRecord url = urlService.findURLByShortURL(shortURL);
        Map<String, Object> response = new HashMap<>();
        if (url != null) {
            response.put("status", "success");
            response.put("data", url.longURL());
        } else {
            response.put("status", "URL not found");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/redirect/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public RedirectView redirectLongURL(@PathVariable String shortURL) {
        URLRecord url = urlService.findOneAndUpdate(shortURL);
        if (url != null) {
            return new RedirectView(url.longURL());
        } else {
            return new RedirectView("404 page"); //TODO
        }
    }

    @DeleteMapping(value = "/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> deleteURL(@PathVariable String shortURL) {
        Long deletedCount = urlService.deleteURL(shortURL);
        Map<String, Object> response = new HashMap<>();
        if (deletedCount == 0) {
            response.put("status", "error");
            response.put("data", "URL not found");
        } else {
            response.put("status", "success");
            response.put("result", deletedCount);
            response.put("data", "URL deleted");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> createURL(@RequestBody URLRecord url) {

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
            response.put("status", "success");
            response.put("data", Objects.requireNonNull(result.getInsertedId()).toString());
        } else {
            response.put("status", "error");
            response.put("data", "URL not created");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> updateURL(@RequestBody URLRecord url) {

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
            response.put("status", "error");
            response.put("data", "URL not found");
        } else {
            response.put("status", "success");
            response.put("result", result.getModifiedCount());
            response.put("data", "URL updated");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
