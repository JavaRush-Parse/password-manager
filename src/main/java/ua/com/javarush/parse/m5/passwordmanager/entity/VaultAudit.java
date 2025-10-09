package ua.com.javarush.parse.m5.passwordmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vault_audit")
public class VaultAudit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "vault_item_id", nullable = false)
  private Long vaultItemId;

  @Column(name = "action_type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private ActionType actionType;

  @Column(name = "field_name", length = 50)
  private String fieldName;

  @Column(name = "old_value", columnDefinition = "TEXT")
  private String oldValue;

  @Column(name = "new_value", columnDefinition = "TEXT")
  private String newValue;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;

  @Column(name = "changed_by", length = 100)
  private String changedBy;

  @PrePersist
  protected void onCreate() {
    changedAt = LocalDateTime.now();
  }

  public enum ActionType {
    CREATE,
    UPDATE,
    DELETE
  }
}
