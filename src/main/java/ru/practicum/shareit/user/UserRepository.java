package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public User save(User user);

    public Optional<User> findById(Long id);

    public List<User> findAll();

    public void deleteById(Long id);

    boolean existsByEmail(String email);
}
