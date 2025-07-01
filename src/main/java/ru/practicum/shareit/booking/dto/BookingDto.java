package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Дата начала обязательна")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания обязательна")
    private LocalDateTime end;

    private BookingStatus status;
    private BookerDto booker;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemDto item;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
