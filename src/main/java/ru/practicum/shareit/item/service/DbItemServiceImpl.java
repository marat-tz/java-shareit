package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto dto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.save(ItemMapper.mapDtoToItem(dto, owner));
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    public ItemDto update(ItemDto dto, Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow();

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Обновляемая вещь с id = " + id + " не принадлежит " +
                    "указанному пользователю с id = " + userId);
        }

        // обновляем все поля
        if (!StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() != null) {
            item = ItemMapper.mapNewItemAllFields(item, dto);

            // обновляем имя и описание
        } else if (!StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = ItemMapper.mapNewItemNameDescription(item, dto);

            // обновляем имя
        } else if (!StringUtils.isBlank(dto.getName())
                && StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = ItemMapper.mapNewItemName(item, dto);

            // обновляем доступность
        } else if (StringUtils.isBlank(dto.getName())
                && StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() != null) {
            item = ItemMapper.mapNewItemAvailable(item, dto);

            // обновляем описание
        } else if (StringUtils.isBlank(dto.getName())
                && !StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            item = ItemMapper.mapNewItemDescription(item, dto);

        } else {
            throw new ValidationException("Полученный объект обновляемой вещи не содержит полей");
        }

        return ItemMapper.mapItemToDto(item);
    }

    @Override
    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow();
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    public List<ItemDto> findByOwnerId(Long id) {
        List<Item> items = itemRepository.findByOwnerId(id);
        return ItemMapper.mapItemToDto(items);
    }

    @Override
    public List<ItemDto> findByNameContainingIgnoreCase(String text) {
        return ItemMapper.mapItemToDto(itemRepository.findByNameContainingIgnoreCase(text));
    }

    @Override
    public void delete(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }
}