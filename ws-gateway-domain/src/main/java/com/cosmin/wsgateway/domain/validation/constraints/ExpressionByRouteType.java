package com.cosmin.wsgateway.domain.validation.constraints;

import com.cosmin.wsgateway.domain.validation.validators.ExpressionByRouteTypeValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ExpressionByRouteTypeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExpressionByRouteType {
    String message() default "Invalid route expression";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
