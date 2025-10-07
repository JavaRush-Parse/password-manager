package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static ua.com.javarush.parse.m5.passwordmanager.config.RedirectConstants.REDIRECT_HOME;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTrigger;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.config.RedirectConstants;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@Slf4j
@Controller
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionControllerWeb {

  private final CollectionService collectionService;

  @GetMapping("/create")
  public String showCreateForm(Model model) {
    model.addAttribute("collection", new CollectionForm());
    return "create-collection";
  }

  @PostMapping("/save")
  public String save(@ModelAttribute("collection") CollectionForm form) {
    Collection collection = new Collection();
    collection.setName(form.getName());
    collection.setDescription(form.getDescription());
    collection.setColor(form.getColor());
    collection.setIcon(form.getIcon());
    collectionService.save(collection);
    return REDIRECT_HOME;
  }

  @HxRequest
  @HxTrigger("refreshVaultTable")
  @PostMapping("/save")
  @ResponseBody
  public String saveNewItem(
      @ModelAttribute("collection") CollectionForm form, HttpServletResponse response) {
    try {

      Collection collection = new Collection();
      collection.setName(form.getName());
      collection.setDescription(form.getDescription());
      collection.setColor(form.getColor());
      collection.setIcon(form.getIcon());
      collectionService.save(collection);

      return "";
    } catch (Exception e) {
      log.error("Error saving collection", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return "Error saving collection: " + e.getMessage();
    }
  }

  @HxRequest
  @GetMapping("/create-modal")
  public String showCreateFormModal(Model model) {
    model.addAttribute("collection", new CollectionForm());
    return "component/create-collection-modal :: modal";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    Optional<Collection> existing = collectionService.findById(id);
    if (existing.isPresent()) {
      CollectionForm form = new CollectionForm();
      form.setId(existing.get().getId());
      form.setName(existing.get().getName());
      form.setDescription(existing.get().getDescription());
      form.setColor(existing.get().getColor());
      form.setIcon(existing.get().getIcon());
      model.addAttribute("collection", form);
      return "edit-collection";
    }
    return REDIRECT_HOME;
  }

  @HxRequest
  @GetMapping("/edit/{id}")
  public String showEditFormModal(@PathVariable Long id, Model model) {
    Optional<Collection> existing = collectionService.findById(id);
    if (existing.isPresent()) {
      CollectionForm form = new CollectionForm();
      form.setId(existing.get().getId());
      form.setName(existing.get().getName());
      form.setDescription(existing.get().getDescription());
      form.setColor(existing.get().getColor());
      form.setIcon(existing.get().getIcon());
      model.addAttribute("collection", form);
      return "edit-collection :: modal";
    }
    return REDIRECT_HOME;
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Long id, @ModelAttribute("collection") CollectionForm form) {
    Collection collection = new Collection();
    collection.setName(form.getName());
    collection.setDescription(form.getDescription());
    collection.setColor(form.getColor());
    collection.setIcon(form.getIcon());
    collectionService.update(id, collection);
    return RedirectConstants.REDIRECT_COLLECTIONS;
  }

  @HxRequest
  @PostMapping(value = "/update/{id}", headers = "HX-Request=true")
  @ResponseBody
  @HxTrigger("refreshVaultTable")
  public String updateHTMX(
      @PathVariable Long id,
      @ModelAttribute("collection") CollectionForm form,
      HttpServletResponse response) {
    try {
      Collection collection = new Collection();
      collection.setName(form.getName());
      collection.setDescription(form.getDescription());
      collection.setColor(form.getColor());
      collection.setIcon(form.getIcon());
      collectionService.update(id, collection);
      return "";
    } catch (Exception e) {
      log.error("Error updating collection", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return "Error updating collection: " + e.getMessage();
    }
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    collectionService.deleteById(id);
    return RedirectConstants.REDIRECT_COLLECTIONS;
  }

  @Data
  public static class CollectionForm {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
  }
}
