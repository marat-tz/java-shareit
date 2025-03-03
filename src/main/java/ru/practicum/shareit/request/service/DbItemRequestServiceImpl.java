package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDtoResponse create(ItemRequestDtoRequest dto, Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.save(ItemRequestMapper.toEntity(dto));
        return ItemRequestMapper.toDto(request);
    }

}
