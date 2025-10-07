package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.config.SecurityConfig;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VaultControllerWeb.class)
@Import(SecurityConfig.class)
public class VaultControllerWebAuditTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private VaultItemService vaultItemService;

  @MockBean
  private CollectionService collectionService;

  @MockBean
  private VaultAuditService vaultAuditService;

  @Test
  @WithMockUser
  void showAuditHistory_ShouldReturnAuditHistoryView() throws Exception {
    VaultItem vaultItem = VaultItem.builder()
        .id(1L)
        .name("Test Item")
        .resource("https://example.com")
        .login("testuser")
        .build();

    List<VaultAudit> auditHistory = List.of(
        VaultAudit.builder()
            .id(1L)
            .vaultItemId(1L)
            .actionType(VaultAudit.ActionType.CREATE)
            .fieldName("*")
            .newValue("Item created")
            .changedAt(LocalDateTime.now())
            .changedBy("test@example.com")
            .build()
    );

    when(vaultItemService.findById(1L)).thenReturn(Optional.of(vaultItem));
    when(vaultAuditService.getAuditHistory(1L)).thenReturn(auditHistory);

    mockMvc.perform(get("/vault-item/audit/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("audit-history"))
        .andExpect(model().attributeExists("vaultItem"))
        .andExpect(model().attributeExists("auditHistory"))
        .andExpect(model().attribute("vaultItem", vaultItem))
        .andExpect(model().attribute("auditHistory", auditHistory));
  }

  @Test
  @WithMockUser
  void showAuditHistory_WithNonExistentVaultItem_ShouldRedirectToHome() throws Exception {
    when(vaultItemService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/vault-item/audit/999"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser
  void showAuditHistoryModal_ShouldReturnModalFragment() throws Exception {
    VaultItem vaultItem = VaultItem.builder()
        .id(1L)
        .name("Test Item")
        .resource("https://example.com")
        .login("testuser")
        .build();

    List<VaultAudit> auditHistory = List.of(
        VaultAudit.builder()
            .id(1L)
            .vaultItemId(1L)
            .actionType(VaultAudit.ActionType.UPDATE)
            .fieldName("name")
            .oldValue("Old Name")
            .newValue("New Name")
            .changedAt(LocalDateTime.now())
            .changedBy("test@example.com")
            .build()
    );

    when(vaultItemService.findById(1L)).thenReturn(Optional.of(vaultItem));
    when(vaultAuditService.getAuditHistory(1L)).thenReturn(auditHistory);

    mockMvc.perform(get("/vault-item/audit-modal/1")
            .header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("component/audit-history-modal :: modal"))
        .andExpect(model().attributeExists("vaultItem"))
        .andExpect(model().attributeExists("auditHistory"));
  }

  @Test
  @WithMockUser
  void showAuditHistoryModal_WithNonExistentVaultItem_ShouldReturnErrorFragment() throws Exception {
    when(vaultItemService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/vault-item/audit-modal/999")
            .header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("component/audit-history-modal :: error"));
  }

  @Test
  void showAuditHistory_WithoutAuthentication_ShouldRedirectToLogin() throws Exception {
    mockMvc.perform(get("/vault-item/audit/1"))
        .andExpect(status().is3xxRedirection());
  }
}