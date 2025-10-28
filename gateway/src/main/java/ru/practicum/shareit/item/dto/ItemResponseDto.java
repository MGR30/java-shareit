package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;

    @Setter
    @Getter
    public static class BookingInfoDto {
        private Long id;
        private Long bookerId;

        public BookingInfoDto(Long id, Long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }

    }
}
