package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemResponseDto toResponseDto(Item item) {
        List<Booking> bookings = bookingRepository.findByItemId(item.getId());
        LocalDateTime now = LocalDateTime.now();

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(getLastBooking(bookings, now))
                .nextBooking(getNextBooking(bookings, now))
                .comments(commentRepository.findByItemId(item.getId()).stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ItemResponseDto.BookingInfoDto getLastBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(b -> !b.getStart().isAfter(now))
                .filter(b -> b.getEnd().isAfter(now))
                .max(Comparator.comparing(Booking::getEnd))
                .map(b -> new ItemResponseDto.BookingInfoDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    private ItemResponseDto.BookingInfoDto getNextBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(b -> new ItemResponseDto.BookingInfoDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto, Long userId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(User.builder().id(userId).build())
                .build();
    }
}
