package ua.com.javarush.parse.m5.passwordmanager.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;

@Service
@RequiredArgsConstructor
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final UserService userService;

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
  public List<Collection> findAll() {
    User currentUser = userService.getCurrentUser();
    return collectionRepository.findByOwner(currentUser, Sort.by(Sort.Direction.ASC, "id"));
  }

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':id:' + #id")
  public Optional<Collection> findById(Long id) {
    User currentUser = userService.getCurrentUser();
    return collectionRepository.findByIdAndOwner(id, currentUser);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Collection save(Collection collection) {
    User currentUser = userService.getCurrentUser();
    collection.setOwner(currentUser);
    return collectionRepository.save(collection);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public void deleteById(Long id) {
    User currentUser = userService.getCurrentUser();
    Optional<Collection> collection = collectionRepository.findByIdAndOwner(id, currentUser);
    if (collection.isPresent()) {
      collectionRepository.deleteById(id);
    } else {
      throw new RuntimeException("Collection not found or you don't have permission to delete it");
    }
  }

  public boolean existsByName(String name) {
    User currentUser = userService.getCurrentUser();
    return collectionRepository.existsByNameIgnoreCaseAndOwner(name, currentUser);
  }

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':name:' + #name")
  public Optional<Collection> findByName(String name) {
    User currentUser = userService.getCurrentUser();
    return collectionRepository.findByNameIgnoreCaseAndOwner(name, currentUser);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Optional<Collection> update(Long id, Collection updatedCollectionData) {
    User currentUser = userService.getCurrentUser();
    return collectionRepository
        .findByIdAndOwner(id, currentUser)
        .map(
            existingCollection -> {
              existingCollection.setName(updatedCollectionData.getName());
              existingCollection.setColor(updatedCollectionData.getColor());
              existingCollection.setIcon(updatedCollectionData.getIcon());
              existingCollection.setDescription(updatedCollectionData.getDescription());
              return collectionRepository.save(existingCollection);
            });
  }
}
