package com.myfinance.backend.users.entities.security;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
