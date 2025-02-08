package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto newItem, Long id, Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findAllByUserId(Long id);

    // TODO: только доступные для аренды вещи
    List<ItemDto> findByText(String text);

    void delete(Long id);
}
