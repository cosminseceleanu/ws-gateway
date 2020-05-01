package com.cosmin.wsgateway.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Value;
import org.hibernate.validator.constraints.URL;


public interface Authentication {
    class None implements Authentication {

    }

    @Value
    class Basic implements Authentication {
        @NotBlank
        @Size(min = 5, max = 255)
        private final String username;

        @NotBlank
        @Size(min = 5, max = 255)
        private final String password;
    }

    @Value
    class Bearer implements Authentication {
        @NotNull
        @URL
        private final String authorizationServerUrl;
    }
}
