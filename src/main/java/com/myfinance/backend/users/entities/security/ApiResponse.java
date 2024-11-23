package com.myfinance.backend.users.entities.security;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
