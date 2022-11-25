package org.rrajesh1979.urlshort.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls")
public record URLRecord(
        @Id ObjectId id,
        String longURL,
        String shortURL,
        long expirationDays,
        String userID,
        String status,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
