package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userMemoryStorage")
public class MemoryUserStorageImpl implements UserStorage {

    @Autowired
    private UserMapper mapper;
    private long userId = 1;
    private Map<Long, User> users = new HashMap<>();

    private long generateId() {
        return userId++;
    }

    private void emailExistCheck(UserDto dto) {
        log.info("Метод emailExistCheck, объект {}", dto);
            users.values().forEach(mapUser -> {
                        if (mapUser.getEmail().equals(dto.getEmail())) {
                            log.error("Пользователь с указанным email = {} уже существует", dto.getEmail());
                            throw new ConflictException("Пользователь с указанным email существует");
                        }
                    }
            );
    }

    @Override
    public UserDto create(UserDto dto) {
        emailExistCheck(dto);

        long id = generateId();
        User user = mapper.mapDtoToUserNewId(dto, id);

        users.put(id, user);
        return mapper.mapUserToDto(user);
    }

    @Override
    public UserDto update(UserDto dto, Long id) {
        if (dto.getEmail() != null) {
            emailExistCheck(dto);
        }

        if (!users.containsKey(id)) {
            log.error("Обновляемый пользователь не существует");
            throw new NotFoundException("Обновляемый пользователь не существует");
        }

        User user = mapper.mapDtoToUserUpdate(users.get(id), dto, id);
        users.put(id, user);

        return mapper.mapUserToDto(user);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<UserDto> findAll() {
        return users.values()
                .stream()
                .map(user -> mapper.mapUserToDto(user))
                .toList();
    }

    @Override
    public UserDto findById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует");
        }
        return mapper.mapUserToDto(users.get(id));
    }
}
