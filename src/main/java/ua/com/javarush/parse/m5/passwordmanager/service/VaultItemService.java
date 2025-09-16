package ua.com.javarush.parse.m5.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItemIdentifier;
import ua.com.javarush.parse.m5.passwordmanager.exception.VaultItemImportException;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VaultItemService {

  private final VaultItemRepository vaultItemRepository;

  public VaultItem save(VaultItem vaultItem) {
    return vaultItemRepository.save(vaultItem);
  }

    @Transactional
    public Optional<VaultItem> update(Long id, VaultItem updatedItemData) {
        return vaultItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setName(updatedItemData.getName());
                    existingItem.setResource(updatedItemData.getResource());
                    existingItem.setLogin(updatedItemData.getLogin());
                    existingItem.setDescription(updatedItemData.getDescription());
                    existingItem.setCollection(updatedItemData.getCollection());
                    if (updatedItemData.getPassword() != null && !updatedItemData.getPassword().isEmpty()) {
                        existingItem.setPassword(updatedItemData.getPassword());
                    }
                    return vaultItemRepository.save(existingItem);
                });
    }

  @Transactional(readOnly = true)
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
      VaultItemIdentifier identifier = new VaultItemIdentifier(vaultItem.getResource(), vaultItem.getLogin());

      if (vaultItem.getLogin() == null || vaultItem.getLogin().isBlank()) {
        errors.add("Vault item with name '" + vaultItem.getName() + "' has an empty login.");
      }
      if (vaultItem.getResource() == null || vaultItem.getResource().isBlank()) {
        errors.add("Vault item with name '" + vaultItem.getName() + "' has an empty resource.");
      }
      if (seen.contains(identifier)) {
        errors.add("Duplicate entry in import list: " + vaultItem.getName() + " - " + vaultItem.getResource() + " - " + vaultItem.getLogin());
      } else {
        seen.add(identifier);
      }
      if (vaultItemRepository.findByResourceAndLogin(vaultItem.getResource(), vaultItem.getLogin())) {
        errors.add("Entry already exists in database: " + vaultItem.getName() + " - " + vaultItem.getResource() + " - " + vaultItem.getLogin());
      }
    }

    if (!errors.isEmpty()) {
      throw new VaultItemImportException(errors);
    }

    return vaultItemRepository.saveAll(vaultItems);
  }

  public Optional<VaultItem> findById(Long id){
    return vaultItemRepository.findById(id);
  }

  public List<VaultItem> findByLogin(String login){
    return vaultItemRepository.findVaultItemByLogin(login);
  }

  public List<VaultItem> findByResource(String resource){ return  vaultItemRepository.findVaultItemByResource(resource);}

  public List<VaultItem> findByCollectionName(String collectionName){ return vaultItemRepository.findVaultItemByCollectionName(collectionName);}

  public void deleteById(Long id){
    vaultItemRepository.deleteById(id);
  }

}
