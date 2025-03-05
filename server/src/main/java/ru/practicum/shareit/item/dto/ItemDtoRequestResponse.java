package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoRequestResponse {
    Long id;
    String name;
    String description;
    Long ownerId;
    Long requestId;
}
