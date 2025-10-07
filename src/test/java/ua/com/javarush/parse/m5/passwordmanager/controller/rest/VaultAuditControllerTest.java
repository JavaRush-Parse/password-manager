package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;

@ExtendWith(MockitoExtension.class)
public class VaultAuditControllerTest {

  @Mock private VaultAuditService vaultAuditService;

  @InjectMocks private VaultAuditController vaultAuditController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(vaultAuditController).build();
  }

  @Test
  void getAuditHistory_ShouldReturnAuditHistory() throws Exception {
    List<VaultAudit> auditHistory =
        List.of(
            VaultAudit.builder()
                .id(1L)
                .vaultItemId(1L)
                .actionType(VaultAudit.ActionType.CREATE)
                .fieldName("*")
                .newValue("Item created")
                .changedAt(LocalDateTime.now())
                .changedBy("test@example.com")
                .build(),
            VaultAudit.builder()
                .id(2L)
                .vaultItemId(1L)
                .actionType(VaultAudit.ActionType.UPDATE)
                .fieldName("name")
                .oldValue("Old Name")
                .newValue("New Name")
                .changedAt(LocalDateTime.now())
                .changedBy("test@example.com")
                .build());

    when(vaultAuditService.getAuditHistory(1L)).thenReturn(auditHistory);

    mockMvc
        .perform(get("/api/v1/vault-audit/item/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].vaultItemId").value(1))
        .andExpect(jsonPath("$[0].actionType").value("CREATE"))
        .andExpect(jsonPath("$[1].actionType").value("UPDATE"))
        .andExpect(jsonPath("$[1].fieldName").value("name"));
  }

  @Test
  void getUserAuditHistory_ShouldReturnUserAuditHistory() throws Exception {
    List<VaultAudit> userAuditHistory =
        List.of(
            VaultAudit.builder()
                .id(1L)
                .vaultItemId(1L)
                .actionType(VaultAudit.ActionType.CREATE)
                .fieldName("*")
                .newValue("Item created")
                .changedAt(LocalDateTime.now())
                .changedBy("test@example.com")
                .build());

    when(vaultAuditService.getUserAuditHistory("test@example.com")).thenReturn(userAuditHistory);

    mockMvc
        .perform(
            get("/api/v1/vault-audit/user/test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].changedBy").value("test@example.com"));
  }

  @Test
  void getAuditHistory_WithEmptyResult_ShouldReturnEmptyArray() throws Exception {
    when(vaultAuditService.getAuditHistory(999L)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/vault-audit/item/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(0));
  }
}
