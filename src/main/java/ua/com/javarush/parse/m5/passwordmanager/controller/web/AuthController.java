package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // <-- Импортируем BindingResult
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.com.javarush.parse.m5.passwordmanager.dto.user.UserRegistrationRequestDto;
import ua.com.javarush.parse.m5.passwordmanager.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new UserRegistrationRequestDto());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(
      @ModelAttribute("user") @Valid UserRegistrationRequestDto request,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "register";
    }

    try {
      userService.register(request);
    } catch (RuntimeException e) {
      bindingResult.rejectValue("email", "email.exists", e.getMessage());
      return "register";
    }

    return "redirect:/login?registration_success";
  }

  @GetMapping("/login")
  public String showLoginForm() {
    return "login";
  }
}
