package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

public interface ItemRequestService {
    ItemRequestDtoResponse create(ItemRequestDtoRequest dto, Long userId);
}
