package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerDto booker;
    private ItemDto item;
    private Long itemId;

    @Data
    @Builder
    public static class BookerDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class ItemDto {
        private Long id;
        private String name;
    }
}
