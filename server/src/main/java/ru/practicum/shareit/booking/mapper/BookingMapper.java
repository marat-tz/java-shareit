package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {
    public Booking mapDtoToNewBooking(BookingDtoRequest dto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public BookingDtoResponse mapBookingToDto(Booking booking) {
        BookingDtoResponse dto = new BookingDtoResponse();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booking.getUser());
        dto.setItem(booking.getItem());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static List<BookingDtoResponse> mapBookingToDto(Iterable<Booking> bookings) {
        List<BookingDtoResponse> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapBookingToDto(booking));
        }
        return dtos;
    }
}
