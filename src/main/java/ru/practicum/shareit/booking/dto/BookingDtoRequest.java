package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.optional.qual.Present;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoRequest {

    @NotNull
    Long itemId;

    @NotNull
    @Present
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;
}
