package org.rrajesh1979.urlshort.utils;

import com.google.common.hash.Hashing;

import java.math.BigInteger;

public class ShortenURL {
    /*
    1. Use Base 58 encoding algorithm
    2. Sha256 hash of the long URL
    3. Convert the hash to BigInteger
    4. Convert the BigInteger to Base 58 string
     */

    public static String shortenURL(String longURL, String userID) {
        String inputURL = longURL + userID;
        byte[] urlHashBytes = Hashing.sha256().hashBytes(inputURL.getBytes()).asBytes();
        // Convert the hash to BigInteger
        BigInteger urlHash = new BigInteger(1, urlHashBytes);
        // Convert the BigInteger to Base 58 string
        String shortURL = Base58.encode(urlHash.toByteArray());
        return shortURL.substring(0, 7); //TODO Error handling
    }
}
