package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAllByUserId(userId);
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
