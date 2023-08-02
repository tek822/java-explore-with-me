package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(long userId);
}
