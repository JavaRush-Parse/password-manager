package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@WebMvcTest(VaultControllerWeb.class)
@Import(VaultControllerWebTest.TestConfig.class)
class VaultControllerWebTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public VaultItemService vaultItemService() {
      return mock(VaultItemService.class);
    }

    @Bean
    public CollectionService collectionService() {
      return mock(CollectionService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private VaultItemService service;

  @Test
  void whenGetById_thenReturnsVaultView() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.findById(1L)).thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/vault-item/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("vault"))
        .andExpect(model().attributeExists("vault"));
  }

  @Test
  void whenShowCreateForm_thenReturnsCreateVaultView() throws Exception {
    mockMvc
        .perform(get("/vault-item/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("create-vault"))
        .andExpect(model().attributeExists("vault"));
  }

  @Test
  void whenSaveNewItem_thenRedirectsHome() throws Exception {
    mockMvc
        .perform(post("/vault-item/save").flashAttr("vault", new VaultItem()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void whenShowEditForm_thenReturnsEditVaultView() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.findById(1L)).thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/vault-item/edit/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-vault"))
        .andExpect(model().attributeExists("vault"));
  }

  @Test
  void whenUpdateItem_thenRedirectsHome() throws Exception {
    mockMvc
        .perform(post("/vault-item/update/{id}", 1L).flashAttr("vault", new VaultItem()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }
}
