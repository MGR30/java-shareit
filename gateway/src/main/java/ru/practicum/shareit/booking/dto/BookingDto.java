package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    private BookingState state;
    private BookerDto booker;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemDto item;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "ItemId обязателен")
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