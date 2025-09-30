package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollectionService Tests")
class CollectionServiceTest {

  @Mock private CollectionRepository collectionRepository;

  @InjectMocks private CollectionService collectionService;

  @Test
  @DisplayName("Find all collections")
  void findAll_shouldReturnAllCollections() {
    // Given
    when(collectionRepository.findAll(any(Sort.class)))
        .thenReturn(List.of(new Collection(), new Collection()));

    // When
    List<Collection> result = collectionService.findAll();

    // Then
    assertThat(result).hasSize(2);
    verify(collectionRepository).findAll(any(Sort.class));
  }

  @Test
  @DisplayName("Find collection by ID")
  void findById_shouldReturnCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

    // When
    Optional<Collection> result = collectionService.findById(1L);

    // Then
    assertThat(result).isPresent();
    verify(collectionRepository).findById(1L);
  }

  @Test
  @DisplayName("Save collection")
  void save_shouldSaveCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

    // When
    Collection saved = collectionService.save(collection);

    // Then
    assertThat(saved).isNotNull();
    verify(collectionRepository).save(collection);
  }

  @Test
  @DisplayName("Delete collection by ID")
  void deleteById_shouldDeleteCollection() {
    // Given
    doNothing().when(collectionRepository).deleteById(1L);

    // When
    collectionService.deleteById(1L);

    // Then
    verify(collectionRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Check if collection exists by name")
  void existsByName_shouldReturnTrueIfExisits() {
    // Given
    when(collectionRepository.existsByNameIgnoreCase("Test Collection")).thenReturn(true);

    // When
    boolean result = collectionService.existsByName("Test Collection");

    // Then
    assertThat(result).isTrue();
    verify(collectionRepository).existsByNameIgnoreCase("Test Collection");
  }

  @Test
  @DisplayName("Find collection by name")
  void findByName_shouldReturnCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.findByNameIgnoreCase("Test Collection"))
        .thenReturn(Optional.of(collection));

    // When
    Optional<Collection> result = collectionService.findByName("Test Collection");

    // Then
    assertThat(result).isPresent();
    verify(collectionRepository).findByNameIgnoreCase("Test Collection");
  }

  @Test
  @DisplayName("Update existing collection")
  void update_shouldUpdateCollection() {
    // Given
    Collection existingCollection = new Collection();
    existingCollection.setId(1L);
    existingCollection.setName("Old Name");

    Collection updatedData = new Collection();
    updatedData.setName("New Name");

    when(collectionRepository.findById(1L)).thenReturn(Optional.of(existingCollection));
    when(collectionRepository.save(any(Collection.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<Collection> result = collectionService.update(1L, updatedData);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("New Name");
    verify(collectionRepository).findById(1L);
    verify(collectionRepository).save(existingCollection);
  }
}
