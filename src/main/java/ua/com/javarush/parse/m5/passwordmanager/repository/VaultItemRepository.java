package ua.com.javarush.parse.m5.passwordmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;

public interface VaultItemRepository extends JpaRepository<VaultItem, Long> {

  List<VaultItem> findVaultItemByLoginAndOwner(String login, User owner);

  List<VaultItem> findAllByOwner(User owner, Sort sort);

  boolean existsByResourceAndLoginAndOwner(String resource, String login, User owner);

  List<VaultItem> findVaultItemByResourceAndOwner(String resource, User owner);

  List<VaultItem> findVaultItemByCollectionNameAndOwner(String collectionName, User owner);

  Optional<VaultItem> findByIdAndOwner(Long id, User owner);

  @Query(
      "SELECT v FROM VaultItem v WHERE v.owner = :owner AND ("
          + "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.login) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
  List<VaultItem> searchByNameResourceOrLoginAndOwner(@Param("searchTerm") String searchTerm, @Param("owner") User owner);

  // Legacy methods - kept for backward compatibility but should be avoided
  @Deprecated
  List<VaultItem> findVaultItemByLogin(String login);

  @Deprecated
  boolean findByResourceAndLogin(String resource, String login);

  @Deprecated
  List<VaultItem> findVaultItemByResource(String resource);

  @Deprecated
  List<VaultItem> findVaultItemByCollectionName(String collectionName);

  @Deprecated
  @Query(
      "SELECT v FROM VaultItem v WHERE "
          + "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.resource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(v.login) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<VaultItem> searchByNameResourceOrLogin(@Param("searchTerm") String searchTerm);
}
