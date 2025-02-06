package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.annotation.NotSpaces;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class UserDto {
    Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна соответствовать шаблону name@domain.xx")
    String email;

    @NotBlank(message = "Имя не может быть пустым")
    String name;
}
