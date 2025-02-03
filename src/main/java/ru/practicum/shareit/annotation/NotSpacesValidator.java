package ru.practicum.shareit.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotSpacesValidator implements ConstraintValidator<NotSpaces, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(" "); // добавить проверку табуляции и переноса
    }
}
