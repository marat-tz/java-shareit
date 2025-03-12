package item;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;
    private UserDto userDto;
    private ItemDtoResponse itemDtoResponse;
    private ItemDtoRequest itemDtoRequest;
    private Item item;
    private CommentDtoResponse commentDtoResponse;
    private CommentDtoRequest commentDtoRequest;


    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        userDto = new UserDto(1L, "test@test.com", "test");

        itemDtoRequest = new ItemDtoRequest("name", "desc", true, null);

        itemDtoResponse = new ItemDtoResponse(1L, "name", "desc", true,
                null, null, null);

        User user = new User(1L, "email@mail.com", "name");
        item = new Item(1L, user, "name", "desc", true, null);

        commentDtoRequest = new CommentDtoRequest("desc");

        commentDtoResponse = new CommentDtoResponse(1L, "text", item, "name", now);
    }

    @Test
    void create_shouldCreateItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void update_shouldUpdateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/" + itemDtoResponse.getId())
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void findById_shouldFindItemById() throws Exception {
        when(itemService.findById(anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(get("/items/" + itemDtoResponse.getId())
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void findByOwnerId_shouldFindItemByOwnerId() throws Exception {
        when(itemService.findByOwnerId(anyLong()))
                .thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemDtoResponse> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

    @Test
    void findByText_shouldFindItemByText() throws Exception {
        when(itemService.findByText(anyString()))
                .thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items/search?text=" + anyString())
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemDtoResponse> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

    @Test
    void addComment_shouldAddComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDtoResponse);

        mvc.perform(post("/items/" + itemDtoResponse.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.item").value(item))
                .andExpect(jsonPath("$.authorName").value("name"));
    }

    @Test
    void delete_shouldDeleteItem() throws Exception {
        mvc.perform(delete("/items/" + itemDtoResponse.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .delete(anyLong());
    }

}
