package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDtoRequest dto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.create(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.findUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable("requestId") Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.findRequestById(requestId, userId);
    }

}
