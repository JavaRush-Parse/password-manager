package ua.com.javarush.parse.m5.passwordmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultAuditRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class VaultAuditServiceTest {

  @Mock private VaultAuditRepository vaultAuditRepository;
  @Mock private UserRepository userRepository;
  @Mock private SecurityContext securityContext;

  private VaultAuditService vaultAuditService;

  private VaultItem testVaultItem;
  private Collection testCollection;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");
    testCollection = new Collection();
    testCollection.setId(1L);
    testCollection.setName("Test Collection");

    testVaultItem =
        VaultItem.builder()
            .id(1L)
            .name("Test Item")
            .resource("https://example.com")
            .login("testuser")
            .description("Test description")
            .password("testpassword")
            .collection(testCollection)
            .build();

    vaultAuditService = new VaultAuditService(userRepository, vaultAuditRepository);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void logCreate_ShouldSaveAuditLogWithCreateAction() {
    // Mock authentication
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            "test@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    vaultAuditService.logCreate(testVaultItem);

    ArgumentCaptor<VaultAudit> auditCaptor = ArgumentCaptor.forClass(VaultAudit.class);
    verify(vaultAuditRepository).save(auditCaptor.capture());

    VaultAudit savedAudit = auditCaptor.getValue();
    assertThat(savedAudit.getVaultItemId()).isEqualTo(1L);
    assertThat(savedAudit.getActionType()).isEqualTo(VaultAudit.ActionType.CREATE);
    assertThat(savedAudit.getFieldName()).isEqualTo("*");
    assertThat(savedAudit.getOldValue()).isNull();
    assertThat(savedAudit.getNewValue()).contains("Test Item");
    assertThat(savedAudit.getChangedBy()).isEqualTo("test@example.com");
    assertThat(savedAudit.getChangedAt()).isNotNull();
  }

  @Test
  void logUpdate_ShouldSaveAuditLogsForChangedFields() {
    VaultItem oldItem =
        VaultItem.builder()
            .id(1L)
            .name("Old Name")
            .resource("https://old.com")
            .login("olduser")
            .description("Old description")
            .password("oldpassword")
            .collection(testCollection)
            .build();

    VaultItem newItem =
        VaultItem.builder()
            .id(1L)
            .name("New Name")
            .resource("https://new.com")
            .login("newuser")
            .description("New description")
            .password("newpassword")
            .collection(testCollection)
            .build();

    vaultAuditService.logUpdate(oldItem, newItem);

    verify(vaultAuditRepository, times(5)).save(any(VaultAudit.class));
  }

  @Test
  void logUpdate_WithPasswordChange_ShouldMaskPasswords() {
    VaultItem oldItem =
        VaultItem.builder()
            .id(1L)
            .name("Test Item")
            .resource("https://example.com")
            .login("testuser")
            .description("Test description")
            .password("oldpassword")
            .collection(testCollection)
            .build();

    VaultItem newItem =
        VaultItem.builder()
            .id(1L)
            .name("Test Item")
            .resource("https://example.com")
            .login("testuser")
            .description("Test description")
            .password("newpassword")
            .collection(testCollection)
            .build();

    vaultAuditService.logUpdate(oldItem, newItem);

    ArgumentCaptor<VaultAudit> auditCaptor = ArgumentCaptor.forClass(VaultAudit.class);
    verify(vaultAuditRepository).save(auditCaptor.capture());

    VaultAudit savedAudit = auditCaptor.getValue();
    assertThat(savedAudit.getFieldName()).isEqualTo("password");
    assertThat(savedAudit.getOldValue()).isEqualTo("***");
    assertThat(savedAudit.getNewValue()).isEqualTo("***");
  }

  @Test
  void logUpdate_WithNoChanges_ShouldNotSaveAnyAudits() {
    vaultAuditService.logUpdate(testVaultItem, testVaultItem);

    verify(vaultAuditRepository, never()).save(any(VaultAudit.class));
  }

  @Test
  void logDelete_ShouldSaveAuditLogWithDeleteAction() {
    // Mock authentication
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            "test@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    vaultAuditService.logDelete(1L);

    ArgumentCaptor<VaultAudit> auditCaptor = ArgumentCaptor.forClass(VaultAudit.class);
    verify(vaultAuditRepository).save(auditCaptor.capture());

    VaultAudit savedAudit = auditCaptor.getValue();
    assertThat(savedAudit.getVaultItemId()).isEqualTo(1L);
    assertThat(savedAudit.getActionType()).isEqualTo(VaultAudit.ActionType.DELETE);
    assertThat(savedAudit.getFieldName()).isEqualTo("*");
    assertThat(savedAudit.getOldValue()).isEqualTo("Item deleted");
    assertThat(savedAudit.getNewValue()).isNull();
    assertThat(savedAudit.getChangedBy()).isEqualTo("test@example.com");
  }

  @Test
  void getAuditHistory_ShouldReturnAuditHistoryFromRepository() {
    List<VaultAudit> expectedAudits =
        List.of(
            VaultAudit.builder()
                .vaultItemId(1L)
                .actionType(VaultAudit.ActionType.CREATE)
                .changedAt(LocalDateTime.now())
                .build());

    when(vaultAuditRepository.findByVaultItemIdOrderByChangedAtDesc(1L)).thenReturn(expectedAudits);

    List<VaultAudit> result = vaultAuditService.getAuditHistory(1L);

    assertThat(result).isEqualTo(expectedAudits);
    verify(vaultAuditRepository).findByVaultItemIdOrderByChangedAtDesc(1L);
  }

  @Test
  void getUserAuditHistory_ShouldReturnUserAuditHistoryFromRepository() {
    List<VaultAudit> expectedAudits =
        List.of(
            VaultAudit.builder()
                .vaultItemId(1L)
                .actionType(VaultAudit.ActionType.CREATE)
                .changedBy("test@example.com")
                .changedAt(LocalDateTime.now())
                .build());

    when(vaultAuditRepository.findByChangedByOrderByChangedAtDesc("test@example.com"))
        .thenReturn(expectedAudits);

    List<VaultAudit> result = vaultAuditService.getUserAuditHistory("test@example.com");

    assertThat(result).isEqualTo(expectedAudits);
    verify(vaultAuditRepository).findByChangedByOrderByChangedAtDesc("test@example.com");
  }

  @Test
  void logUpdate_WithCollectionChange_ShouldLogCollectionNames() {
    Collection oldCollection = new Collection();
    oldCollection.setId(1L);
    oldCollection.setName("Old Collection");

    Collection newCollection = new Collection();
    newCollection.setId(2L);
    newCollection.setName("New Collection");

    VaultItem oldItem =
        VaultItem.builder()
            .id(1L)
            .name("Test Item")
            .resource("https://example.com")
            .login("testuser")
            .description("Test description")
            .password("testpassword")
            .collection(oldCollection)
            .build();

    VaultItem newItem =
        VaultItem.builder()
            .id(1L)
            .name("Test Item")
            .resource("https://example.com")
            .login("testuser")
            .description("Test description")
            .password("testpassword")
            .collection(newCollection)
            .build();

    vaultAuditService.logUpdate(oldItem, newItem);

    ArgumentCaptor<VaultAudit> auditCaptor = ArgumentCaptor.forClass(VaultAudit.class);
    verify(vaultAuditRepository).save(auditCaptor.capture());

    VaultAudit savedAudit = auditCaptor.getValue();
    assertThat(savedAudit.getFieldName()).isEqualTo("collection");
    assertThat(savedAudit.getOldValue()).isEqualTo("Old Collection");
    assertThat(savedAudit.getNewValue()).isEqualTo("New Collection");
  }

  @Test
  void getCurrentUser_WithNoAuthentication_ShouldReturnSystem() {
    // Setup a failing authentication scenario
    when(securityContext.getAuthentication()).thenReturn(null);

    vaultAuditService.logCreate(testVaultItem);

    ArgumentCaptor<VaultAudit> auditCaptor = ArgumentCaptor.forClass(VaultAudit.class);
    verify(vaultAuditRepository).save(auditCaptor.capture());

    VaultAudit savedAudit = auditCaptor.getValue();
    assertThat(savedAudit.getChangedBy()).isEqualTo("system");
  }
}
