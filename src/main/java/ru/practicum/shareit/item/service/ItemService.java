package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

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
