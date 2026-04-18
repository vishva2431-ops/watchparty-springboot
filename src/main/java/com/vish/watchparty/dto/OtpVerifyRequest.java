package com.vish.watchparty.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String mobile;
    private String otp;
    private String name;
}