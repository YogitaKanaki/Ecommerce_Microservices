package com.multiservices.user_service.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record AddressReq(
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String postalCode,
            @NotBlank String country,
            boolean isDefault
    ) {}

    public record RegisterReq(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6) String password,

            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,

            @Valid AddressReq address // optional, can be null
    ) {}

    public record LoginReq(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record TokenRes(String token) {}
}
