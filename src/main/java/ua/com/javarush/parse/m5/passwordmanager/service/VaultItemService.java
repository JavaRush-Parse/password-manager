package ua.com.javarush.parse.m5.passwordmanager.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.javarush.parse.m5.passwordmanager.dto.PageResponse;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItemIdentifier;
import ua.com.javarush.parse.m5.passwordmanager.exception.VaultItemImportException;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

@Service
@RequiredArgsConstructor
public class VaultItemService {

  private final VaultItemRepository vaultItemRepository;
  private final VaultAuditService vaultAuditService;

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public VaultItem save(VaultItem vaultItem) {
    VaultItem savedItem = vaultItemRepository.save(vaultItem);
    vaultAuditService.logCreate(savedItem);
    return savedItem;
  }

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public Optional<VaultItem> update(VaultItem updatedItemData) {
    long id = updatedItemData.getId();

    return vaultItemRepository
        .findById(id)
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
  @Cacheable(value = "vault-items", key = "'all'")
  public List<VaultItem> findAll() {
    return vaultItemRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
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
      if (vaultItemRepository.findByResourceAndLogin(
          vaultItem.getResource(), vaultItem.getLogin())) {
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

    return vaultItemRepository.saveAll(vaultItems);
  }

  @Cacheable(value = "vault-items", key = "#id")
  public Optional<VaultItem> findById(Long id) {
    return vaultItemRepository.findById(id);
  }

  @Cacheable(value = "vault-items", key = "'login:' + #login")
  public List<VaultItem> findByLogin(String login) {
    return vaultItemRepository.findVaultItemByLogin(login);
  }

  @Cacheable(value = "vault-items", key = "'resource:' + #resource")
  public List<VaultItem> findByResource(String resource) {
    return vaultItemRepository.findVaultItemByResource(resource);
  }

  @Cacheable(value = "vault-items", key = "'collection:' + #collectionName")
  public List<VaultItem> findByCollectionName(String collectionName) {
    return vaultItemRepository.findVaultItemByCollectionName(collectionName);
  }

  @Transactional
  @CacheEvict(value = "vault-items", allEntries = true)
  public void deleteById(Long id) {
    vaultAuditService.logDelete(id);
    vaultItemRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "vault-items", key = "'search:' + #searchTerm")
  public List<VaultItem> search(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return vaultItemRepository.findAll();
    }
    return vaultItemRepository.searchByNameResourceOrLogin(searchTerm);
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "vault-items", key = "'page:' + #page + ':size:' + #size")
  public PageResponse<VaultItem> findAllPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
    Page<VaultItem> vaultItemPage = vaultItemRepository.findAll(pageable);
    return PageResponse.of(vaultItemPage);
  }

  @Transactional(readOnly = true)
  @Cacheable(
      value = "vault-items",
      key = "'search:' + #searchTerm + ':page:' + #page + ':size:' + #size")
  public PageResponse<VaultItem> searchPaginated(String searchTerm, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
    Page<VaultItem> vaultItemPage;
    if (searchTerm == null || searchTerm.isBlank()) {
      vaultItemPage = vaultItemRepository.findAll(pageable);
    } else {
      vaultItemPage = vaultItemRepository.searchByNameResourceOrLogin(searchTerm, pageable);
    }
    return PageResponse.of(vaultItemPage);
  }
}
