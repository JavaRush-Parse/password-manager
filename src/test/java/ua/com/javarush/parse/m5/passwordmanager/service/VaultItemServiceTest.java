package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.exception.VaultItemImportException;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("VaultItemService Tests")
class VaultItemServiceTest {

  @Mock private VaultItemRepository repository;
  @Mock private VaultAuditService vaultAuditService;
  @Mock private UserService userService;

  @InjectMocks private VaultItemService service;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
  }

  @Test
  @DisplayName("Verify repository's save method is called on save")
  void whenSave_thenRepositorySaveIsCalled() {
    // Given
    VaultItem vaultItem = new VaultItem();
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.save(any(VaultItem.class))).thenReturn(vaultItem);

    // When
    VaultItem saved = service.save(vaultItem);

    // Then
    assertThat(saved).isNotNull();
    assertThat(saved.getOwner()).isEqualTo(testUser);
    verify(repository).save(vaultItem);
    verify(vaultAuditService).logCreate(saved);
  }

  @Test
  @DisplayName("Update existing vault item")

  // AAA
  void whenUpdate_thenExistingItemIsUpdated() {
    // Given
    VaultItem existingItem = new VaultItem();
    existingItem.setId(1L);
    existingItem.setName("Old Name");

    VaultItem updatedData = new VaultItem();
    updatedData.setId(existingItem.getId());
    updatedData.setName("New Name");
    updatedData.setResource("Resource");
    updatedData.setLogin("Login");
    updatedData.setDescription("Description");
    updatedData.setPassword("password");

    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.findByIdAndOwner(existingItem.getId(), testUser)).thenReturn(Optional.of(existingItem));
    when(repository.save(any(VaultItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<VaultItem> result = service.update(updatedData);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("New Name");
    verify(repository).findByIdAndOwner(existingItem.getId(), testUser);
    verify(repository).save(existingItem);
    verify(vaultAuditService).logUpdate(any(VaultItem.class), any(VaultItem.class));
  }

  @Test
  @DisplayName("Verify repository's findAll method is called on findAll")
  void whenFindAll_thenRepositoryFindAllIsCalled() {
    // Given
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.findAllByOwner(eq(testUser), any(Sort.class))).thenReturn(List.of(new VaultItem(), new VaultItem()));

    // When
    List<VaultItem> result = service.findAll();

    // Then
    assertThat(result).hasSize(2);
    verify(repository).findAllByOwner(eq(testUser), any(Sort.class));
  }

  @Test
  @DisplayName("Verify repository's findById method is called on findById")
  void whenFindById_thenRepositoryFindByIdIsCalled() {
    // Given
    VaultItem vaultItem = new VaultItem();
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(vaultItem));

    // When
    Optional<VaultItem> result = service.findById(1L);

    // Then
    assertThat(result).isPresent();
    verify(repository).findByIdAndOwner(1L, testUser);
  }

  @Test
  @DisplayName("Verify repository's findVaultItemByLogin method is called on findByLogin")
  void whenFindByLogin_thenRepositoryFindVaultItemByLoginIsCalled() {
    // Given
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.findVaultItemByLoginAndOwner("testuser", testUser)).thenReturn(List.of(new VaultItem()));

    // When
    List<VaultItem> result = service.findByLogin("testuser");

    // Then
    assertThat(result).hasSize(1);
    verify(repository).findVaultItemByLoginAndOwner("testuser", testUser);
  }

  @Test
  @DisplayName("Verify repository's deleteById method is called on deleteById")
  void whenDeleteById_thenRepositoryDeleteByIdIsCalled() {
    // Given
    VaultItem vaultItem = new VaultItem();
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(vaultItem));
    doNothing().when(repository).deleteById(1L);

    // When
    service.deleteById(1L);

    // Then
    verify(repository).findByIdAndOwner(1L, testUser);
    verify(vaultAuditService).logDelete(1L);
    verify(repository).deleteById(1L);
  }

  @Test
  @DisplayName("Import vault items successfully")
  void importVaultItems_shouldImportItemsSuccessfully() {
    // Given
    VaultItem item1 = new VaultItem();
    item1.setLogin("login1");
    item1.setResource("resource1");

    VaultItem item2 = new VaultItem();
    item2.setLogin("login2");
    item2.setResource("resource2");

    List<VaultItem> items = List.of(item1, item2);

    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.existsByResourceAndLoginAndOwner(anyString(), anyString(), eq(testUser))).thenReturn(false);
    when(repository.saveAll(items)).thenReturn(items);

    // When
    List<VaultItem> result = service.importVaultItems(items);

    // Then
    assertThat(result).hasSize(2);
    verify(repository).saveAll(items);
  }

  @Test
  @DisplayName("Import vault items with empty login")
  void importVaultItems_shouldThrowExceptionForEmptyLogin() {
    // Given
    VaultItem item = new VaultItem();
    item.setLogin("");
    item.setResource("resource");
    List<VaultItem> items = List.of(item);
    when(userService.getCurrentUser()).thenReturn(testUser);

    // When & Then
    assertThrows(VaultItemImportException.class, () -> service.importVaultItems(items));
  }

  @Test
  @DisplayName("Import vault items with empty resource")
  void importVaultItems_shouldThrowExceptionForEmptyResource() {
    // Given
    VaultItem item = new VaultItem();
    item.setLogin("login");
    item.setResource("");
    List<VaultItem> items = List.of(item);
    when(userService.getCurrentUser()).thenReturn(testUser);

    // When & Then
    assertThrows(VaultItemImportException.class, () -> service.importVaultItems(items));
  }

  @Test
  @DisplayName("Import vault items with duplicate item in list")
  void importVaultItems_shouldThrowExceptionForDuplicateItemInList() {
    // Given
    VaultItem item1 = new VaultItem();
    item1.setLogin("login");
    item1.setResource("resource");

    VaultItem item2 = new VaultItem();
    item2.setLogin("login");
    item2.setResource("resource");

    List<VaultItem> items = List.of(item1, item2);
    when(userService.getCurrentUser()).thenReturn(testUser);

    // When & Then
    assertThrows(VaultItemImportException.class, () -> service.importVaultItems(items));
  }

  @Test
  @DisplayName("Import vault items with item that already exists in database")
  void importVaultItems_shouldThrowExceptionForItemAlreadyExistsInDatabase() {
    // Given
    VaultItem item = new VaultItem();
    item.setLogin("login");
    item.setResource("resource");
    List<VaultItem> items = List.of(item);
    when(userService.getCurrentUser()).thenReturn(testUser);
    when(repository.existsByResourceAndLoginAndOwner("resource", "login", testUser)).thenReturn(true);

    // When & Then
    assertThrows(VaultItemImportException.class, () -> service.importVaultItems(items));
  }
}
