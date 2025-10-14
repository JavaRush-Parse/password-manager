package ua.com.javarush.parse.m5.passwordmanager.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;

public interface VaultItemRepository extends JpaRepository<VaultItem, Long> {

  List<VaultItem> findVaultItemByLogin(String login);

  boolean findByResourceAndLogin(String resource, String login);

  List<VaultItem> findVaultItemByResource(String resource);

  List<VaultItem> findVaultItemByCollectionName(String collectionName);

  @Query(
      "SELECT v FROM VaultItem v WHERE "
          + "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.login) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<VaultItem> searchByNameResourceOrLogin(@Param("searchTerm") String searchTerm);

  @Query(
      "SELECT v FROM VaultItem v WHERE "
          + "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.login) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  Page<VaultItem> searchByNameResourceOrLogin(
      @Param("searchTerm") String searchTerm, Pageable pageable);
}
