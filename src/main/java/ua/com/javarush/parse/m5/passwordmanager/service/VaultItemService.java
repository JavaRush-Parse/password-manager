package ua.com.javarush.parse.m5.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.exception.EmptyLoginException;
import ua.com.javarush.parse.m5.passwordmanager.exception.EmptyResourceException;
import ua.com.javarush.parse.m5.passwordmanager.exception.ImportEntryDuplicateException;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VaultItemService {

  private final VaultItemRepository vaultItemRepository;

  public VaultItem save(VaultItem vaultItem) {
    return vaultItemRepository.save(vaultItem);
  }


  @Transactional(
          rollbackFor = {EmptyLoginException.class, EmptyResourceException.class, ImportEntryDuplicateException.class,},
          isolation = Isolation.READ_COMMITTED)
  public List<VaultItem> importVaultItems(List<VaultItem> vaultItems) {
    Set<VaultItem> seen = new HashSet<>();

    for (VaultItem vaultItem : vaultItems) {
      if (vaultItem.getLogin().isEmpty() || vaultItem.getLogin().isBlank()) {
        throw new EmptyLoginException();
      } else if (vaultItem.getResource().isEmpty() || vaultItem.getResource().isBlank()) {
        throw new EmptyLoginException();
      } else if (seen.contains(vaultItem)) {
        throw new ImportEntryDuplicateException();
      } else if (vaultItemRepository.findByResourceAndLogin(vaultItem.getResource(), vaultItem.getLogin())) {
        throw new ImportEntryDuplicateException("Login already exists");
      }

      seen.add(vaultItem);
    }

    return vaultItemRepository.saveAll(vaultItems);
  }

  @Transactional(readOnly = true)
  public List<VaultItem> findAll() {
    return vaultItemRepository.findAll();
  }

  public Optional<VaultItem> findById(Long id){
    return vaultItemRepository.findById(id);
  }

  public List<VaultItem> findByLogin(String login){
    return vaultItemRepository.findVaultItemByLogin(login);
  }

  public void deleteById(Long id){
    vaultItemRepository.deleteById(id);
  }

}
