package com.najmi.fleetshare.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be at least 12 characters and contain a mix of uppercase, lowercase, numbers, and symbols";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
