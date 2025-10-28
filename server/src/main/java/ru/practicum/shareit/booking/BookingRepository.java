package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Основные методы с пагинацией
    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    // Методы с фильтрацией по статусу
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    // Текущие бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId,
                                              @Param("now") LocalDateTime now,
                                              Pageable pageable);

    // Прошлые бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBooker(@Param("bookerId") Long bookerId,
                                           @Param("now") LocalDateTime now,
                                           Pageable pageable);

    // Будущие бронирования пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBooker(@Param("bookerId") Long bookerId,
                                             @Param("now") LocalDateTime now,
                                             Pageable pageable);

    // Текущие бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId,
                                             @Param("now") LocalDateTime now,
                                             Pageable pageable);

    // Прошлые бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(@Param("ownerId") Long ownerId,
                                          @Param("now") LocalDateTime now,
                                          Pageable pageable);

    // Будущие бронирования владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(@Param("ownerId") Long ownerId,
                                            @Param("now") LocalDateTime now,
                                            Pageable pageable);

    // Существующие методы для обратной совместимости
    List<Booking> findByBookerId(Long bookerId, org.springframework.data.domain.Sort sort);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemOwnerId(Long ownerId, org.springframework.data.domain.Sort sort);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :endDate " +
            "AND b.status = 'APPROVED'")
    boolean existsByBookerIdAndItemIdAndEndBefore(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("endDate") LocalDateTime endDate);
}