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

    public static void main(String[] args) {
        System.out.println("Short URL: " + shortenURL("https://www.mongodb.com", "rrajesh1979").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.github.com", "rrajesh1979").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.yahoo.com", "rrajesh1979").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.facebook.com", "rrajesh1979").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.google.com", "rrajesh1979").substring(0, 7));
        System.out.println("\n");
        System.out.println("Short URL: " + shortenURL("https://www.mongodb.com", "robert").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.github.com", "robert").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.yahoo.com", "robert").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.facebook.com", "robert").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.google.com", "robert").substring(0, 7));
        System.out.println("\n");
        System.out.println("Short URL: " + shortenURL("https://www.mongodb.com", "john").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.github.com", "john").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.yahoo.com", "john").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.facebook.com", "john").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.google.com", "john").substring(0, 7));
        System.out.println("\n");
        System.out.println("Short URL: " + shortenURL("https://www.mongodb.com", "johndoe").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.github.com", "johndoe").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.yahoo.com", "johndoe").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.facebook.com", "johndoe").substring(0, 7));
        System.out.println("Short URL: " + shortenURL("https://www.google.com", "johndoe").substring(0, 7));
    }
}
