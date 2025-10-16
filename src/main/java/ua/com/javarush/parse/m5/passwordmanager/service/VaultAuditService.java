package ua.com.javarush.parse.m5.passwordmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultAuditRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@Service
public class VaultAuditService extends BaseUserAwareService {

  private final VaultAuditRepository vaultAuditRepository;

  public VaultAuditService(
      UserRepository userRepository, VaultAuditRepository vaultAuditRepository) {
    super(userRepository);
    this.vaultAuditRepository = vaultAuditRepository;
  }

  @Transactional
  public void logCreate(VaultItem vaultItem) {
    String currentUser = getCurrentUserEmailForAudit();

    VaultAudit audit =
        VaultAudit.builder()
            .vaultItemId(vaultItem.getId())
            .actionType(VaultAudit.ActionType.CREATE)
            .fieldName("*")
            .oldValue(null)
            .newValue(buildItemSnapshot(vaultItem))
            .changedAt(LocalDateTime.now())
            .changedBy(currentUser)
            .build();

    vaultAuditRepository.save(audit);
  }

  @Transactional
  public void logUpdate(VaultItem oldItem, VaultItem newItem) {
    String currentUser = getCurrentUserEmailForAudit();

    if (!Objects.equals(oldItem.getName(), newItem.getName())) {
      logFieldChange(newItem.getId(), "name", oldItem.getName(), newItem.getName(), currentUser);
    }

    if (!Objects.equals(oldItem.getResource(), newItem.getResource())) {
      logFieldChange(
          newItem.getId(), "resource", oldItem.getResource(), newItem.getResource(), currentUser);
    }

    if (!Objects.equals(oldItem.getLogin(), newItem.getLogin())) {
      logFieldChange(newItem.getId(), "login", oldItem.getLogin(), newItem.getLogin(), currentUser);
    }

    if (!Objects.equals(oldItem.getDescription(), newItem.getDescription())) {
      logFieldChange(
          newItem.getId(),
          "description",
          oldItem.getDescription(),
          newItem.getDescription(),
          currentUser);
    }

    if (!Objects.equals(oldItem.getPassword(), newItem.getPassword())) {
      logFieldChange(newItem.getId(), "password", "***", "***", currentUser);
    }

    if (!Objects.equals(oldItem.getCollection(), newItem.getCollection())) {
      String oldCollectionName =
          oldItem.getCollection() != null ? oldItem.getCollection().getName() : null;
      String newCollectionName =
          newItem.getCollection() != null ? newItem.getCollection().getName() : null;
      logFieldChange(
          newItem.getId(), "collection", oldCollectionName, newCollectionName, currentUser);
    }
  }

  @Transactional
  public void logDelete(Long vaultItemId) {
    String currentUser = getCurrentUserEmailForAudit();

    VaultAudit audit =
        VaultAudit.builder()
            .vaultItemId(vaultItemId)
            .actionType(VaultAudit.ActionType.DELETE)
            .fieldName("*")
            .oldValue("Item deleted")
            .newValue(null)
            .changedAt(LocalDateTime.now())
            .changedBy(currentUser)
            .build();

    vaultAuditRepository.save(audit);
  }

  @Transactional(readOnly = true)
  public List<VaultAudit> getAuditHistory(Long vaultItemId) {
    return vaultAuditRepository.findByVaultItemIdOrderByChangedAtDesc(vaultItemId);
  }

  @Transactional(readOnly = true)
  public List<VaultAudit> getUserAuditHistory(String username) {
    return vaultAuditRepository.findByChangedByOrderByChangedAtDesc(username);
  }

  private void logFieldChange(
      Long vaultItemId, String fieldName, String oldValue, String newValue, String changedBy) {
    VaultAudit audit =
        VaultAudit.builder()
            .vaultItemId(vaultItemId)
            .actionType(VaultAudit.ActionType.UPDATE)
            .fieldName(fieldName)
            .oldValue(oldValue)
            .newValue(newValue)
            .changedAt(LocalDateTime.now())
            .changedBy(changedBy)
            .build();

    vaultAuditRepository.save(audit);
  }

  private String getCurrentUserEmailForAudit() {
    try {
      return super.getCurrentUser().getEmail();
    } catch (Exception e) {
      return "system";
    }
  }

  private String buildItemSnapshot(VaultItem item) {
    StringBuilder snapshot = new StringBuilder();
    snapshot.append("name: ").append(item.getName()).append(", ");
    snapshot.append("resource: ").append(item.getResource()).append(", ");
    snapshot.append("login: ").append(item.getLogin()).append(", ");
    snapshot.append("description: ").append(item.getDescription()).append(", ");
    snapshot
        .append("collection: ")
        .append(item.getCollection() != null ? item.getCollection().getName() : "none");
    return snapshot.toString();
  }
}
