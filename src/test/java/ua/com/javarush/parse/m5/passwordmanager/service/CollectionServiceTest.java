package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollectionService Tests")
class CollectionServiceTest {

  @Mock private CollectionRepository collectionRepository;
  @Mock private UserRepository userRepository;
  @Mock private SecurityContext securityContext;

  private CollectionService collectionService;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");

    collectionService = new CollectionService(userRepository, collectionRepository);

    // Setup security context
    SecurityContextHolder.setContext(securityContext);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            "test@example.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
  }

  @Test
  @DisplayName("Find all collections")
  void findAll_shouldReturnAllCollections() {
    // Given
    when(collectionRepository.findByOwner(eq(testUser), any(Sort.class)))
        .thenReturn(List.of(new Collection(), new Collection()));

    // When
    List<Collection> result = collectionService.findAll();

    // Then
    assertThat(result).hasSize(2);
    verify(collectionRepository).findByOwner(eq(testUser), any(Sort.class));
  }

  @Test
  @DisplayName("Find collection by ID")
  void findById_shouldReturnCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(collection));

    // When
    Optional<Collection> result = collectionService.findById(1L);

    // Then
    assertThat(result).isPresent();
    verify(collectionRepository).findByIdAndOwner(1L, testUser);
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
    assertThat(saved.getOwner()).isEqualTo(testUser);
    verify(collectionRepository).save(collection);
  }

  @Test
  @DisplayName("Delete collection by ID")
  void deleteById_shouldDeleteCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(collection));
    doNothing().when(collectionRepository).deleteById(1L);

    // When
    collectionService.deleteById(1L);

    // Then
    verify(collectionRepository).findByIdAndOwner(1L, testUser);
    verify(collectionRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Check if collection exists by name")
  void existsByName_shouldReturnTrueIfExisits() {
    // Given
    when(collectionRepository.existsByNameIgnoreCaseAndOwner("Test Collection", testUser))
        .thenReturn(true);

    // When
    boolean result = collectionService.existsByName("Test Collection");

    // Then
    assertThat(result).isTrue();
    verify(collectionRepository).existsByNameIgnoreCaseAndOwner("Test Collection", testUser);
  }

  @Test
  @DisplayName("Find collection by name")
  void findByName_shouldReturnCollection() {
    // Given
    Collection collection = new Collection();
    when(collectionRepository.findByNameIgnoreCaseAndOwner("Test Collection", testUser))
        .thenReturn(Optional.of(collection));

    // When
    Optional<Collection> result = collectionService.findByName("Test Collection");

    // Then
    assertThat(result).isPresent();
    verify(collectionRepository).findByNameIgnoreCaseAndOwner("Test Collection", testUser);
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

    when(collectionRepository.findByIdAndOwner(1L, testUser))
        .thenReturn(Optional.of(existingCollection));
    when(collectionRepository.save(any(Collection.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<Collection> result = collectionService.update(1L, updatedData);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("New Name");
    verify(collectionRepository).findByIdAndOwner(1L, testUser);
    verify(collectionRepository).save(existingCollection);
  }
}
