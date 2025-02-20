package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

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
        User user = repository.save(UserMapper.mapDtoToUser(dto, id));
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
        // TODO: настроить выкидывание NotFoundException (если нужно)
        User user = repository.findById(id).orElseThrow();
        return UserMapper.mapUserToDto(user);
    }
}
