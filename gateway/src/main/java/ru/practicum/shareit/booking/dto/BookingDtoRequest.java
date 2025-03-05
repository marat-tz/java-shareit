package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoRequest {

    @NotNull
    Long itemId;

    @NotNull
    @PastOrPresent // раньше проверки на время не было вообще. возможны ошибки
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;
}
