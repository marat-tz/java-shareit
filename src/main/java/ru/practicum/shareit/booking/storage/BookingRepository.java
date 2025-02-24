package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(Long userId);

    @Query("select b from Booking as b where b.item.id in :itemIds")
    List<Booking> findAllByItemId(@Param("itemIds") List<Long> itemIds);

    List<Booking> findAllByUserIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByUserIdAndEndAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndEndBefore(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndStartAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndStatus(Long userId, Status status);

    List<Booking> findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, Status status);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}
