package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbUserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        User user = repository.save(UserMapper.mapDtoToUser(dto));
        return UserMapper.mapUserToDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto dto, Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Обновляемый пользователь не найден"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (repository.findByEmail(dto.getEmail()).isEmpty()) {
                user.setEmail(dto.getEmail());
            } else {
                log.error("Указанный email существует - {}", dto.getEmail());
                throw new ConflictException("Указанный email существует");
            }
        }

        repository.save(user);
        return UserMapper.mapUserToDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = repository.getReferenceById(id);
        repository.delete(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        return UserMapper.mapUserToDto(users);
    }

    @Override
    public UserDto findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
        return UserMapper.mapUserToDto(user);
    }
}
