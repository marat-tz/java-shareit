package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.User;

@Value
@Builder(toBuilder = true)
public class Item {
    @NotNull
    User owner;

    @NotBlank
    String name;

    String description;

    boolean available;
}
