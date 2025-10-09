package ua.com.javarush.parse.m5.passwordmanager.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultAudit;

@Repository
public interface VaultAuditRepository extends JpaRepository<VaultAudit, Long> {

  List<VaultAudit> findByVaultItemId(Long vaultItemId, Sort sort);

  @Query(
      "SELECT va FROM VaultAudit va WHERE va.vaultItemId = :vaultItemId ORDER BY va.changedAt DESC")
  List<VaultAudit> findByVaultItemIdOrderByChangedAtDesc(@Param("vaultItemId") Long vaultItemId);

  @Query("SELECT va FROM VaultAudit va WHERE va.changedBy = :changedBy ORDER BY va.changedAt DESC")
  List<VaultAudit> findByChangedByOrderByChangedAtDesc(@Param("changedBy") String changedBy);
}
