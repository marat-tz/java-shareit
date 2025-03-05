package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoResponse {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoResponse lastBooking;
    BookingDtoResponse nextBooking;
    List<CommentDtoResponse> comments;
}
