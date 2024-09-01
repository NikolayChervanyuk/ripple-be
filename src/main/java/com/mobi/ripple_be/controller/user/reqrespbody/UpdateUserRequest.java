package com.mobi.ripple_be.controller.user.reqrespbody;

import com.mobi.ripple_be.validator.AppEmail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull
    @Size(max = 60, message = "Full name cannot be longer than 60 characters")
    private String fullName;

    @NotNull(message = "Username is mandatory")
    @Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
    private String username;

    @NotNull
    @AppEmail
    private String email;
    private String bio;
}
