package ru.marsel.wschat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marsel.wschat.config.oauth.CustomPrincipal;
import ru.marsel.wschat.model.Chat;
import ru.marsel.wschat.repo.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat createChat(String name, CustomPrincipal auth) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setOwnerId(auth.getId());
        chat = chatRepository.save(chat);
        return chat;
    }

    public void deleteChat(Long id, CustomPrincipal auth) {
        Chat chat = chatRepository.getById(id);
        if (chat.getOwnerId().equals(auth.getId())) {
            chatRepository.delete(chat);
        }
    }

    public Chat getChat(Long id) {
        return chatRepository.getById(id);
    }
}
