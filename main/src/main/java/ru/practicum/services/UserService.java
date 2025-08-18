package ru.practicum.services;

import ru.practicum.dto.UserDto;
import ru.practicum.entities.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    void deleteUser(Long id);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    User getUser(Long id);
}
