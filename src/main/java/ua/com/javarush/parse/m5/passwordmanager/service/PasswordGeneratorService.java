package ua.com.javarush.parse.m5.passwordmanager.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.config.PasswordGeneratorProperties;

@Service
@RequiredArgsConstructor
public class PasswordGeneratorService {

  private final PasswordGeneratorProperties properties;
  private final SecureRandom secureRandom;

  public String generateStrongPassword() {
    final int length = properties.getLength();
    final String lowercaseChars = properties.getLowercaseChars();
    final String uppercaseChars = properties.getUppercaseChars();
    final String numbers = properties.getNumbers();
    final String symbols = properties.getSymbols();
    final String allChars = lowercaseChars + uppercaseChars + numbers + symbols;

    List<Character> passwordChars = new ArrayList<>();

    passwordChars.add(lowercaseChars.charAt(secureRandom.nextInt(lowercaseChars.length())));
    passwordChars.add(uppercaseChars.charAt(secureRandom.nextInt(uppercaseChars.length())));
    passwordChars.add(numbers.charAt(secureRandom.nextInt(numbers.length())));
    passwordChars.add(symbols.charAt(secureRandom.nextInt(symbols.length())));

    for (int i = 4; i < length; i++) {
      passwordChars.add(allChars.charAt(secureRandom.nextInt(allChars.length())));
    }

    Collections.shuffle(passwordChars, secureRandom);

    StringBuilder password = new StringBuilder(length);
    for (Character ch : passwordChars) {
      password.append(ch);
    }

    return password.toString();
  }
}
