package ua.com.javarush.parse.m5.passwordmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;

@DataJpaTest
@ActiveProfiles("test")
public class VaultAuditRepositoryTest {

  @Autowired private VaultAuditRepository vaultAuditRepository;

  private VaultAudit audit1;
  private VaultAudit audit2;
  private VaultAudit audit3;

  @BeforeEach
  void setUp() {
    audit1 =
        VaultAudit.builder()
            .vaultItemId(1L)
            .actionType(VaultAudit.ActionType.CREATE)
            .fieldName("*")
            .newValue("New item created")
            .changedAt(LocalDateTime.now().minusHours(2))
            .changedBy("user1@test.com")
            .build();

    audit2 =
        VaultAudit.builder()
            .vaultItemId(1L)
            .actionType(VaultAudit.ActionType.UPDATE)
            .fieldName("name")
            .oldValue("Old Name")
            .newValue("New Name")
            .changedAt(LocalDateTime.now().minusHours(1))
            .changedBy("user1@test.com")
            .build();

    audit3 =
        VaultAudit.builder()
            .vaultItemId(2L)
            .actionType(VaultAudit.ActionType.DELETE)
            .fieldName("*")
            .oldValue("Item deleted")
            .changedAt(LocalDateTime.now())
            .changedBy("user2@test.com")
            .build();

    vaultAuditRepository.saveAll(List.of(audit1, audit2, audit3));
  }

  @Test
  void findByVaultItemId_ShouldReturnAuditsForSpecificItem() {
    List<VaultAudit> audits = vaultAuditRepository.findByVaultItemId(1L, Sort.by("changedAt"));

    assertThat(audits).hasSize(2);
    assertThat(audits.get(0).getActionType()).isEqualTo(VaultAudit.ActionType.CREATE);
    assertThat(audits.get(1).getActionType()).isEqualTo(VaultAudit.ActionType.UPDATE);
  }

  @Test
  void findByVaultItemIdOrderByChangedAtDesc_ShouldReturnAuditsInDescendingOrder() {
    List<VaultAudit> audits = vaultAuditRepository.findByVaultItemIdOrderByChangedAtDesc(1L);

    assertThat(audits).hasSize(2);
    assertThat(audits.get(0).getActionType()).isEqualTo(VaultAudit.ActionType.UPDATE);
    assertThat(audits.get(1).getActionType()).isEqualTo(VaultAudit.ActionType.CREATE);
  }

  @Test
  void findByChangedByOrderByChangedAtDesc_ShouldReturnAuditsForSpecificUser() {
    List<VaultAudit> audits =
        vaultAuditRepository.findByChangedByOrderByChangedAtDesc("user1@test.com");

    assertThat(audits).hasSize(2);
    assertThat(audits).allMatch(audit -> audit.getChangedBy().equals("user1@test.com"));
  }

  @Test
  void findByVaultItemId_WithNonExistentId_ShouldReturnEmptyList() {
    List<VaultAudit> audits = vaultAuditRepository.findByVaultItemId(999L, Sort.by("changedAt"));

    assertThat(audits).isEmpty();
  }
}
