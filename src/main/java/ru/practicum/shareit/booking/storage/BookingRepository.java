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
    List<Booking> findAllByItemIdOrderByStartDesc(@Param("itemIds") List<Long> itemIds);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByUserIdAndItemIdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByUserIdAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByUserIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, Status status);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}
