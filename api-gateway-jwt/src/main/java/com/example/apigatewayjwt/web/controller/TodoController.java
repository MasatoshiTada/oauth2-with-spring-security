package com.example.apigatewayjwt.web.controller;

import com.example.apigatewayjwt.web.request.TodoRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final WebClient webClient;

    public TodoController(WebClient webClient) {
        this.webClient = webClient;
    }

    private ResponseEntity<?> createResponse(ClientResponse response) {
        return ResponseEntity.status(response.statusCode())
                .headers(response.headers().asHttpHeaders())
                .body(response.bodyToMono(String.class).block());
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        ClientResponse response = webClient.get()
                .uri("/todos")
                .exchange()
                .block();
        return createResponse(response);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody TodoRequest todoRequest) {
        ClientResponse response = webClient.post()
                .uri("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(todoRequest)
                .exchange()
                .block();
        return createResponse(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateDoneById(@PathVariable Integer id) {
        ClientResponse response = webClient.patch()
                .uri("/todos/{id}", id)
                .exchange()
                .block();
        return createResponse(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        ClientResponse response = webClient.delete()
                .uri("/todos/{id}", id)
                .exchange()
                .block();
        return createResponse(response);
    }
}
