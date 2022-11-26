package org.rrajesh1979.urlshort.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface URLRepository extends MongoRepository<URLRecord, ObjectId> {
    Page<URLRecord> findAll(Pageable pageable);
    List<URLRecord> findByUserID(String userID, Pageable pageable);
    List<URLRecord> findByShortURL(String shortURL);
    Long deleteByShortURL(String shortURL);
}
