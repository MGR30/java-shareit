package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto createBooking(Long userId, BookingDto bookingDto) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Long itemId = bookingDto.getItemId();
        if (itemId == null) {
            throw new ValidationException("ID вещи не указан");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        validateBooking(bookingDto, user, item);

        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) throws AccessDeniedException {
        if (!userRepository.existsById(userId)) {
            throw new AccessDeniedException("Пользователь не найден");
        }

        User user = userRepository.findById(userId).orElse(null);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже было обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);
    }

    public BookingDto getBookingById(Long userId, Long bookingId) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }

        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return filterBookings(bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start")), state);
    }

    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return filterBookings(bookingRepository.findByItemOwnerId(userId, Sort.by(Sort.Direction.DESC, "start")), state);
    }

    private void validateBooking(BookingDto bookingDto, User user, Item item) throws AccessDeniedException {
        if (item.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Владелец не может бронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Даты начала и окончания не могут совпадать");
        }
    }

    private List<BookingDto> filterBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());

        return switch (bookingState) {
            case CURRENT -> bookings.stream()
                    .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
            case PAST -> bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
            case FUTURE -> bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
            case WAITING -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
            case REJECTED -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
            default -> // ALL
                    bookings.stream()
                            .map(BookingMapper::toDto)
                            .collect(Collectors.toList());
        };
    }

    private enum BookingState {
        ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
    }
}
