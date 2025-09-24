package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.com.javarush.parse.m5.passwordmanager.service.PasswordGeneratorService;

@Controller
@RequiredArgsConstructor
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;

    @GetMapping("/password/generate")
    public String generatePassword(Model model) {
        String generatedPassword = passwordGeneratorService.generateStrongPassword();
        model.addAttribute("generatedPassword", generatedPassword);
        return "fragments/password-input :: password-input";
    }
}
