package ua.com.javarush.parse.m5.passwordmanager.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

  boolean existsByNameIgnoreCase(String name);

  Optional<Collection> findByNameIgnoreCase(String name);

  List<Collection> findByOwner(User owner, Sort sort);

  boolean existsByNameIgnoreCaseAndOwner(String name, User owner);

  Optional<Collection> findByNameIgnoreCaseAndOwner(String name, User owner);

  Optional<Collection> findByIdAndOwner(Long id, User owner);
}
