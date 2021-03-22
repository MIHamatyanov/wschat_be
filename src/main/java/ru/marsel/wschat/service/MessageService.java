package ru.marsel.wschat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marsel.wschat.controller.WebsocketController;
import ru.marsel.wschat.model.Message;
import ru.marsel.wschat.repo.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void save(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getChatHistory(Long id) {
        List<Message> chatMessages = messageRepository.getAllByChatId(id);
        chatMessages.forEach(WebsocketController::fillMeta);
        return chatMessages;
    }
}
