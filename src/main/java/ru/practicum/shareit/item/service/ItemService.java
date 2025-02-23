package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);
    CommentDtoOut addComment(CommentDtoIn dto, Long itemId, Long userId);
    ItemDto update(ItemDto newItem, Long id, Long userId);
    ItemDto findById(Long id);
    List<ItemDto> findByOwnerId(Long id);
    List<ItemDto> findByText(String text);
    void delete(Long id);
}
