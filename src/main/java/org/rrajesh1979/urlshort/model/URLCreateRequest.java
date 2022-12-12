package org.rrajesh1979.urlshort.model;

public record URLCreateRequest(
        String longURL,
        long expirationDays,
        String userID
) {
}
