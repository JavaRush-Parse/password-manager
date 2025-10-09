package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@Tag(
    name = "Vault Item Management",
    description = "API for managing individual passwords and vault items")
@RestController
@RequestMapping("/api/v1/vault")
@RequiredArgsConstructor
public class VaultItemController {

  private final VaultItemService vaultItemService;

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new vault item")
  public VaultItem create(@RequestBody VaultItem item) {
    return vaultItemService.save(item);
  }

  @PostMapping("/import")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Import multiple vault items in a batch")
  public List<VaultItem> importVaultItems(@RequestBody List<VaultItem> items) {
    return vaultItemService.importVaultItems(items);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a vault item by ID")
  public ResponseEntity<VaultItem> findById(@PathVariable Long id) {
    return vaultItemService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an existing vault item")
  public ResponseEntity<VaultItem> update(
      @PathVariable Long id, @RequestBody VaultItem updatedItemData) {
    return vaultItemService
        .update(updatedItemData)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/login/{login}")
  @Operation(summary = "Find vault items by login")
  public List<VaultItem> findByLogin(@PathVariable String login) {
    return vaultItemService.findByLogin(login);
  }

  @GetMapping("/resource")
  @Operation(summary = "Find vault items by resource URL")
  public List<VaultItem> findByResource(@RequestParam String resource) {
    return vaultItemService.findByResource(resource);
  }

  @GetMapping("/collection/{collectionName}")
  @Operation(summary = "Find vault items by collection name")
  public List<VaultItem> findByCollectionName(@PathVariable String collectionName) {
    return vaultItemService.findByCollectionName(collectionName);
  }

  @GetMapping("/all")
  @Operation(summary = "Get all vault items")
  public List<VaultItem> getAll() {
    return vaultItemService.findAll();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a vault item by ID")
  public void delete(@PathVariable Long id) {
    vaultItemService.deleteById(id);
  }
}
