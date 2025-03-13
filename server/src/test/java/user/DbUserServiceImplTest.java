package user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbUserServiceImplTest {

    @Autowired
    UserService userService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;

    @BeforeAll
    static void initUsers() {
        userDtoRequest1 = new UserDto(null, "user1@test.com", "user1");
        userDtoRequest2 = new UserDto(null, "user2@test.com", "user2");
    }

    @Test
    void create_shouldCreateUser() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);

        Assertions.assertThat(userDtoResponse1.getEmail()).isEqualTo(userDtoRequest1.getEmail());
        Assertions.assertThat(userDtoResponse1.getName()).isEqualTo(userDtoRequest1.getName());
    }

    @Test
    void update_shouldUpdateUser() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.update(userDtoRequest2, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest2.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest2.getName());
    }

    @Test
    void update_shouldNotUpdateUserEmailConflict() {
        userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);

        UserDto updateDto = new UserDto(null, userDtoRequest1.getEmail(), "test");

        Assertions.assertThatThrownBy(() -> {
            userService.update(updateDto, userDtoResponse2.getId());
        }).isInstanceOf(ConflictException.class);
    }

    @Test
    void update_shouldNotUpdateNotUser() {
        UserDto updateDto = new UserDto(null, userDtoRequest1.getEmail(), "test");

        Assertions.assertThatThrownBy(() -> {
            userService.update(updateDto, 1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldNotUpdateNameIfNullName() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoRequestNullName = new UserDto(null, "user2@test.com", null);
        UserDto userDtoResponse2 = userService.update(userDtoRequestNullName, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest2.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest1.getName());
    }

    @Test
    void update_shouldNotUpdateNameIfNullEmail() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoRequestNullName = new UserDto(null, null, "user2");
        UserDto userDtoResponse2 = userService.update(userDtoRequestNullName, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest1.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest2.getName());
    }

    @Test
    void delete_shouldDeleteUser() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto findUserDto = userService.findById(userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse1.getId()).isEqualTo(findUserDto.getId());

        userService.delete(userDtoResponse1.getId());

        Assertions.assertThatThrownBy(() -> {
            userService.findById(userDtoResponse1.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldFindUserById() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto findUserResponse = userService.findById(userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse1.getId()).isEqualTo(findUserResponse.getId());
        Assertions.assertThat(userDtoResponse1.getEmail()).isEqualTo(findUserResponse.getEmail());
        Assertions.assertThat(userDtoResponse1.getName()).isEqualTo(findUserResponse.getName());
    }

    @Test
    void findAll_shouldFindAllUsers() {
        userService.create(userDtoRequest1);
        userService.create(userDtoRequest2);
        List<UserDto> usersList = userService.findAll();

        Assertions.assertThat(usersList.size()).isEqualTo(2);
    }

}
