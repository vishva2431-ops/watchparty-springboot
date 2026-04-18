package com.vish.watchparty.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public String generateOtp(String mobile) {
        String otp = "123456";
        otpStore.put(mobile, otp);
        return otp;
    }

    public boolean verifyOtp(String mobile, String otp) {
        return otp.equals(otpStore.get(mobile));
    }
}