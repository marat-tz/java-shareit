package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class BookingMapper {
    public Booking mapDtoToNewBooking(BookingDtoIn dto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public BookingDtoOut mapBookingToDto(Booking booking) {
        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booking.getUser());
        dto.setItem(booking.getItem());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static List<BookingDtoOut> mapBookingToDto(Iterable<Booking> bookings) {
        List<BookingDtoOut> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapBookingToDto(booking));
        }
        return dtos;
    }
}
