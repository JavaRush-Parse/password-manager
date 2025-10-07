package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class VaultControllerWebAuditTest {

  @Mock
  private VaultItemService vaultItemService;

  @Mock
  private CollectionService collectionService;

  @Mock
  private VaultAuditService vaultAuditService;

  @InjectMocks
  private VaultControllerWeb vaultControllerWeb;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(vaultControllerWeb).build();
  }

  @Test
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
  void showAuditHistory_WithNonExistentVaultItem_ShouldRedirectToHome() throws Exception {
    when(vaultItemService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/vault-item/audit/999"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
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
  void showAuditHistoryModal_WithNonExistentVaultItem_ShouldReturnErrorFragment() throws Exception {
    when(vaultItemService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/vault-item/audit-modal/999")
            .header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("component/audit-history-modal :: error"));
  }

}