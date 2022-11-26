package org.rrajesh1979.urlshort.service;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.rrajesh1979.urlshort.repository.URLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class URLService {
    public final URLRepository urlRepository;
    public final MongoTemplate mongoTemplate;

    @Autowired
    public URLService(URLRepository urlRepository, MongoTemplate mongoTemplate) {
        this.urlRepository = urlRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<URLRecord> getAllURLs(int page, int limit) {
        List<URLRecord> urls = new ArrayList<>();
        try {
            Pageable paging = PageRequest.of(page, limit);
            Page<URLRecord> pageURLs = urlRepository.findAll(paging);
            urls = pageURLs.getContent();

            return urls;
        } catch (Exception e) {
            return urls;
        }
    }

    //FindURLsByUserID
    public List<URLRecord> findURLsByUserID(String userID, int page, int limit) {
        List<URLRecord> urls = new ArrayList<>();
        try {
            Pageable paging = PageRequest.of(page, limit);
            urls = urlRepository.findByUserID(userID, paging);
            return urls;
        } catch (Exception e) {
            return urls;
        }
    }

    //FindURLByShortURL
    public URLRecord findURLByShortURL(String shortURL) {
        List<URLRecord> urls;
        try {
            urls = urlRepository.findByShortURL(shortURL);
            if (!urls.isEmpty()) {
                return urls.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    //FindOneAndUpdate increment redirects
    public URLRecord findOneAndUpdate(String shortURL) {
        Query query = new Query();
        query.addCriteria(Criteria.where("shortURL").is(shortURL));
        Update update = new Update();
        update.inc("redirects", 1);
        return mongoTemplate.findAndModify(query, update, URLRecord.class);
    }

    //DeleteURL
    public Long deleteURL(String shortURL) {
        return urlRepository.deleteByShortURL(shortURL);
    }

    //UpdateURL
    public UpdateResult updateURL(URLRecord urlRecord) {
        UpdateResult result;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("longURL").is(urlRecord.longURL()));

            Update update = new Update();
            update.set("shortURL", urlRecord.shortURL()); //TODO
            update.set("expirationDays", urlRecord.expirationDays());
            update.set("redirects", 0);
            update.set("status", "ACTIVE");
            update.set("updatedAt", LocalDateTime.now());
            update.set("expiresAt", LocalDateTime.now().plusDays(urlRecord.expirationDays()));

            result = mongoTemplate.updateFirst(query, update, "urls");
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    //CreateURL
    public InsertOneResult createURL(URLRecord urlRecord) {
        InsertOneResult result;
        try {
            result = mongoTemplate.getCollection("urls").insertOne(urlRecord.toDocument());
            return result;
        } catch (Exception e) {
            return null;
        }
    }


}
