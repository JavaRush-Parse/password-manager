package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@WebMvcTest(VaultControllerWeb.class)
@Import(VaultControllerWebTest.TestConfig.class)
@DisplayName("VaultControllerWeb Tests")
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

    @Bean
    public VaultAuditService vaultAuditService() {
      return mock(VaultAuditService.class);
    }

    @Bean
    public JwtUtil jwtUtil() {
      return mock(JwtUtil.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private VaultItemService service;

  @Test
  @WithMockUser
  @DisplayName("Get vault item by ID")
  void whenGetById_thenReturnsVaultView() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.findById(1L)).thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/vault-item/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("vault"))
        .andExpect(model().attributeExists("vaultItem"))
        .andExpect(model().attribute("vaultItem", item));
  }

  @Test
  @WithMockUser
  @DisplayName("Show create vault item form")
  void whenShowCreateForm_thenReturnsCreateVaultView() throws Exception {
    mockMvc
        .perform(get("/vault-item/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("create-vault"))
        .andExpect(model().attributeExists("vault"));
  }

  @Test
  @WithMockUser
  @DisplayName("Save new vault item and redirect to home")
  void whenSaveNewItem_thenRedirectsHome() throws Exception {
    mockMvc
        .perform(post("/vault-item/save").with(csrf()).flashAttr("vault", new VaultItem()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser
  @DisplayName("Save new vault item with HTMX and return empty string")
  void whenSaveNewItemHtmx_thenReturnsEmptyString() throws Exception {
    mockMvc
        .perform(
            post("/vault-item/save")
                .with(csrf())
                .header("HX-Request", "true")
                .flashAttr("vault", new VaultItem()))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @BeforeEach
  void setUp() {
    reset(service);
  }

  @Test
  @WithMockUser
  @DisplayName("Show edit vault item form")
  void whenShowEditForm_thenReturnsEditVaultView() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.findById(1L)).thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/vault-item/edit/{id}", 1L).header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-vault :: modal"))
        .andExpect(model().attributeExists("vault"));
  }

  @Test
  @WithMockUser
  @DisplayName("Show edit vault item form - Not Found")
  void whenShowEditForm_thenReturnsRedirectsHome() throws Exception {

    when(service.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/vault-item/edit/{id}", 1L).header("HX-Request", "true"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser
  @DisplayName("Update vault item and redirect to home")
  void whenUpdateItem_thenRedirectsHome() throws Exception {
    mockMvc
        .perform(post("/vault-item/update").with(csrf()).flashAttr("vault", new VaultItem()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser
  @DisplayName("Delete vault item and redirect to home")
  void whenDeleteItem_thenRedirectsHome() throws Exception {
    mockMvc
        .perform(delete("/vault-item/delete/{id}", 1L).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser
  @DisplayName("Show create vault modal with HTMX")
  void whenShowCreateFormModal_withHxRequest_thenReturnsModalFragment() throws Exception {
    mockMvc
        .perform(get("/vault-item/create-modal").header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("component/create-vault-modal :: modal"))
        .andExpect(model().attributeExists("vault"))
        .andExpect(model().attributeExists("collections"));
  }

  @Test
  @WithMockUser
  @DisplayName("Save new vault item with HTMX - Error handling")
  void whenSaveNewItemHtmx_withException_thenReturnsError() throws Exception {
    when(service.save(any(VaultItem.class))).thenThrow(new RuntimeException("Save failed"));

    mockMvc
        .perform(
            post("/vault-item/save")
                .with(csrf())
                .header("HX-Request", "true")
                .flashAttr("vault", new VaultItem()))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error saving item: Save failed"));
  }

  @Test
  @WithMockUser
  @DisplayName("Update vault item with HTMX and return empty string")
  void whenUpdateItemHTMX_thenReturnsEmptyString() throws Exception {
    mockMvc
        .perform(
            post("/vault-item/update")
                .with(csrf())
                .header("HX-Request", "true")
                .flashAttr("vault", new VaultItem()))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  @WithMockUser
  @DisplayName("Update vault item with HTMX - Error handling")
  void whenUpdateItemHTMX_withException_thenReturnsError() throws Exception {
    when(service.update(any(VaultItem.class))).thenThrow(new RuntimeException("Update failed"));

    mockMvc
        .perform(
            post("/vault-item/update")
                .with(csrf())
                .header("HX-Request", "true")
                .flashAttr("vault", new VaultItem()))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error updating item: Update failed"));
  }

  @Test
  @WithMockUser
  @DisplayName("Get vault item by ID - Item not found")
  void whenGetById_withNonExistentId_thenReturnsViewWithoutAttribute() throws Exception {
    when(service.findById(999L)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/vault-item/{id}", 999L))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }
}
