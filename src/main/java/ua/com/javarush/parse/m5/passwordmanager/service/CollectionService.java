package ua.com.javarush.parse.m5.passwordmanager.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;

@Service
@RequiredArgsConstructor
public class CollectionService {

  private final CollectionRepository collectionRepository;

  @Cacheable(value = "collections", key = "'all'")
  public List<Collection> findAll() {
    return collectionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
  }

  @Cacheable(value = "collections", key = "#id")
  public Optional<Collection> findById(Long id) {
    return collectionRepository.findById(id);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Collection save(Collection collection) {
    return collectionRepository.save(collection);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public void deleteById(Long id) {
    collectionRepository.deleteById(id);
  }

  public boolean existsByName(String name) {
    return collectionRepository.existsByNameIgnoreCase(name);
  }

  @Cacheable(value = "collections", key = "'name:' + #name")
  public Optional<Collection> findByName(String name) {
    return collectionRepository.findByNameIgnoreCase(name);
  }

  @CacheEvict(value = "collections", allEntries = true)
  public Optional<Collection> update(Long id, Collection updatedCollectionData) {
    return collectionRepository
        .findById(id)
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
