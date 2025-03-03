package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoResponse {
    Long id;
    String description;
    LocalDateTime created;
}
