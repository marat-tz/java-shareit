package item;

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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbItemServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    ItemRequestService itemRequestService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;

    static ItemDtoRequest itemDtoRequest1;
    static ItemDtoRequest itemDtoRequest2;

    @BeforeAll
    static void init() {
        userDtoRequest1 = new UserDto(null, "user1@test.com", "user1");
        userDtoRequest2 = new UserDto(null, "user2@test.com", "user2");

        itemDtoRequest1 = new ItemDtoRequest("test1", "description1", true, null);
        itemDtoRequest2 = new ItemDtoRequest("test2", "description2", true, null);
    }

    @Test
    void create_shouldCreateItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());

        Assertions.assertThat(itemDtoResponse.getName()).isEqualTo(itemDtoRequest1.getName());
        Assertions.assertThat(itemDtoResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);

        ItemRequestDtoRequest itemRequestDtoRequest = new ItemRequestDtoRequest("request description");
        ItemRequestDtoResponse itemRequestDtoResponse =
                itemRequestService.create(itemRequestDtoRequest, userDtoResponse2.getId());

        ItemDtoRequest itemDtoRequest = new ItemDtoRequest("name", "desc",
                true, itemRequestDtoResponse.getId());
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest, userDtoResponse1.getId());

        Assertions.assertThat(itemDtoResponse.getName()).isEqualTo(itemDtoRequest.getName());
        Assertions.assertThat(itemDtoResponse.getDescription()).isEqualTo(itemDtoRequest.getDescription());
        Assertions.assertThat(itemDtoResponse.getAvailable()).isEqualTo(itemDtoRequest.getAvailable());
    }

    @Test
    void create_shouldNotCreateItemNotUser() {
        Assertions.assertThatThrownBy(() -> {
            itemService.create(itemDtoRequest1, 1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoCreateResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoUpdateResponse = itemService.update(itemDtoRequest2,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest2.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest2.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest2.getAvailable());
    }

    @Test
    void update_shouldNotUpdateEmptyDto() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoRequest emptyDto = new ItemDtoRequest("", "", null, null);

        ItemDtoResponse itemDtoCreateResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());

        Assertions.assertThatThrownBy(() -> {
            itemService.update(emptyDto, itemDtoCreateResponse.getId(), userDtoResponse.getId());
        }).isInstanceOf(ValidationException.class);
    }

    @Test
    void update_shouldNotUpdateNoItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);

        Assertions.assertThatThrownBy(() -> {
            itemService.update(itemDtoRequest2, 1L, userDtoResponse.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldNotUpdateWrongUser() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);

        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        Assertions.assertThatThrownBy(() -> {
            itemService.update(itemDtoRequest2, itemDtoResponse.getId(), userDtoResponse2.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateOnlyName() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoRequest dtoName = new ItemDtoRequest("new_name", null, null, null);
        ItemDtoResponse itemDtoCreateResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoUpdateResponse = itemService.update(dtoName,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(dtoName.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void update_shouldUpdateOnlyDescription() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoRequest dtoDescription = new ItemDtoRequest(null, "new description", null, null);
        ItemDtoResponse itemDtoCreateResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoUpdateResponse = itemService.update(dtoDescription,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(dtoDescription.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest1.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void update_shouldUpdateOnlyAvailable() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoRequest dtoAvailable = new ItemDtoRequest(null, null, false, null);
        ItemDtoResponse itemDtoCreateResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoUpdateResponse = itemService.update(dtoAvailable,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(dtoAvailable.getAvailable());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest1.getName());
    }

    @Test
    void findById_shouldFindItemById() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponseCreate = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoResponseFind = itemService.findById(itemDtoResponseCreate.getId());

        Assertions.assertThat(itemDtoResponseCreate.getName()).isEqualTo(itemDtoResponseFind.getName());
        Assertions.assertThat(itemDtoResponseCreate.getDescription()).isEqualTo(itemDtoResponseFind.getDescription());
        Assertions.assertThat(itemDtoResponseCreate.getAvailable()).isEqualTo(itemDtoResponseFind.getAvailable());
    }

    @Test
    void findById_shouldNotFindItemById() {
        Assertions.assertThatThrownBy(() -> {
            itemService.findById(1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByOwnerId_shouldFindItemByOwnerId() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponseCreate = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        List<ItemDtoResponse> itemDtoResponseFind = itemService.findByOwnerId(userDtoResponse.getId());

        Assertions.assertThat(itemDtoResponseFind.size()).isEqualTo(1);
    }

    @Test
    void findByText_shouldFindItemByText() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponseCreate1 = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoResponseCreate2 = itemService.create(itemDtoRequest2, userDtoResponse.getId());
        List<ItemDtoResponse> itemDtoResponseName = itemService.findByText(itemDtoRequest1.getName());
        List<ItemDtoResponse> itemDtoResponseDesc = itemService.findByText(itemDtoRequest2.getDescription());

        Assertions.assertThat(itemDtoResponseName.size()).isEqualTo(1);
        Assertions.assertThat(itemDtoResponseDesc.size()).isEqualTo(1);
    }

    @Test
    void delete_shouldDeleteItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        ItemDtoResponse itemDtoFind = itemService.findById(itemDtoResponse.getId());

        Assertions.assertThat(itemDtoResponse.getId()).isEqualTo(itemDtoFind.getId());

        itemService.delete(itemDtoResponse.getId());

        Assertions.assertThatThrownBy(() -> {
            itemService.findById(itemDtoResponse.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void addComment_shouldAddCommentToItem() {
        UserDto ownerDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, ownerDtoResponse.getId());

        UserDto bookerDtoResponse = userService.create(userDtoRequest2);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, bookerDtoResponse.getId());
        bookingService.approve(bookingDtoResponse.getId(), true, ownerDtoResponse.getId());

        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("comment");

        itemService.addComment(commentDtoRequest, itemDtoResponse.getId(), bookerDtoResponse.getId());
        ItemDtoResponse itemWithComment = itemService.findById(itemDtoResponse.getId());

        Assertions.assertThat(itemWithComment.getComments().size()).isEqualTo(1);
    }

    @Test
    void addComment_shouldNotAddCommentNoBooking() {
        UserDto ownerDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, ownerDtoResponse.getId());

        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("comment");

        Assertions.assertThatThrownBy(() -> {
            itemService.addComment(commentDtoRequest, itemDtoResponse.getId(), ownerDtoResponse.getId());
        }).isInstanceOf(ValidationException.class);
    }
}
