package ua.com.javarush.parse.m5.passwordmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;

@DataJpaTest
@DisplayName("VaultItemRepository Tests")
class VaultItemRepositoryTest {

  @Autowired private VaultItemRepository repository;

  @Test
  @DisplayName("Find vault item by login after saving")
  void whenSaved_thenFindsByLogin() {
    // Given
    VaultItem vaultItem = new VaultItem();
    vaultItem.setLogin("testuser");
    vaultItem.setPassword("password");
    repository.save(vaultItem);

    // When
    List<VaultItem> found = repository.findVaultItemByLogin("testuser");

    // Then
    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getLogin()).isEqualTo("testuser");
  }
}
