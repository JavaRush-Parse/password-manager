package ua.com.javarush.parse.m5.passwordmanager.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.javarush.parse.m5.passwordmanager.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Role.RoleName role);
}
