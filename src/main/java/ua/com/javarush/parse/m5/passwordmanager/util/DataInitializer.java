package ua.com.javarush.parse.m5.passwordmanager.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.com.javarush.parse.m5.passwordmanager.entity.Role;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.RoleRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByRole(Role.RoleName.USER).isEmpty()) {
            System.out.println("Creating ROLE_USER...");
            Role userRole = new Role();
            userRole.setRole(Role.RoleName.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByRole(Role.RoleName.ADMIN).isEmpty()) {
            System.out.println("Creating ROLE_ADMIN...");
            Role adminRole = new Role();
            adminRole.setRole(Role.RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
    }
}
