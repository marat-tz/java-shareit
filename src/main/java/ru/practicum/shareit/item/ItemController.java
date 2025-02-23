package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam("text") String text) {
        return service.findByText(text);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@Valid @RequestBody CommentDtoIn dto, @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.addComment(dto, itemId, userId);
    }

    //Отзывы можно будет увидеть по двум эндпоинтам — по GET /items/{itemId} для одной конкретной вещи
    // и по GET /items для всех вещей данного пользователя.

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto newItem, @PathVariable("id") Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.update(newItem, id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
