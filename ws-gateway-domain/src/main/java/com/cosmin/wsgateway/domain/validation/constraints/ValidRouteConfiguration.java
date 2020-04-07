package com.cosmin.wsgateway.domain.validation.constraints;

import com.cosmin.wsgateway.domain.validation.validators.ValidRouteConfigurationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidRouteConfigurationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidRouteConfiguration {
    String message() default "Invalid route configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
