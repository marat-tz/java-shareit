package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class UserMapper {

    public User mapDtoToUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public User mapDtoToUser(UserDto dto, Long id) {
        return User.builder()
                .id(id)
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    public UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> mapUserToDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(mapUserToDto(user));
        }

        return result;
    }
}
