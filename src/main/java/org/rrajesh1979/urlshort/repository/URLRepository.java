package org.rrajesh1979.urlshort.repository;

import org.bson.types.ObjectId;
import org.rrajesh1979.urlshort.model.URLRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface URLRepository extends MongoRepository<URLRecord, ObjectId> {
    Page<URLRecord> findAll(Pageable pageable);
}
