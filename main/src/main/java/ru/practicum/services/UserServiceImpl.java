package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.UserDto;
import ru.practicum.entities.User;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        Optional<User> userInDb = userRepository.findByEmail(user.getEmail());

        if (userInDb.isPresent()) {
            throw new ConflictException("Email is already in use");
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", id)));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<User> users = userRepository.findByIds(ids, from, size);
        return users.stream().map(UserMapper::toDto).toList();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %d is not found", id)));
    }
}
