package ua.com.javarush.parse.m5.passwordmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.repository.VaultItemRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultItemServiceTest {

    @Mock
    private VaultItemRepository repository;

    @InjectMocks
    private VaultItemService service;

    @Test
    void whenSave_thenRepositorySaveIsCalled() {
        VaultItem vaultItem = new VaultItem();
        when(repository.save(any(VaultItem.class))).thenReturn(vaultItem);

        VaultItem saved = service.save(vaultItem);

        assertThat(saved).isNotNull();
        verify(repository).save(vaultItem);
    }

    @Test
    void whenUpdate_thenExistingItemIsUpdated() {
        VaultItem existingItem = new VaultItem();
        existingItem.setId(1L);
        existingItem.setName("Old Name");

        VaultItem updatedData = new VaultItem();
        updatedData.setName("New Name");
        updatedData.setResource("Resource");
        updatedData.setLogin("Login");
        updatedData.setDescription("Description");
        updatedData.setPassword("password");

        when(repository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(repository.save(any(VaultItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<VaultItem> result = service.update(1L, updatedData);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
        verify(repository).findById(1L);
        verify(repository).save(existingItem);
    }

    @Test
    void whenFindAll_thenRepositoryFindAllIsCalled() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(new VaultItem(), new VaultItem()));

        List<VaultItem> result = service.findAll();

        assertThat(result).hasSize(2);
        verify(repository).findAll(any(Sort.class));
    }

    @Test
    void whenFindById_thenRepositoryFindByIdIsCalled() {
        VaultItem vaultItem = new VaultItem();
        when(repository.findById(1L)).thenReturn(Optional.of(vaultItem));

        Optional<VaultItem> result = service.findById(1L);

        assertThat(result).isPresent();
        verify(repository).findById(1L);
    }

    @Test
    void whenFindByLogin_thenRepositoryFindVaultItemByLoginIsCalled() {
        when(repository.findVaultItemByLogin("testuser")).thenReturn(List.of(new VaultItem()));

        List<VaultItem> result = service.findByLogin("testuser");

        assertThat(result).hasSize(1);
        verify(repository).findVaultItemByLogin("testuser");
    }

    @Test
    void whenDeleteById_thenRepositoryDeleteByIdIsCalled() {
        doNothing().when(repository).deleteById(1L);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }
}
