package ru.practicum.user.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.dto.UserMapper.toUserDto;
import static ru.practicum.util.Util.getUser;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
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
        return users.stream().map(user -> toUserDto(user)).collect(Collectors.toList());
    }

    @Override
    public void delete(long userId) {
        log.info("Удалены данные пользователя с id: {}", userId);
        User user = getUser(userRepository, userId);
        userRepository.deleteById(userId);
    }
}
