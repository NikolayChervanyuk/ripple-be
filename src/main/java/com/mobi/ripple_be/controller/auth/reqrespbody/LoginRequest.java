package com.mobi.ripple_be.controller.auth.reqrespbody;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Identifier is mandatory")
    @Size(max = 320, message = "Identifier cannot be longer than 320 characters")
    private String identifier;

    @NotNull(message = "Password is mandatory")
    @Size(max = 72, message = "Password cannot be longer than 72 characters. " +
            "This limit is set because bcrypt uses only the first 72 characters anyway")
    @Size(min = 6, message = "Password cannot be shorter than 6 characters")
    private String password;
}
