package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto registerUser(UserDto userDto);

    void deleteUser(Long userId);

}
