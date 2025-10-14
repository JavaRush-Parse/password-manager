package ua.com.javarush.parse.m5.passwordmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

@Entity
@SoftDelete
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VaultItem implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private String resource;

  private String description;

  private String login;

  private String password;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection_id")
  private Collection collection;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    VaultItem vaultItem = (VaultItem) o;
    return id == vaultItem.id
        && Objects.equals(name, vaultItem.name)
        && Objects.equals(resource, vaultItem.resource)
        && Objects.equals(description, vaultItem.description)
        && Objects.equals(login, vaultItem.login)
        && Objects.equals(password, vaultItem.password)
        && Objects.equals(collection, vaultItem.collection);
  }

  @Override
  public int hashCode() {
    int result = Long.hashCode(id);
    result = 31 * result + Objects.hashCode(name);
    result = 31 * result + Objects.hashCode(resource);
    result = 31 * result + Objects.hashCode(description);
    result = 31 * result + Objects.hashCode(login);
    result = 31 * result + Objects.hashCode(password);
    result = 31 * result + Objects.hashCode(collection);
    return result;
  }
}
