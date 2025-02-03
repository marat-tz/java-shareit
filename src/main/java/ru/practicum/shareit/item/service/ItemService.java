package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item);

    ItemDto update(ItemDto item, UserDto user);

    ItemDto findById(Long id);

    // TODO: должен возращаться список всех вещей конкретного пользователя по эндпоинту /items (id юзера в заголовке)
    List<ItemDto> findAll();

    // TODO: только доступные для аренды вещи
    List<ItemDto> findByText(String text);
}
