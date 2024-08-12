package com.unsolved.hgu.util;


import java.security.SecureRandom;

public class RandomGenerator {
    private static final int LENGTH_SIX = 6;

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder stringBuilder = new StringBuilder(LENGTH_SIX);
        for (int i = 0; i < LENGTH_SIX; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return stringBuilder.toString();
    }
}
