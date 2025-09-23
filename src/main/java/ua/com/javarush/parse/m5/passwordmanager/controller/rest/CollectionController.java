package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@Tag(name = "Collection Management", description = "API for managing user's password collections")
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

  private final CollectionService collectionService;

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new collection")
  public Collection create(@RequestBody Collection collection) {
    return collectionService.save(collection);
  }

  @GetMapping("/all")
  @Operation(summary = "Get all collections")
  public List<Collection> getAll() {
    return collectionService.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a collection by ID")
  public ResponseEntity<Collection> findById(@PathVariable Long id) {
    return collectionService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update an existing collection")
  public ResponseEntity<Collection> update(
      @PathVariable Long id, @RequestBody Collection updatedData) {
    return collectionService
        .update(id, updatedData)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a collection by ID")
  public void delete(@PathVariable Long id) {
    collectionService.deleteById(id);
  }
}
