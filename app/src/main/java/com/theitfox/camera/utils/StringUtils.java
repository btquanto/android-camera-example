package com.theitfox.camera.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by btquanto on 16/09/2016.
 */
public class StringUtils {
    private static MessageDigest sMessageDigest;

    private static MessageDigest getMessageDigest() {
        if (sMessageDigest == null) {
            generateMessageDigest("SHA-256");
            // fall back
            generateMessageDigest("SHA-1");
            // fall back
            generateMessageDigest("MD5");
        }
        return sMessageDigest;
    }

    private static void generateMessageDigest(String algorithm) {
        if (sMessageDigest == null) {
            try {
                sMessageDigest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hash string.
     *
     * @param value the value
     * @return the string
     */
    public static String hash(String value) {
        MessageDigest md = getMessageDigest();
        String hashString;
        if (md != null) {
            md.reset();
            md.update(value.getBytes());
            hashString = new BigInteger(1, md.digest()).toString(16);
        } else {
            int hashInt = 7;
            for (int i = 0; i < value.length(); i++) {
                hashInt = hashInt * 31 + value.charAt(i);
            }
            hashString = Integer.toString(hashInt);
        }
        return hashString;
    }

    /**
     * Concatenate string.
     *
     * @param strings the strings
     * @return the string
     */
    public static String concatenate(List<? extends Object> strings) {
        return concatenate(strings, null);
    }

    /**
     * Concatenate string.
     *
     * @param strings   the strings
     * @param delimiter the delimiter
     * @return the string
     */
    public static String concatenate(List<? extends Object> strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (Object s : strings) {
            builder.append(s);
            if (delimiter != null) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }
}
