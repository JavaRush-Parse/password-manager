package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultAuditService;

@Tag(
    name = "Vault Audit Management",
    description = "API for viewing audit logs of vault item changes")
@RestController
@RequestMapping("/api/v1/vault-audit")
@RequiredArgsConstructor
public class VaultAuditController {

  private final VaultAuditService vaultAuditService;

  @GetMapping("/item/{vaultItemId}")
  @Operation(summary = "Get audit history for a specific vault item")
  public ResponseEntity<List<VaultAudit>> getAuditHistory(@PathVariable Long vaultItemId) {
    List<VaultAudit> auditHistory = vaultAuditService.getAuditHistory(vaultItemId);
    return ResponseEntity.ok(auditHistory);
  }

  @GetMapping("/user/{username}")
  @Operation(summary = "Get audit history for a specific user")
  public ResponseEntity<List<VaultAudit>> getUserAuditHistory(@PathVariable String username) {
    List<VaultAudit> auditHistory = vaultAuditService.getUserAuditHistory(username);
    return ResponseEntity.ok(auditHistory);
  }
}
