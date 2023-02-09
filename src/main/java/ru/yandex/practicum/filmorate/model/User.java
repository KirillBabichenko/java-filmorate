package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Логин не может быть пустым или содержать пробелы")
    private String login;
    private String name;
    @NotBlank
    @Email(message = "Электронная почта не может быть пустой")
    private String email;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Long> friends;
    private Set<Long> unverifiedFriends;
}
