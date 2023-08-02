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

import static ru.practicum.Constants.DEFAULT_PAGE_FROM;
import static ru.practicum.Constants.DEFAULT_PAGE_SIZE;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST запрос на создание пользователя: {}", userDto);
        return userService.add(userDto);
    }

    @GetMapping
    List<UserDto> getUser(@RequestParam(name = "ids", required = false) List<Long> ids,
                    @PositiveOrZero(message = "Параметр from >= 0")
                    @RequestParam(name = "from", required = false, defaultValue = DEFAULT_PAGE_FROM) int from,
                    @Positive(message = "Параметр size > 0")
                    @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size) {
        log.info("GET запрос данных пользователей с ids: {}, from: {}, size: {}", ids, from, size);
        return userService.get(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable(name = "userId", required = true) long userId) {
        log.info("DELETE запрос для пользователя с id: {}", userId);
        userService.delete(userId);
    }
}
