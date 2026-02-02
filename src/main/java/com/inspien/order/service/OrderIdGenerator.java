package com.inspien.order.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;



@Component
public class OrderIdGenerator {

    private static final SecureRandom RND = new SecureRandom();
    public String nextOrderId() {

        char letter = (char) ('A' + RND.nextInt(26));
        int num = RND.nextInt(1000);
        return letter + String.format("%03d", num);
    }

}
