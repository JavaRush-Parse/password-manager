package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.dto.ErrorResponse;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

  private final CollectionService collectionService;

  @PostMapping("/create")
  public ResponseEntity<Collection> save(@RequestBody Collection collection) {
    Collection saved = collectionService.save(collection);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  @GetMapping("/all")
  public ResponseEntity<List<Collection>> getAll() {
    return new ResponseEntity<>(collectionService.findAll(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id) {
    Optional<Collection> collection = collectionService.findById(id);
    if (collection.isPresent()) {
      return ResponseEntity.ok(collection.get());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ErrorResponse.of("Collection with id '" + id + "' not found"));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id, @RequestBody Collection updatedCollectionData) {
    Optional<Collection> updatedCollection = collectionService.update(id, updatedCollectionData);
    if (updatedCollection.isPresent()) {
      return ResponseEntity.ok(updatedCollection.get());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ErrorResponse.of("Collection with id '" + id + "' not found"));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    collectionService.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
