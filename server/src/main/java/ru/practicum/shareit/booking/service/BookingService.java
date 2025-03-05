package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.enums.State;

import java.util.List;

public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest dto, Long userId);

    BookingDtoResponse approve(Long bookingId, Boolean status, Long userId);

    BookingDtoResponse findById(Long bookingId, Long userId);

    List<BookingDtoResponse> findAllByUser(Long userId, State state);

    List<BookingDtoResponse> findAllByUserItems(Long userId, State state);
}
