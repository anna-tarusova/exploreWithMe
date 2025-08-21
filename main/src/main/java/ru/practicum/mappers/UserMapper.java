package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.UserDto;
import ru.practicum.entities.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    // User -> UserDto
    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    // UserDto -> User
    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
