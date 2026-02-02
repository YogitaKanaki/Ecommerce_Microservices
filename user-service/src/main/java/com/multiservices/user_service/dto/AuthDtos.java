package com.multiservices.user_service.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record RegisterReq(@Email @NotBlank String email, @NotBlank String password) {}
    public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}
    public record TokenRes(String token) {}
}
