package ua.com.javarush.parse.m5.passwordmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;

import java.util.List;

public interface VaultItemRepository extends JpaRepository<VaultItem, Long> {

  List<VaultItem> findVaultItemByLogin(String login);

  boolean findByResourceAndLogin(String resource, String login);

  List<VaultItem> findVaultItemByResource(String resource);

  List<VaultItem> findVaultItemByCollectionName(String collectionName);
}
