package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(Collection<User> user);
}
