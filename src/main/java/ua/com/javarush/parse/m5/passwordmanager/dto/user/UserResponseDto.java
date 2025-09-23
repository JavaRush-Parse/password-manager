package ua.com.javarush.parse.m5.passwordmanager.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
}
