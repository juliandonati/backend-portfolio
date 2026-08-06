package com.juliandonati.backendPortafolio.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface AfterStartDate {
    String message() default "La fecha de fin no puede ser anterior a la fecha de inicio";
    // Boilerplate para que Jakarta Validation reconozca la clase como un validador
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
