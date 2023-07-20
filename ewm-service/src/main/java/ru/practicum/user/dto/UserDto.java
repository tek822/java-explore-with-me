package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Long id;

    @NotBlank
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    String name;

    @Email
    @NotBlank
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    String email;
}
