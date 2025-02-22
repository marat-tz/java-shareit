package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto dto, Long userId) {
        log.info("Добавление вещи {} пользователем {}", dto.getName(), userId);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " +
                "с id = " + userId + " не найден"));
        Item item = itemRepository.save(ItemMapper.mapDtoToItem(dto, owner));
        log.info("Вещь {} создана", item);
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto dto, Long id, Long userId) {
        log.info("Обновление вещи с id = {}, пользователь = {}, dto.name = {}, dto.description = {}, " +
                "dto.available = {}", id, userId, dto.getName(), dto.getDescription(), dto.getAvailable());

        if (StringUtils.isBlank(dto.getName()) && StringUtils.isBlank(dto.getDescription())
                && dto.getAvailable() == null) {
            throw new ValidationException("Полученный DTO обновляемой вещи не содержит полей");
        }

        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь с id = " +
                id + " не найдена"));
        log.info("Текущие поля обновляемой вещи: item.name = {}, item.description = {}, item.available = {}",
                item.getName(), item.getDescription(), item.getAvailable());

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Обновляемая вещь с id = " + id + " не принадлежит " +
                    "указанному пользователю с id = " + userId);
        }

        if (!StringUtils.isBlank(dto.getName())) {
            item.setName(dto.getName());
        }

        if (!StringUtils.isBlank(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
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
    public List<ItemDto> findByText(String text) {
        log.info("Поиск вещи по тексту: {}", text);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.findByNameContainingIgnoreCase(text);
        items.addAll(itemRepository.findByDescriptionContainingIgnoreCase(text));

        // TODO: можно отсеять всё ещё на этапе запроса
        List<Item> result = items
                .stream()
                .filter(Item::getAvailable)
                .toList();

        return ItemMapper.mapItemToDto(result);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }
}