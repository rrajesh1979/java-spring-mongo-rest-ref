package org.rrajesh1979.urlshort.model;

public record URLUpdateRequest(
        String longURL,
        long expirationDays,
        String userID
) {
}
