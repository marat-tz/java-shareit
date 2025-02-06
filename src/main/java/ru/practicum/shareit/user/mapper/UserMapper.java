package ru.practicum.shareit.user.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Slf4j
@Component
public class UserMapper {

    public User mapDtoToUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public User mapDtoToUserNewId(UserDto dto, Long id) {
        return User.builder()
                .id(id)
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public User mapDtoToUserUpdate(User user, UserDto dto, Long id) {
        User newUser;

        if (dto.getEmail() != null && dto.getName() != null) {
            newUser = User.builder()
                    .id(id)
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .build();

        } else if (dto.getEmail() == null && dto.getName() != null) {
            newUser = User.builder()
                    .id(id)
                    .email(user.getEmail())
                    .name(dto.getName())
                    .build();

        } else if (dto.getEmail() != null) {
            newUser = User.builder()
                    .id(id)
                    .email(dto.getEmail())
                    .name(user.getName())
                    .build();
        } else {
            log.error("Полученный UserDto не содержит имя и email");
            throw new ValidationException("Полученный UserDto не содержит имя и email");
        }

        return newUser;
    }


    public UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }


}
