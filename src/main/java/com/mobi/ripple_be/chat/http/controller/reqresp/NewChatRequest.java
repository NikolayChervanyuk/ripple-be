package com.mobi.ripple_be.chat.http.controller.reqresp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewChatRequest {

    @Length(min = 1, max = 80)
    private String name;

    @Size(min = 1, max = 300)
    private List<String> userToAddIds;
}
