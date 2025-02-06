package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    UserDto create(UserDto user);

    UserDto update(UserDto user, Long id);

    void delete(Long id);

    List<UserDto> findAll();

    UserDto findById(Long id);
}
