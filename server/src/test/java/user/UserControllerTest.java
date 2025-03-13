package user;

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
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
class UserControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    UserService userService;

    private MockMvc mvc;

    @InjectMocks
    private UserController controller;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(
                1L,
                "test@test.com",
                "test");
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        when(userService.update(any(), anyLong()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void delete_shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .delete(anyLong());
    }

    @Test
    void findById_shouldFindUserById() throws Exception {
        when(userService.findById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void findAll_shouldFindAllUsers() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<UserDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty UserDto list");
                    }
                }));
    }
}
