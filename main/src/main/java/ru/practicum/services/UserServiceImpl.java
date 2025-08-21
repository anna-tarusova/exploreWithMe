package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.entities.User;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repositories.UserRepository;
import ru.practicum.specifications.UserSpecification;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(User user) {
        Optional<User> userInDb = userRepository.findByEmail(user.getEmail());

        if (userInDb.isPresent()) {
            throw new ConflictException("Email is already in use");
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", id)));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Specification<User> specification = UserSpecification.filterUsers(ids);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<User> users = userRepository.findAll(specification, pageable).stream().toList();
        return users.stream().map(UserMapper::toDto).toList();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %d is not found", id)));
    }
}
