package ua.com.javarush.parse.m5.passwordmanager.controller.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.dto.ErrorResponse;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vault")
public class VaultItemController {

    private final VaultItemService vaultItemService;

    @PostMapping("/create")
    public ResponseEntity<VaultItem> save(@RequestBody VaultItem item) {
        VaultItem save = vaultItemService.save(item);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

  @PostMapping("/import")
  public ResponseEntity<?> importVaultItems(@RequestBody List<VaultItem> items) {
    try {
      vaultItemService.importVaultItems(items);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
              Map.of(
                      "status", HttpStatus.BAD_REQUEST.value(),
                      "message", e.getMessage()
              )
      );
    }
  }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<VaultItem> item = vaultItemService.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("Vault item with id '" + id + "' not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody VaultItem updatedItemData) {
        Optional<VaultItem> updatedItem = vaultItemService.update(id, updatedItemData);
        if (updatedItem.isPresent()) {
            return ResponseEntity.ok(updatedItem.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("Vault item with id '" + id + "' not found"));
        }
    }

    @GetMapping
    public ResponseEntity<?> findByLogin(@RequestParam String login) {
        List<VaultItem> byLogin = vaultItemService.findByLogin(login);

        if (byLogin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("Vault items for login '" + login + "' not found"));
        }

        return ResponseEntity.ok(byLogin);
    }

    @GetMapping("/resource")
    public ResponseEntity<?> findByResource(@RequestParam String resource) {
        List<VaultItem> byResource = vaultItemService.findByResource(resource);
        if (byResource.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.of("Vault items for resource '" + resource + "' not found"));
        }
        return ResponseEntity.ok(byResource);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VaultItem>> getAll() {
        return new ResponseEntity<>(vaultItemService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaultItemService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
