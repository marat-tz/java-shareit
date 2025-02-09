package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.Objects;

@Service
public class MemoryItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    public MemoryItemServiceImpl(@Qualifier("itemMemoryStorage") ItemStorage storage) {
        this.storage = storage;
    }

    @Override
    public ItemDto create(ItemDto dto, Long userId) {
        Item item = storage.create(ItemMapper.mapDtoToItem(dto), userId);
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    public ItemDto update(ItemDto dto, Long id, Long userId) {
        Item item = storage.findById(id);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Обновляемая вещь с id = " + id + " не принадлежит " +
                    "указанному пользователю с id = " + userId);
        }

        // обновляем все поля
        if (!StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() != null) {
            item = Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .available(dto.getAvailable())
                    .build();

        // обновляем имя и описание
        } else if (!StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .available(item.getAvailable())
                    .build();

        // обновляем имя
        } else if (!StringUtils.isBlank(dto.getName())
                && StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(dto.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build();

        // обновляем доступность
        } else if (StringUtils.isBlank(dto.getName())
                && StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() != null) {
            item = Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(dto.getAvailable())
                    .build();

        // обновляем описание
        } else if (StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = Item.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(dto.getDescription())
                    .available(item.getAvailable())
                    .build();

        } else {
            throw new ValidationException("Полученный объект обновляемой вещи не содержит полей");
        }

        return ItemMapper.mapItemToDto(storage.update(item, id));
    }

    @Override
    public ItemDto findById(Long id) {
        return ItemMapper.mapItemToDto(storage.findById(id));
    }

    @Override
    public List<ItemDto> findAllByUserId(Long id) {
        return storage.findAllByUserId(id)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .toList();
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return storage.findByText(text)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        storage.delete(id);
    }
}
