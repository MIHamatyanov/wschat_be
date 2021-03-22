package ru.marsel.wschat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marsel.wschat.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/long-polling")
public class LongPollingController {
    private static final List<Message> messages = new ArrayList<>();

    @GetMapping
    public ResponseEntity<?> getMessage(@RequestParam(required = false, defaultValue = "0") Long id) throws InterruptedException {
        List<Message> result = messages.stream().filter((m) -> m.getId() > id).collect(Collectors.toList());
        while (result.isEmpty()) {
            Thread.sleep(1000);
            result = messages.stream().filter((m) -> m.getId() > id).collect(Collectors.toList());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        messages.add(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
