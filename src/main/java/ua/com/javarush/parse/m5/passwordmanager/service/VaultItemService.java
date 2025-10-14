package ua.com.javarush.parse.m5.passwordmanager.service;

import java.util.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItemIdentifier;
import ua.com.javarush.parse.m5.passwordmanager.exception.VaultItemImportException;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;
import ua.com.javarush.parse.m5.passwordmanager.repository.user.UserRepository;

@Service
public class VaultItemService extends BaseUserAwareService {

  private final VaultItemRepository vaultItemRepository;
  private final VaultAuditService vaultAuditService;

  public VaultItemService(
      UserRepository userRepository,
      VaultItemRepository vaultItemRepository,
      VaultAuditService vaultAuditService) {
    super(userRepository);
    this.vaultItemRepository = vaultItemRepository;
    this.vaultAuditService = vaultAuditService;
  }

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public VaultItem save(VaultItem vaultItem) {
    vaultItem.setOwner(getCurrentUser());
    VaultItem savedItem = vaultItemRepository.save(vaultItem);
    vaultAuditService.logCreate(savedItem);
    return savedItem;
  }

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public Optional<VaultItem> update(VaultItem updatedItemData) {
    long id = updatedItemData.getId();
    User currentUser = getCurrentUser();

    return vaultItemRepository
        .findByIdAndOwner(id, currentUser)
        .map(
            existingItem -> {
              VaultItem oldItemCopy =
                  VaultItem.builder()
                      .id(existingItem.getId())
                      .name(existingItem.getName())
                      .resource(existingItem.getResource())
                      .login(existingItem.getLogin())
                      .description(existingItem.getDescription())
                      .password(existingItem.getPassword())
                      .collection(existingItem.getCollection())
                      .owner(existingItem.getOwner())
                      .build();

              existingItem.setName(updatedItemData.getName());
              existingItem.setResource(updatedItemData.getResource());
              existingItem.setLogin(updatedItemData.getLogin());
              existingItem.setDescription(updatedItemData.getDescription());
              existingItem.setCollection(updatedItemData.getCollection());
              if (updatedItemData.getPassword() != null
                  && !updatedItemData.getPassword().isEmpty()) {
                existingItem.setPassword(updatedItemData.getPassword());
              }

              VaultItem savedItem = vaultItemRepository.save(existingItem);
              vaultAuditService.logUpdate(oldItemCopy, savedItem);
              return savedItem;
            });
  }

  @Transactional(readOnly = true)

  @Cacheable(
          value = "vault-items",
          key =
                  "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
  public List<VaultItem> findAll() {
    return vaultItemRepository.findAllByOwner(getCurrentUser(), Sort.by(Sort.Direction.ASC, "id"));
  }

  @org.springframework.transaction.annotation.Transactional(
      rollbackFor = {VaultItemImportException.class},
      isolation = Isolation.READ_COMMITTED)
  public List<VaultItem> importVaultItems(List<VaultItem> vaultItems) {
    List<String> errors = new ArrayList<>();
    Set<VaultItemIdentifier> seen = new HashSet<>();

    for (VaultItem vaultItem : vaultItems) {
      VaultItemIdentifier identifier =
          new VaultItemIdentifier(vaultItem.getResource(), vaultItem.getLogin());

      if (vaultItem.getLogin() == null || vaultItem.getLogin().isBlank()) {
        errors.add("Vault item with name '" + vaultItem.getName() + "' has an empty login.");
      }
      if (vaultItem.getResource() == null || vaultItem.getResource().isBlank()) {
        errors.add("Vault item with name '" + vaultItem.getName() + "' has an empty resource.");
      }
      if (seen.contains(identifier)) {
        errors.add(
            "Duplicate entry in import list: "
                + vaultItem.getName()
                + " - "
                + vaultItem.getResource()
                + " - "
                + vaultItem.getLogin());
      } else {
        seen.add(identifier);
      }
      if (vaultItemRepository.existsByResourceAndLoginAndOwner(
          vaultItem.getResource(), vaultItem.getLogin(), getCurrentUser())) {
        errors.add(
            "Entry already exists in database: "
                + vaultItem.getName()
                + " - "
                + vaultItem.getResource()
                + " - "
                + vaultItem.getLogin());
      }
    }

    if (!errors.isEmpty()) {
      throw new VaultItemImportException(errors);
    }

    User currentUser = getCurrentUser();
    vaultItems.forEach(item -> item.setOwner(currentUser));
    return vaultItemRepository.saveAll(vaultItems);
  }

  @Cacheable(value = "vault-items", key = "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':id:' + #id")
  public Optional<VaultItem> findById(Long id) {
    User currentUser = getCurrentUser();
    return vaultItemRepository.findByIdAndOwner(id, currentUser);
  }

  @Cacheable(value = "vault-items", key = "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':login:' + #login")
  public List<VaultItem> findByLogin(String login) {
    User currentUser = getCurrentUser();
    return vaultItemRepository.findVaultItemByLoginAndOwner(login, currentUser);
  }

  @Cacheable(value = "vault-items", key = "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':resource:' + #resource")
  public List<VaultItem> findByResource(String resource) {
    User currentUser = getCurrentUser();
    return vaultItemRepository.findVaultItemByResourceAndOwner(resource, currentUser);
  }

  @Cacheable(value = "vault-items", key = "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':collection:' + #collectionName")
  public List<VaultItem> findByCollectionName(String collectionName) {
    User currentUser = getCurrentUser();
    return vaultItemRepository.findVaultItemByCollectionNameAndOwner(collectionName, currentUser);
  }

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public void deleteById(Long id) {
    User currentUser = getCurrentUser();
    var vaultItem = vaultItemRepository.findByIdAndOwner(id, currentUser);
    if (vaultItem.isPresent()) {
      vaultAuditService.logDelete(id);
      vaultItemRepository.deleteById(id);
    } else {
      throw new RuntimeException("VaultItem not found or you don't have permission to delete it");
    }
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "vault-items", key = "'user:' + T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + ':search:' + #searchTerm")
  public List<VaultItem> search(String searchTerm) {
    User currentUser = getCurrentUser();
    if (searchTerm == null || searchTerm.isBlank()) {
      return vaultItemRepository.findAllByOwner(currentUser, Sort.by(Sort.Direction.ASC, "id"));
    }
    return vaultItemRepository.searchByNameResourceOrLoginAndOwner(searchTerm, currentUser);
  }
}
