package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findByOwnerId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findByText(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> create(ItemDtoRequest dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> addComment(CommentDtoRequest dto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, dto);
    }

    public ResponseEntity<Object> update(ItemDtoRequest dto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, dto);
    }

    public void delete(Long itemId) {
        delete("", itemId);
    }

}
