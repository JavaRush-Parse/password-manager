package ua.com.javarush.parse.m5.passwordmanager.service;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@Service
public class CollectionService extends BaseUserAwareService {

  private final CollectionRepository collectionRepository;

  public CollectionService(
      UserRepository userRepository, CollectionRepository collectionRepository) {
    super(userRepository);
    this.collectionRepository = collectionRepository;
  }

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
  public List<Collection> findAll() {
    return collectionRepository.findByOwner(getCurrentUser(), Sort.by(Sort.Direction.ASC, "id"));
  }

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':id:' + #id")
  public Optional<Collection> findById(Long id) {
    return collectionRepository.findByIdAndOwner(id, getCurrentUser());
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Collection save(Collection collection) {
    collection.setOwner(getCurrentUser());
    return collectionRepository.save(collection);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public void deleteById(Long id) {
    Optional<Collection> collection = collectionRepository.findByIdAndOwner(id, getCurrentUser());
    if (collection.isPresent()) {
      collectionRepository.deleteById(id);
    } else {
      throw new RuntimeException("Collection not found or you don't have permission to delete it");
    }
  }

  public boolean existsByName(String name) {
    return collectionRepository.existsByNameIgnoreCaseAndOwner(name, getCurrentUser());
  }

  @Cacheable(
      value = "collections",
      key =
          "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':name:' + #name")
  public Optional<Collection> findByName(String name) {
    return collectionRepository.findByNameIgnoreCaseAndOwner(name, getCurrentUser());
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Optional<Collection> update(Long id, Collection updatedCollectionData) {
    return collectionRepository
        .findByIdAndOwner(id, getCurrentUser())
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
