package ua.com.javarush.parse.m5.passwordmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "collections")
@SoftDelete
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Collection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Name is required.")
  @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters.")
  @Column(nullable = false, length = 50)
  private String name;

  @Size(max = 7, message = "Colour must be a valid hex code (e.g., #RRGGBB).")
  @Column(length = 7)
  private String color;

  private String icon;

  @Size(max = 200, message = "Description cannot exceed 200 characters.")
  @Column(length = 200)
  private String description;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  @JsonIgnore
  private User owner;

  @JsonIgnore
  @OneToMany(
      mappedBy = "collection",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<VaultItem> vaultItems = new HashSet<>();

  public void addVaultItem(VaultItem item) {
    vaultItems.add(item);
    item.setCollection(this);
  }

  public void removeVaultItem(VaultItem item) {
    vaultItems.remove(item);
    item.setCollection(null);
  }
}
