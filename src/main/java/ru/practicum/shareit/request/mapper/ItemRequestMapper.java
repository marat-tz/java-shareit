package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemRequestDtoRequest dto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public ItemRequestDtoResponse toDto(ItemRequest request) {
        ItemRequestDtoResponse dto = new ItemRequestDtoResponse();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static List<ItemRequestDtoResponse> toDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDtoResponse> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            dtos.add(toDto(request));
        }
        return dtos;
    }

}
