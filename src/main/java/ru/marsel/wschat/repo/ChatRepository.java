package ru.marsel.wschat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.marsel.wschat.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat getById(Long id);
}
