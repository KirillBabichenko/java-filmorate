package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Data
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
    private Set<Long> friends = new HashSet<>();
   // private Set<Long> unverifiedFriends = new HashSet<>();

}
