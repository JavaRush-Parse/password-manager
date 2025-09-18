package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

  private final VaultItemService vaultItemService;

  @GetMapping
  public String home(Model model) {
    List<VaultItem> vaultItems = vaultItemService.findAll();
    model.addAttribute("vaultItems", vaultItems);

    return "home";
  }

  @HxRequest
  @GetMapping("/vault-table")
  public String vaultTable(Model model) {
    List<VaultItem> vaultItems = vaultItemService.findAll();
    model.addAttribute("vaultItems", vaultItems);
    return "component/vault-table :: vaultTable";
  }
}
