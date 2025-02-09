package ru.practicum.shareit.item.mapper;

import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public Item mapDtoToItem(ItemDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDto mapItemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item mapNewItemAllFields(Item item, ItemDto dto) {
        return Item.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public Item mapNewItemNameDescription(Item item, ItemDto dto) {
        return Item.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item mapNewItemName(Item item, ItemDto dto) {
        return Item.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(dto.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item mapNewItemAvailable(Item item, ItemDto dto) {
            return Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(dto.getAvailable())
                    .build();
        }
    public Item mapNewItemDescription(Item item, ItemDto dto) {
            return Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(dto.getDescription())
                    .available(item.getAvailable())
                    .build();
        }
}
