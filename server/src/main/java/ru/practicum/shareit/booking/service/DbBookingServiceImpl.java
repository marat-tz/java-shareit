package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbBookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse create(BookingDtoRequest dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь " + userId + " не найден"));

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(()
                -> new NotFoundException("Вещь " + dto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь " + item.getId() + " недоступна для бронирования");
        }

        Booking booking = bookingRepository.save(BookingMapper.mapDtoToNewBooking(dto, user, item));
        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public BookingDtoResponse approve(Long bookingId, Boolean status, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        Long ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(ownerId, userId)) {
            throw new ValidationException("Пользователь " + userId + " не является владельцем вещи " +
                    booking.getItem().getId());
        }

        if (!Objects.equals(booking.getStatus(), Status.WAITING)) {
            throw new ValidationException("Бронирование подтверждено или отклонено");
        }

        if (status) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public BookingDtoResponse findById(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронирование " + bookingId + " не найдено"));
        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public List<BookingDtoResponse> findAllByUser(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> result =  switch (state) {
            case CURRENT -> bookingRepository.findAllByUserIdAndEndAfterOrderByStartDesc(userId, now);
            case PAST -> bookingRepository.findAllByUserIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByUserIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findAllByUserId(userId);
        };

        return BookingMapper.mapBookingToDto(result);
    }

    @Override
    public List<BookingDtoResponse> findAllByUserItems(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        List<Booking> bookings;

        Status status = switch (state) {
            case CURRENT -> Status.CURRENT;
            case PAST -> Status.PAST;
            case FUTURE -> Status.FUTURE;
            case WAITING -> Status.WAITING;
            case REJECTED -> Status.REJECTED;
            default -> null;
        };

        if (status == null) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        } else {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, status);
        }

        return bookings
                .stream()
                .map(BookingMapper::mapBookingToDto)
                .collect(Collectors.toList());
    }
}
