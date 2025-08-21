package ua.com.javarush.parse.m5.passwordmanager.controller.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vault-item")
public class VaultItemController {

  private final VaultItemRepository vaultItemRepository;

  @PostMapping("/create")
  public ResponseEntity<VaultItem> save(@RequestBody VaultItem item) {

    VaultItem save = vaultItemRepository.save(item);

    return new ResponseEntity<VaultItem>(save, HttpStatus.CREATED);
  }

  @GetMapping("/all")
  public ResponseEntity<List<VaultItem>> getAll() {
    return new ResponseEntity<>(vaultItemRepository.findAll(), HttpStatus.OK);
  }
}
