package request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbItemRequestServiceImplTest {

    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;
    static ItemRequestDtoRequest itemRequestDtoRequest;

    @BeforeAll
    static void init() {
        userDtoRequest1 = new UserDto(null, "user1@test.com", "user1");
        userDtoRequest2 = new UserDto(null, "user2@test.com", "user2");
        itemRequestDtoRequest = new ItemRequestDtoRequest("request description");
    }

    @Test
    void create_shouldCreateItemRequest() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService
                .create(itemRequestDtoRequest, userDtoResponse.getId());

        Assertions.assertThat(itemRequestDtoResponse.getDescription()).isEqualTo(itemRequestDtoRequest.getDescription());
    }

    @Test
    void create_shouldNotCreateNotUser() {
        Assertions.assertThatThrownBy(() ->
                itemRequestService.create(itemRequestDtoRequest, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findUserRequests_shouldFindUserRequests() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemRequestDtoResponse request1 = itemRequestService.create(itemRequestDtoRequest, userDtoResponse.getId());
        ItemRequestDtoResponse request2 = itemRequestService.create(itemRequestDtoRequest, userDtoResponse.getId());
        ItemRequestDtoResponse request3 = itemRequestService.create(itemRequestDtoRequest, userDtoResponse.getId());

        List<ItemRequestDtoResponse> requests = itemRequestService.findUserRequests(userDtoResponse.getId());

        Assertions.assertThat(requests.size()).isEqualTo(3);
        Assertions.assertThat(requests.get(0).getId()).isEqualTo(request3.getId());
        Assertions.assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        Assertions.assertThat(requests.get(2).getId()).isEqualTo(request1.getId());
    }

    @Test
    void findUserRequests_shouldNotFindNotUser() {
        Assertions.assertThatThrownBy(() ->
                itemRequestService.findUserRequests(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllRequests_shouldFindAllRequestsExceptOwn() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        itemRequestService.create(itemRequestDtoRequest, userDtoResponse1.getId());
        itemRequestService.create(itemRequestDtoRequest, userDtoResponse1.getId());
        itemRequestService.create(itemRequestDtoRequest, userDtoResponse2.getId());
        itemRequestService.create(itemRequestDtoRequest, userDtoResponse2.getId());

        List<ItemRequestDtoResponse> requests = itemRequestService.findAllRequests(userDtoResponse2.getId());

        Assertions.assertThat(requests.size()).isEqualTo(2);
    }

    @Test
    void findRequestById_shouldFindRequestById() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService
                .create(itemRequestDtoRequest, userDtoResponse.getId());

        ItemRequestDtoResponse findItemResponse = itemRequestService
                .findRequestById(itemRequestDtoResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(findItemResponse.getDescription()).isEqualTo(itemRequestDtoRequest.getDescription());
    }

    @Test
    void findRequestById_shouldNotFindRequestByIdNoUser() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService
                .create(itemRequestDtoRequest, userDtoResponse.getId());

        Assertions.assertThatThrownBy(() ->
                itemRequestService.findRequestById(itemRequestDtoResponse.getId(), 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findRequestById_shouldNotFindRequestByIdNoRequest() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                itemRequestService.findRequestById(2L, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

}
