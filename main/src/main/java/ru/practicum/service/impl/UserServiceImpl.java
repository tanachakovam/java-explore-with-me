package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.dto.user.UserDto;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;
import ru.practicum.model.User;
import ru.practicum.mapper.UserMapper;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        Collection<User> users = repository.findAllByIdIsIn(ids, pageable);
        return mapper.toUserDto(users);
    }

    @Override
    @Transactional
    public UserDto registerUser(UserDto userDto) {
        checkUserName(userDto.getName());
        User user = mapper.toUser(userDto);
        User registeredUser = repository.save(user);
        return mapper.toUserDto(registeredUser);
    }

    @Transactional(readOnly = true)
    public void checkUserName(String name) {
        if (repository.findUserByName(name) != null) {
            throw new ValidationException("User name already exists.");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " is not found."));
        repository.deleteById(userId);
    }
}
