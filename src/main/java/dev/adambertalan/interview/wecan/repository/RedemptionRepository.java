package dev.adambertalan.interview.wecan.repository;

import dev.adambertalan.interview.wecan.entity.RedemptionEntity;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<RedemptionEntity, Long> {

    List<RedemptionEntity> findByVoucher(VoucherEntity voucher);
}
