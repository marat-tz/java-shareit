package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDtoRequestIdResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemRequestDtoRequest dto, User owner) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setOwner(owner);
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

    public ItemRequestDtoResponse toDto(ItemRequest request, List<Item> items) {
        log.info("МАППЕР, request = {}, items = {}", request, items);
        List<ItemDtoRequestIdResponse> resultItems = ItemMapper.mapItemToDtoRequest(
                items.stream().filter(item -> Objects.equals(item.getRequest(), request)).toList());
        ItemRequestDtoResponse dto = new ItemRequestDtoResponse();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(resultItems);
        return dto;
    }

    public static List<ItemRequestDtoResponse> toDto(Iterable<ItemRequest> requests, List<Item> items) {
        List<ItemRequestDtoResponse> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> requestItems = items.stream().filter(item -> item.getRequest().equals(request)).toList();
            dtos.add(toDto(request, requestItems));
        }
        return dtos;
    }

}
