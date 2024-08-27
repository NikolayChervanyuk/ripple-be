package com.mobi.ripple_be.controller.auth.reqrespbody;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @Size(max = 60, message = "Full name cannot be longer than 60 characters")
    private String fullName;

    @NotNull(message = "Username is mandatory")
    @Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
    private String username;

    @NotNull(message = "Email is mandatory")
    @Size(max = 320, message = "Email cannot be longer than 320 characters")
    private String email;

    @NotNull(message = "Password is mandatory")
    @Size(max = 72, message = "Password cannot be longer than 72 characters. " +
            "This limit is set because bcrypt uses only the first 72 characters anyway")
    @Size(min = 6, message = "Password cannot be shorter than 6 characters")
    private String password;
}
