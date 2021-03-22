package ru.marsel.wschat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.marsel.wschat.config.oauth.CustomPrincipal;
import ru.marsel.wschat.model.Chat;
import ru.marsel.wschat.model.Message;
import ru.marsel.wschat.service.ChatService;
import ru.marsel.wschat.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Chat>> getChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody String name, @AuthenticationPrincipal CustomPrincipal auth) {
        return ResponseEntity.ok(chatService.createChat(name, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Chat> deleteChat(@PathVariable Long id, @AuthenticationPrincipal CustomPrincipal auth) {
        chatService.deleteChat(id, auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/message")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getChatHistory(id));
    }
}
