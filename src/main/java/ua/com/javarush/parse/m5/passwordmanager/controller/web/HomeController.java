package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

  @GetMapping("/")
  public String home() {
    return "index";
  }
}
