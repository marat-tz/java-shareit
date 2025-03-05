package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDtoResponse create(ItemDtoRequest dto, Long userId) {
        log.info("Добавление вещи {} пользователем {}", dto.getName(), userId);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " +
                "с id = " + userId + " не найден"));
        ItemRequest request = null;

        if (Objects.nonNull(dto.getRequestId())) {
            request = requestRepository.findById(dto.getRequestId()).orElse(null);
        }

        Item item = itemRepository.save(ItemMapper.mapDtoToItem(dto, owner, request));
        log.info("Вещь {} создана", item);
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(CommentDtoRequest dto, Long itemId, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " +
                "с id = " + userId + " не найден"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id = " +
                itemId + " не найдена"));

        List<Booking> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId,
                LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь " + userId + " не брал в аренду вещь " + itemId);
        }

        Comment comment = commentRepository.save(CommentMapper.mapDtoToComment(dto, owner, item));
        return CommentMapper.mapCommentToDto(comment);
    }

    @Override
    @Transactional
    public ItemDtoResponse update(ItemDtoRequest dto, Long id, Long userId) {
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
    public ItemDtoResponse findById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь " + itemId + " не найдена"));

        BookingDtoResponse lastBooking = null;
        BookingDtoResponse nextBooking = null;

        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        if (!lastBookings.isEmpty()) {
            lastBooking = BookingMapper.mapBookingToDto(lastBookings.get(0));
        }

        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            nextBooking = BookingMapper.mapBookingToDto(nextBookings.get(0));
        }

        List<CommentDtoResponse> comments = CommentMapper.mapCommentToDto(commentRepository.findAllByItemId(itemId));

        return ItemMapper.mapItemToDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoResponse> findByOwnerId(Long id) {
        List<Item> items = itemRepository.findByOwnerId(id);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemIds);
        List<Comment> comments = commentRepository.findAllByItemId(itemIds);

        Map<Long, List<Booking>> bookingsGroup = bookings
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<CommentDtoResponse>> commentsGroup = comments
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::mapCommentToDto, Collectors.toList())));

        return items
                .stream()
                .map(item -> {
                    List<Booking> bookingList = bookingsGroup.getOrDefault(item.getId(), Collections.emptyList());
                    BookingDtoResponse bookingLast = bookingList
                            .stream()
                            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                            .reduce((first, second) -> second)
                            .map(BookingMapper::mapBookingToDto)
                            .orElse(null);
                    BookingDtoResponse bookingNext = bookingList
                            .stream()
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .findFirst()
                            .map(BookingMapper::mapBookingToDto)
                            .orElse(null);

                    return ItemMapper.mapItemToDto(item, bookingLast, bookingNext,
                            commentsGroup.getOrDefault(item.getId(), Collections.emptyList())
                    );
                })
                .toList();
    }

    @Override
    public List<ItemDtoResponse> findByText(String text) {
        log.info("Поиск вещи по тексту: {}", text);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.findByNameContainingIgnoreCase(text);
        items.addAll(itemRepository.findByDescriptionContainingIgnoreCase(text));

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