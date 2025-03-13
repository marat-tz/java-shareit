package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse create(ItemRequestDtoRequest dto, Long userId);

    List<ItemRequestDtoResponse> findUserRequests(Long userId);

    List<ItemRequestDtoResponse> findAllRequests(Long userId);

    ItemRequestDtoResponse findRequestById(Long requestId, Long userId);

}
