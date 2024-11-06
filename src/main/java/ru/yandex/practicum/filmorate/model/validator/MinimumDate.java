package ru.yandex.practicum.filmorate.model.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
public @interface MinimumDate {
    String message() default "Дата должна быть не раньше {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "";
}
