package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

  @GetMapping("/register")
  public String register() {
    return "register";
  }
}