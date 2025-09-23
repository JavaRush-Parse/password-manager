package ua.com.javarush.parse.m5.passwordmanager.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.javarush.parse.m5.passwordmanager.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRole(Role.RoleName role);
}
