package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service("memoryUserService")
public class MemoryUserServiceImpl implements UserService {

    private final UserStorage storage;

    public MemoryUserServiceImpl(@Qualifier("userMemoryStorage") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public UserDto create(UserDto dto) {
        return storage.create(dto);
    }

    @Override
    public UserDto update(UserDto dto, Long id) {
        return storage.update(dto, id);
    }

    @Override
    public void delete(Long id) {
        storage.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return storage.findAll();
    }

    @Override
    public UserDto findById(Long id) {
        return storage.findById(id);
    }
}
