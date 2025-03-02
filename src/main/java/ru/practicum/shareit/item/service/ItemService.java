package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);

    CommentDtoResponse addComment(CommentDtoRequest dto, Long itemId, Long userId);

    ItemDto update(ItemDto newItem, Long id, Long userId);

    ItemDto findById(Long id);

    List<ItemDto> findByOwnerId(Long id);

    List<ItemDto> findByText(String text);

    void delete(Long id);
}
