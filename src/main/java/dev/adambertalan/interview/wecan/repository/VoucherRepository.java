package dev.adambertalan.interview.wecan.repository;

import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    Optional<VoucherEntity> findByCode(String code);
}
