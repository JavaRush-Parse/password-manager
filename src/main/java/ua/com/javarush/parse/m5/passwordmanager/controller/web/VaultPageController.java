package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.javarush.parse.m5.passwordmanager.dto.PageResponse;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@Controller
@RequestMapping("/vault")
@RequiredArgsConstructor
@Slf4j
public class VaultPageController {

  private final VaultItemService vaultItemService;

  @GetMapping
  public String vault(Model model) {
    List<VaultItem> vaultItems = vaultItemService.findAll();
    model.addAttribute("vaultItems", vaultItems);
    return "vault";
  }

  @HxRequest
  @GetMapping("/table")
  public String vaultTable(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Model model) {
    PageResponse<VaultItem> pageResponse = vaultItemService.findAllPaginated(page, size);
    model.addAttribute("vaultItems", pageResponse.getContent());
    model.addAttribute("currentPage", pageResponse.getCurrentPage());
    model.addAttribute("totalPages", pageResponse.getTotalPages());
    model.addAttribute("totalItems", pageResponse.getTotalElements());
    model.addAttribute("pageSize", pageResponse.getPageSize());
    return "component/vault-table :: vaultTable";
  }

  @HxRequest
  @GetMapping("/table-search")
  public String search(
      @RequestParam(required = false) String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Model model) {
    PageResponse<VaultItem> pageResponse = vaultItemService.searchPaginated(query, page, size);
    model.addAttribute("vaultItems", pageResponse.getContent());
    model.addAttribute("currentPage", pageResponse.getCurrentPage());
    model.addAttribute("totalPages", pageResponse.getTotalPages());
    model.addAttribute("totalItems", pageResponse.getTotalElements());
    model.addAttribute("pageSize", pageResponse.getPageSize());
    model.addAttribute("query", query != null ? query : "");
    return "component/vault-table :: vaultTable";
  }
}
