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
        long redirects,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public org.bson.Document toDocument() {
        org.bson.Document urlDocument = new org.bson.Document();
        urlDocument.append("longURL", this.longURL);
        urlDocument.append("shortURL", this.shortURL);
        urlDocument.append("expirationDays", this.expirationDays);
        urlDocument.append("userID", this.userID);
        urlDocument.append("status", this.status);
        urlDocument.append("redirects", this.redirects);
        urlDocument.append("expiresAt", this.expiresAt);
        urlDocument.append("createdAt", this.createdAt);
        urlDocument.append("updatedAt", this.updatedAt);
        return urlDocument;
    }
}
