package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public Item addItem(Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        Item item = itemMapper.toItem(itemDto, userId);
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Long userId, ItemDto itemDto) throws AccessDeniedException {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (!item.getOwnerId().equals(userId)) throw new AccessDeniedException("Нет прав для редактирования");

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return itemRepository.save(item);
    }

    public List<Item> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId);
    }

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.search(text);
    }
}
