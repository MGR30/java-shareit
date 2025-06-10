package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Email уже используется");
        }
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + id));

        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);

        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(userDto.getEmail())) {
                    throw new DuplicateEmailException("Email уже используется");
                }
                user.setEmail(userDto.getEmail());
            }
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
