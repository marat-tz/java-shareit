package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDtoResponse create(ItemDtoRequest item, Long userId);

    CommentDtoResponse addComment(CommentDtoRequest dto, Long itemId, Long userId);

    ItemDtoResponse update(ItemDtoRequest newItem, Long id, Long userId);

    ItemDtoResponse findById(Long id);

    List<ItemDtoResponse> findByOwnerId(Long id);

    List<ItemDtoResponse> findByText(String text);

    void delete(Long id);
}
