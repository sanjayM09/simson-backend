package com.example.kkBazar.entity.admin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
@Component
public class OtpStore {
    private Map<String, OtpDetails> otpStore = new HashMap<>();

    public void storeOtp(String email, String otp) {
        otpStore.put(email, new OtpDetails(otp, LocalDateTime.now()));
    }

    public OtpDetails getOtpDetails(String email) {
        return otpStore.get(email);
    }

    public void removeOtp(String email) {
        otpStore.remove(email);
    }

    public static class OtpDetails {
        private String otp;
        private LocalDateTime timestamp;

        public OtpDetails(String otp, LocalDateTime timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
