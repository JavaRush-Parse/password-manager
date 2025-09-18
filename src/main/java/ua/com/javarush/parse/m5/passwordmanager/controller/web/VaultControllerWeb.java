package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTrigger;

import java.util.Optional;

@Controller
@RequestMapping("/vault-item")
@RequiredArgsConstructor
@Slf4j
public class VaultControllerWeb {

    private final VaultItemService vaultItemService;
    private final CollectionService collectionService;

    @GetMapping("/{id}")
    public String get(@PathVariable Long id, Model model) {
        Optional<VaultItem> byId = vaultItemService.findById(id);
        byId.ifPresent(vaultItem -> {
            model.addAttribute("vault", vaultItem);
        });
        return "vault";
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
    public String saveNewItemHtmx(@ModelAttribute("vault") VaultItem item, HttpServletResponse response) {
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
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<VaultItem> byId = vaultItemService.findById(id);
        if (byId.isPresent()) {
            model.addAttribute("vault", byId.get());
            model.addAttribute("collections", collectionService.findAll());
            return "edit-vault";
        }
    return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute("vault") VaultItem itemFromForm) {
        vaultItemService.update(id, itemFromForm);
        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        vaultItemService.deleteById(id);
        return "redirect:/";
    }
}