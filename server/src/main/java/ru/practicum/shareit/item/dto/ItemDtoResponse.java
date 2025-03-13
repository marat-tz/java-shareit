package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
