package com.harshita.urlshortner.utils;

public class Base62Encoder {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(long value) {

        if (value == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();

        while (value > 0) {

            int remainder = (int) (value % 62);

            encoded.append(
                    CHARACTERS.charAt(remainder));

            value = value / 62;
        }

        return encoded.reverse().toString();
    }
}