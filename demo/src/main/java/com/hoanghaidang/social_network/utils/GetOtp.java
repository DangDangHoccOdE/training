package com.hoanghaidang.social_network.utils;

import java.security.SecureRandom;

public class GetOtp {
    public static String generateOtp(int length){
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // random 0-9
        }

        return otp.toString();
    }
}
