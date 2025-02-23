package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(Long userId);
    List<Booking> findAllByUserIdAndEndAfter(Long userId, Instant now);
    List<Booking> findAllByUserIdAndEndBefore(Long userId, Instant now);
    List<Booking> findAllByUserIdAndStartAfter(Long userId, Instant now);
    List<Booking> findAllByUserIdAndStatus(Long userId, Status status);
}
