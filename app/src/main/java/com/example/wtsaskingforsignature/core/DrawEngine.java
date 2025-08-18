package com.example.wtsaskingforsignature.core;

import java.security.SecureRandom;

public final class DrawEngine {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private DrawEngine() {}

    public static int draw(int minInclusive, int maxInclusive) {
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive > maxInclusive");
        }
        int bound = (maxInclusive - minInclusive) + 1;
        int n = SECURE_RANDOM.nextInt(bound);
        return minInclusive + n;
    }
}


