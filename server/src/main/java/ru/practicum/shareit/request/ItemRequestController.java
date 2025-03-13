package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse create(@Valid @RequestBody ItemRequestDtoRequest dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findRequestById(@PathVariable("requestId") Long requestId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findRequestById(requestId, userId);
    }

}
