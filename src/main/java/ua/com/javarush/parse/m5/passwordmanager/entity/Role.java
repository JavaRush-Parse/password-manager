package ua.com.javarush.parse.m5.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements GrantedAuthority {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, columnDefinition = "varchar")
  @Enumerated(EnumType.STRING)
  private RoleName role;

  @Override
  public String getAuthority() {
    return role.name();
  }

  public enum RoleName {
    USER,
    ADMIN
  }
}
