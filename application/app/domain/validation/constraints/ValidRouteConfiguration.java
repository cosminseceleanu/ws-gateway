package domain.validation.constraints;

import domain.validation.validators.ValidRouteConfigurationValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ValidRouteConfigurationValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidRouteConfiguration {
    String message() default "Invalid route configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}