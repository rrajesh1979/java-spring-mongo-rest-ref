package org.rrajesh1979.urlshort.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

public record URLCreateRequest(
        String longURL,
        long expirationDays,
        String userID
) {
}
