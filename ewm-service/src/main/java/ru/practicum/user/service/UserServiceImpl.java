package ru.practicum.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.dto.UserMapper.toUserDto;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("Добавлен пользователь: {}", userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        List<User> users;
        PageRequest page = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAllPageable(page);
        } else {
            users = userRepository.findAllById(ids);
        }
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long userId) {
        log.info("Удалены данные пользователя с id: {}", userId);
        User user = getUser(userRepository, userId);
        userRepository.deleteById(userId);
    }

    public static User getUser(UserRepository userRepository, long  userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + ", не найден")
        );
    }
}
