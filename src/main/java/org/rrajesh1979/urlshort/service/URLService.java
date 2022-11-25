package org.rrajesh1979.urlshort.service;

import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class URLService {
    public URLRepository urlRepository;

    @Autowired
    public URLService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ResponseEntity<Map<String, Object>> getAllURLs(int page, int limit) {
        try {
            List<URLRecord> urls = new ArrayList<URLRecord>();
            Pageable paging = PageRequest.of(page, limit);
            Page<URLRecord> pageURLs = urlRepository.findAll(paging);
            urls = pageURLs.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("data", urls);
            response.put("results", urls.stream().count());
            response.put("status", "success");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
