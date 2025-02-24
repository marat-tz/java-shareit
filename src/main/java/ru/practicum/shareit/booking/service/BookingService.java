package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.enums.State;

import java.util.List;

public interface BookingService {

    BookingDtoOut create(BookingDtoIn dto, Long userId);

    BookingDtoOut approve(Long bookingId, Boolean status, Long userId);

    BookingDtoOut findById(Long bookingId, Long userId);

    List<BookingDtoOut> findAllByUser(Long userId, State state);
}
