package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(UserDto user, Long id);

    void delete(Long id);

    List<UserDto> findAll();

    UserDto findById(Long id);
}
