package org.rrajesh1979.urlshort.resource;

import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "${api.default.path}" )
public class URLResource {
    private static final String ACCEPT_APPLICATION_JSON = "Accept=application/json";

    public URLService urlService;

    @Autowired
    public URLResource(URLService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/", headers = ACCEPT_APPLICATION_JSON)
    public ResponseEntity<Map<String, Object>> getAllURLs(@RequestParam int page, @RequestParam int limit) {
        return urlService.getAllURLs(page-1, limit);
    }
}
