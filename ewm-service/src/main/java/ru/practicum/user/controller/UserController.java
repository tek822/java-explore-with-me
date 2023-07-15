package ru.practicum.user.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST запрос на создание пользователя: {}", userDto);
        return userService.add(userDto);
    }

    @GetMapping(path = "/admin/users")
    List<UserDto> getUser(@PathVariable(name = "ids", required = false) List<Long> ids,
                    @PositiveOrZero(message = "Параметр from >= 0")
                    @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                    @Positive(message = "Параметр size > 0")
                    @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("GET запрос данных пользователей с ids: {}, from: {}, size: {}", ids, from, size);
        return userService.get(ids, from, size);
    }

    @DeleteMapping(path = "/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable(name = "userId", required = true) long userId) {
        log.info("DELETE запрос для пользователя с id: {}", userId);
        userService.delete(userId);
    }
}
