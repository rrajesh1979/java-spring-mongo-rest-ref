package org.rrajesh1979.urlshort.resource;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping( "${api.default.path}" )
@Slf4j
public class URLResource {
    private final Bucket bucket;
    private static final String ACCEPT_APPLICATION_JSON = "Accept=application/json";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_RESULTS = "results";
    public static final String RESPONSE_DATA = "data";
    public static final String SUCCESS = "success";

    public final URLService urlService;

    @Autowired
    public URLResource(URLService urlService) {
        this.urlService = urlService;

        //Simple Rate Limiting
        //Allow 5 requests per minute
        //TODO: Make this configurable. Refactor.
        Bandwidth limit = Bandwidth.simple(5, Duration.ofMinutes(1));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private static Map<String, Object> buildResponse(List<URLRecord> urls) {
        Map<String, Object> response = new HashMap<>();
        response.put(RESPONSE_DATA, urls);
        response.put(RESPONSE_RESULTS, urls.size());
        response.put(RESPONSE_STATUS, SUCCESS);
        return response;
    }

    @GetMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of all URLs",
                    content = {
                        @Content(
                            mediaType = "application/json",
                            examples = {
                                @ExampleObject(
                                    name = "urls",
                                    value = """
                                            {
                                            	"data": [
                                            		{
                                            			"id": {
                                            				"timestamp": 1669221663,
                                            				"date": "2022-11-23T16:41:03.000+00:00"
                                            			},
                                            			"longURL": "https://www.mongodb.com/docs/drivers/go/current/quick-start/",
                                            			"shortURL": "6E2XtLa9",
                                            			"expirationDays": 10,
                                            			"userID": "rrajesh1979",
                                            			"status": "ACTIVE",
                                            			"redirects": 0,
                                            			"expiresAt": "2022-12-03T11:41:03.132",
                                            			"createdAt": "2022-11-23T11:41:03.132",
                                            			"updatedAt": "2022-11-23T11:41:03.132"
                                            		}
                                            	],
                                            	"results": 1,
                                            	"status": "success"
                                            }
                                            """
                                )
                            }
                        )
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> getAllURLs(@RequestParam int page, @RequestParam int limit) {
        if (bucket.tryConsume(1)) {
            log.info("getAllURLs called with page: {} and limit: {}", page, limit);
            List<URLRecord> urls = urlService.getAllURLs(page - 1, limit);
            Map<String, Object> response = buildResponse(urls);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping(value = "/{userID}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getAllURLs(@PathVariable String userID, @RequestParam int page, @RequestParam int limit) {
        log.info("getAllURLs called with userID: {}, page: {} and limit: {}", userID, page, limit);
        List<URLRecord> urls = urlService.getURLsByUserID(userID, page-1, limit);
        Map<String, Object> response = new HashMap<>();
        response.put(RESPONSE_DATA, urls);
        response.put(RESPONSE_RESULTS, urls.size());
        response.put(RESPONSE_STATUS, SUCCESS);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/get/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "URL Record",
                    content = {
                        @Content(
                            mediaType = "application/json",
                            examples = {
                                @ExampleObject(
                                    name = "url",
                                    value = """
                                            {
                                             	"data": "https://www.docker.com",
                                             	"status": "success"
                                             }
                                            """
                                )
                            }
                        )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "URL Not Found",
                    content = {
                        @Content(
                            mediaType = "application/json",
                            examples = {
                                @ExampleObject(
                                    name = "url",
                                    value = """
                                            {
                                                "status": "URL Not Found"
                                            }
                                            """
                                )
                            }
                        )
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> getLongURL(@PathVariable String shortURL) {
        log.info("getLongURL called with shortURL: {}", shortURL);
        URLRecord url = urlService.findURLByShortURL(shortURL);
        Map<String, Object> response = new HashMap<>();
        if (url != null) {
            response.put(RESPONSE_STATUS, "success");
            response.put(RESPONSE_DATA, url.longURL());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put(RESPONSE_STATUS, "URL not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(value = "/redirect/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public RedirectView redirectLongURL(@PathVariable String shortURL) {
        log.info("redirectLongURL called with shortURL: {}", shortURL);
        URLRecord url = urlService.findOneAndUpdate(shortURL);
        if (url != null) {
            return new RedirectView(url.longURL());
        } else {
            return new RedirectView("404 page"); //TODO
        }
    }

    @DeleteMapping(value = "/{shortURL}", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> deleteURL(@PathVariable String shortURL) {
        log.info("deleteURL called with shortURL: {}", shortURL);
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
        log.info("createURL called with longURL: {}", url.longURL());
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
        log.info("updateURL called with longURL: {}", url.longURL());
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
