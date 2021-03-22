package ru.marsel.wschat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marsel.wschat.model.User;
import ru.marsel.wschat.repo.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByEmail(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        return userOpt.orElse(null);

    }
}
