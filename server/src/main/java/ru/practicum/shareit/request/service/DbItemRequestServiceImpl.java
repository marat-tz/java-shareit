package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoResponse create(ItemRequestDtoRequest dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.save(ItemRequestMapper.toEntity(dto, user));
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDtoResponse> findUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByOwnerIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> allItems = itemRepository.findAllByRequestId(requestIds);

        return ItemRequestMapper.toDto(requests, allItems);
    }

    @Override
    public List<ItemRequestDtoResponse> findAllRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByOwnerIdNotOrderByCreatedDesc(userId);
        return ItemRequestMapper.toDto(itemRequests);
    }


    @Override
    public ItemRequestDtoResponse findRequestById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));

        List<Item> allItems = itemRepository.findAllByRequestId(List.of(requestId));

        return ItemRequestMapper.toDto(itemRequest, allItems);
    }

}
