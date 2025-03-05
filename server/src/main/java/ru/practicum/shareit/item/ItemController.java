package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<ItemDtoResponse> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> findByText(@RequestParam("text") String text) {
        return service.findByText(text);
    }

    @PostMapping
    public ItemDtoResponse create(@Valid @RequestBody ItemDtoRequest dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@Valid @RequestBody CommentDtoRequest dto, @PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.addComment(dto, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse update(@RequestBody ItemDtoRequest newItem, @PathVariable("id") Long id,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.update(newItem, id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
