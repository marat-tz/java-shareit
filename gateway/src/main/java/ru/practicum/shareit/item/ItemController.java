package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.dto.ItemDtoRequest;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.findByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return itemClient.findById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam("text") String text) {
        return itemClient.findByText(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDtoRequest dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDtoRequest dto, @PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.addComment(dto, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDtoRequest newItem, @PathVariable("id") Long id,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.update(newItem, id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        itemClient.delete(id);
    }
}
