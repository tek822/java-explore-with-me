package ru.practicum.util;

import ru.practicum.category.Category;
import ru.practicum.category.exception.CategoryNotFoundException;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.exception.UserNotFoundException;

public class Util {
    public static User getUser(UserRepository userRepository, long  userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id: " + userId + ", не найден")
        );
    }

    public static Category getCategory(CategoryRepository categoryRepository, long categoryID) {
        return  categoryRepository.findById(categoryID).orElseThrow(() ->
                new CategoryNotFoundException("Category with id=" + categoryID + " was not found.")
        );
    }
}
