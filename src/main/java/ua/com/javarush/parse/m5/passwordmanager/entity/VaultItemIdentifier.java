package ua.com.javarush.parse.m5.passwordmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class VaultItemIdentifier {
    private String resource;
    private String login;
}
