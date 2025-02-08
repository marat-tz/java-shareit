package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("itemMemoryStorage")
public class MemoryItemStorageImpl implements ItemStorage {

    @Autowired
    private UserService userService;
    private Map<Long, Item> items = new HashMap<>();
    private long itemId = 1;

    private long generateId() {
        return itemId++;
    }

    @Override
    public Item create(Item item, Long userId) {
        User user = UserMapper.mapDtoToUser(userService.findById(userId));

        long id = generateId();
        item = Item.builder()
                .id(id)
                .owner(user)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item newItem, Long id) {
        items.put(id, newItem);
        return newItem;
    }

    @Override
    public Item findById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь с id = " + id + " не существует");
        }

        return items.get(id);
    }

    @Override
    public List<Item> findAllByUserId(Long id) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), id))
                .toList();
    }

    @Override
    public List<Item> findByText(String text) {
        return items.values()
                .stream()
                .filter(item -> !text.isBlank() &&
                        item.getName().toUpperCase().contains(text)
                        && Objects.equals(item.getAvailable(), true))
                .toList();
    }

    @Override
    public void delete(Long id) {

    }
}
