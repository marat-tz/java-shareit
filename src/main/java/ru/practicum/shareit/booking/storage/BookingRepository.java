package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(Long userId);
    List<Booking> findAllByUserIdAndItemIdAndEndBefore(Long userId, Long ItemId, LocalDateTime now);
    List<Booking> findAllByUserIdAndEndAfter(Long userId, LocalDateTime now);
    List<Booking> findAllByUserIdAndEndBefore(Long userId, LocalDateTime now);
    List<Booking> findAllByUserIdAndStartAfter(Long userId, LocalDateTime now);
    List<Booking> findAllByUserIdAndStatus(Long userId, Status status);
    List<Booking> findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, Status status);
    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}
