package booking;

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
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbBookingServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

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
    void create_shouldCreateBooking() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse2.getId());

        Assertions.assertThat(bookingDtoResponse.getItem().getId()).isEqualTo(bookingDtoRequest.getItemId());
        Assertions.assertThat(bookingDtoResponse.getBooker().getId()).isEqualTo(userDtoResponse2.getId());
        Assertions.assertThat(bookingDtoResponse.getStart()).isEqualTo(bookingDtoRequest.getStart());
        Assertions.assertThat(bookingDtoResponse.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
    }

    @Test
    void create_shouldNotCreateBookingNoUser() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                bookingService.create(bookingDtoRequest, 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldNotCreateBookingNoItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(2L,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                bookingService.create(bookingDtoRequest, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldNotCreateBookingNotAvailableItem() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoRequest itemDtoNotAvailable =
                new ItemDtoRequest("test1", "description1", false, null);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoNotAvailable, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                bookingService.create(bookingDtoRequest, userDtoResponse.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void approve_shouldApproveBooking() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse2.getId());

        BookingDtoResponse bookingApproveDto = bookingService.approve(bookingDtoResponse.getId(),
                true, userDtoResponse1.getId());

        Assertions.assertThat(bookingApproveDto.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approve_shouldRejectBooking() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse2.getId());

        BookingDtoResponse bookingApproveDto = bookingService.approve(bookingDtoResponse.getId(),
                false, userDtoResponse1.getId());

        Assertions.assertThat(bookingApproveDto.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void approve_shouldNotApproveNoBooking() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                bookingService.approve(1L, true, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approve_shouldNotApproveNoOwner() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse2.getId());

        Assertions.assertThatThrownBy(() ->
                bookingService.approve(bookingDtoResponse.getId(), true, userDtoResponse2.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void findById_shouldFindBookingById() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse2.getId());

        BookingDtoResponse findBookingDto = bookingService.findById(bookingDtoResponse.getId(), userDtoResponse2.getId());

        Assertions.assertThat(bookingDtoResponse.getItem().getId()).isEqualTo(findBookingDto.getItem().getId());
        Assertions.assertThat(bookingDtoResponse.getStart()).isEqualTo(findBookingDto.getStart());
        Assertions.assertThat(bookingDtoResponse.getEnd()).isEqualTo(findBookingDto.getEnd());
    }

    @Test
    void findById_shouldNotFindBookingByIdNoUser() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDtoResponse bookingDtoResponse = bookingService.create(bookingDtoRequest, userDtoResponse.getId());

        Assertions.assertThatThrownBy(() ->
                bookingService.findById(bookingDtoResponse.getId(), 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldNotFindBookingByIdNoBooking() {
        UserDto userDtoResponse = userService.create(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                bookingService.findById(2L, userDtoResponse.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByUser_shouldFindAllCurrentBookings() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusMinutes(10));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9));

        bookingService.create(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDtoResponse> bookings = bookingService.findAllByUser(userDtoResponse2.getId(), State.CURRENT);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void findAllByUser_shouldFindAllPastBookings() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusMinutes(110));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(50));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(11));

        bookingService.create(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDtoResponse> bookings = bookingService.findAllByUser(userDtoResponse2.getId(), State.PAST);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void findAllByUser_shouldFindAllFutureBookings() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9));

        bookingService.create(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDtoResponse> bookings = bookingService.findAllByUser(userDtoResponse2.getId(), State.FUTURE);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void findAllByUser_shouldFindAllBookings() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(185));

        bookingService.create(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDtoResponse> bookings = bookingService.findAllByUser(userDtoResponse2.getId(), State.ALL);

        Assertions.assertThat(bookings.size()).isEqualTo(3);
    }

    @Test
    void findAllByUser_shouldNotFindNoUser() {
        Assertions.assertThatThrownBy(() ->
                bookingService.findAllByUser(1L, State.ALL)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByUserItems_shouldFindAllByItems() {
        UserDto userDtoResponse1 = userService.create(userDtoRequest1);
        UserDto userDtoResponse2 = userService.create(userDtoRequest2);
        ItemDtoResponse itemDtoResponse1 = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        ItemDtoResponse itemDtoResponse2 = itemService.create(itemDtoRequest1, userDtoResponse1.getId());
        ItemDtoResponse itemDtoResponse3 = itemService.create(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse1.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse2.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse3.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(185));

        bookingService.create(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.create(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDtoResponse> bookings = bookingService.findAllByUserItems(userDtoResponse1.getId(), State.ALL);

        Assertions.assertThat(bookings.size()).isEqualTo(3);
    }

    @Test
    void findAllByUserItems_shouldNotFindNoUser() {
        Assertions.assertThatThrownBy(() ->
                bookingService.findAllByUserItems(1L, State.ALL)).isInstanceOf(NotFoundException.class);
    }

}
