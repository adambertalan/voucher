package dev.adambertalan.interview.wecan.service;

import dev.adambertalan.interview.wecan.entity.UserEntity;
import dev.adambertalan.interview.wecan.entity.UserRole;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.exception.AuthorizationException;
import dev.adambertalan.interview.wecan.service.exception.VoucherAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VoucherValidator {

    @Autowired
    private VoucherRepository voucherRepository;

    public void checkUserIsAdmin(UserEntity user) throws AuthorizationException {
        if (!UserRole.ADMIN.equals(user.getRole())) {
            throw new AuthorizationException("Only admin users can create vouchers!");
        }
    }

    public void checkVoucherCodeIsUnique(String code) throws VoucherAlreadyExistsException {
        Optional<VoucherEntity> existingVoucherWithCode = voucherRepository.findByCode(code);

        if (existingVoucherWithCode.isPresent()) {
            throw new VoucherAlreadyExistsException("A voucher with this code already exists!");
        }
    }
}
