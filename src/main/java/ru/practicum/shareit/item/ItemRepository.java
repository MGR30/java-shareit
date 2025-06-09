package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    public Item save(Item item);

    public Optional<Item> findById(Long id);

    public List<Item> findAllByOwnerId(Long ownerId);

    public List<Item> search(String text);
}
