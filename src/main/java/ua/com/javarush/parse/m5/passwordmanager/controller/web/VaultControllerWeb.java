package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static ua.com.javarush.parse.m5.passwordmanager.config.RedirectConstants.REDIRECT_HOME;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTrigger;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@Controller
@RequestMapping("/vault-item")
@RequiredArgsConstructor
@Slf4j
public class VaultControllerWeb {

  private final VaultItemService vaultItemService;
  private final CollectionService collectionService;
  private final VaultAuditService vaultAuditService;

  @GetMapping("/{id:[0-9]+}")
  public String get(@PathVariable Long id, Model model) {
    Optional<VaultItem> byId = vaultItemService.findById(id);
    if (byId.isPresent()) {
      model.addAttribute("vaultItem", byId.get());
      return "vault";
    }
    return REDIRECT_HOME;
  }

  @GetMapping("/create")
  public String showCreateForm(Model model) {
    model.addAttribute("vault", new VaultItem());
    model.addAttribute("collections", collectionService.findAll());
    return "create-vault";
  }

  @HxRequest
  @GetMapping("/create-modal")
  public String showCreateFormModal(Model model) {
    model.addAttribute("vault", new VaultItem());
    model.addAttribute("collections", collectionService.findAll());
    return "component/create-vault-modal :: modal";
  }

  @PostMapping(value = "/save", headers = "HX-Request=true")
  @ResponseBody
  @HxTrigger("refreshVaultTable")
  public String saveNewItemHtmx(
      @ModelAttribute("vault") VaultItem item, HttpServletResponse response) {
    try {
      vaultItemService.save(item);
      return "";
    } catch (Exception e) {
      log.error("Error saving vault item", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return "Error saving item: " + e.getMessage();
    }
  }

  @PostMapping("/save")
  public String saveNewItem(@ModelAttribute("vault") VaultItem item) {
    vaultItemService.save(item);
    return REDIRECT_HOME;
  }

  @HxRequest
  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    Optional<VaultItem> byId = vaultItemService.findById(id);
    if (byId.isPresent()) {
      model.addAttribute("vault", byId.get());
      model.addAttribute("collections", collectionService.findAll());
      return "edit-vault :: modal";
    }
    return REDIRECT_HOME;
  }

  @PostMapping("/update")
  public String updateItem(@ModelAttribute("vault") VaultItem itemFromForm) {
    vaultItemService.update(itemFromForm);
    return REDIRECT_HOME;
  }

  @HxRequest
  @PostMapping(value = "/update", headers = "HX-Request=true")
  @ResponseBody
  @HxTrigger("refreshVaultTable")
  public String updateItemHTMX(
      @ModelAttribute("vault") VaultItem itemFromForm, HttpServletResponse response) {
    try {
      vaultItemService.update(itemFromForm);
      return "";
    } catch (Exception e) {
      log.error("Error updating vault item", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return "Error updating item: " + e.getMessage();
    }
  }

  @DeleteMapping("/delete/{id}")
  public String deleteItem(@PathVariable Long id) {
    vaultItemService.deleteById(id);
    return REDIRECT_HOME;
  }

  @GetMapping("/audit/{id}")
  public String showAuditHistory(@PathVariable Long id, Model model) {
    Optional<VaultItem> vaultItem = vaultItemService.findById(id);
    if (vaultItem.isPresent()) {
      model.addAttribute("vaultItem", vaultItem.get());
      model.addAttribute("auditHistory", vaultAuditService.getAuditHistory(id));
      return "audit-history";
    }
    return "redirect:/";
  }

  @HxRequest
  @GetMapping("/audit-modal/{id}")
  public String showAuditHistoryModal(@PathVariable Long id, Model model) {
    Optional<VaultItem> vaultItem = vaultItemService.findById(id);
    if (vaultItem.isPresent()) {
      model.addAttribute("vaultItem", vaultItem.get());
      model.addAttribute("auditHistory", vaultAuditService.getAuditHistory(id));
      return "component/audit-history-modal :: modal";
    }
    return "component/audit-history-modal :: error";
  }
}
