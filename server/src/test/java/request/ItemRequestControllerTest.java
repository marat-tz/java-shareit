package request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDtoRequestIdResponse;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    ItemRequestService requestService;

    private MockMvc mvc;

    @InjectMocks
    private ItemRequestController controller;

    private UserDto userDto;

    private ItemRequestDtoResponse dtoResponse;

    private ItemRequestDtoRequest dtoRequest;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(1L, "test@test.com", "test");

        ItemDtoRequestIdResponse requestIdResponse = new ItemDtoRequestIdResponse(1L, "name", "desc", 1L, 1L);

        dtoResponse = new ItemRequestDtoResponse(1L, "desc", null, List.of(requestIdResponse));
        dtoRequest = new ItemRequestDtoRequest("desc");
    }

    @Test
    void create_shouldCreateRequest() throws Exception {
        when(requestService.create(any(ItemRequestDtoRequest.class), anyLong()))
                .thenReturn(dtoResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void findRequestById_shouldFindRequestById() throws Exception {
        when(requestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(dtoResponse);

        mvc.perform(get("/requests/" + dtoResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void findAllRequests_shouldFindAllRequests() throws Exception {
        when(requestService.findAllRequests(anyLong()))
                .thenReturn(List.of(dtoResponse));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemRequestDtoResponse> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

    @Test
    void findUserRequests_shouldFindAllUserRequests() throws Exception {
        when(requestService.findUserRequests(anyLong()))
                .thenReturn(List.of(dtoResponse));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemRequestDtoResponse> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

}
