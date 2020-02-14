package domain.validation.constraints;

import domain.validation.validators.ExpressionByRouteTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ExpressionByRouteTypeValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExpressionByRouteType {
    String message() default "Invalid route expression";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}