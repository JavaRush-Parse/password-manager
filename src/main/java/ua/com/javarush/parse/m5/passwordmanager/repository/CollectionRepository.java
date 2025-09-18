package ua.com.javarush.parse.m5.passwordmanager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

  boolean existsByNameIgnoreCase(String name);

  Optional<Collection> findByNameIgnoreCase(String name);
}
