package ua.com.javarush.parse.m5.passwordmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ua.com.javarush.parse.m5.passwordmanager.entity.User;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;

@DataJpaTest
@DisplayName("VaultItemRepository Tests")
class VaultItemRepositoryTest {

  @Autowired private VaultItemRepository repository;
  @Autowired private TestEntityManager entityManager;

  @Test
  @DisplayName("Find vault item by login after saving")
  void whenSaved_thenFindsByLogin() {
    // Given
    User owner = new User();
    owner.setUserName("testuser");
    owner.setEmail("test@example.com");
    owner.setPassword("hashedpassword");
    owner = entityManager.persistAndFlush(owner);

    VaultItem vaultItem = new VaultItem();
    vaultItem.setLogin("testuser");
    vaultItem.setPassword("password");
    vaultItem.setOwner(owner);
    repository.save(vaultItem);

    // When
    List<VaultItem> found = repository.findVaultItemByLogin("testuser");

    // Then
    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getLogin()).isEqualTo("testuser");
  }
}
