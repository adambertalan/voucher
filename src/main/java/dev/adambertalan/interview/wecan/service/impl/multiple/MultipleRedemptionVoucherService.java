package dev.adambertalan.interview.wecan.service.impl.multiple;

import dev.adambertalan.interview.wecan.entity.RedemptionEntity;
import dev.adambertalan.interview.wecan.entity.UserEntity;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import dev.adambertalan.interview.wecan.entity.VoucherType;
import dev.adambertalan.interview.wecan.repository.RedemptionRepository;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.VoucherService;
import dev.adambertalan.interview.wecan.service.VoucherTypeSpecificData;
import dev.adambertalan.interview.wecan.service.VoucherValidator;
import dev.adambertalan.interview.wecan.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MultipleRedemptionVoucherService implements VoucherService<MultipleRedemptionVoucherData> {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private VoucherValidator voucherValidator;

    @Override
    public Boolean isApplicableForVoucherType(VoucherType type) {
        return VoucherType.MULTIPLE_REDEMPTION.equals(type);
    }

    @Override
    public RedemptionEntity redeem(UserEntity user, String code) throws VoucherNotFoundException, RedeemFailedException {
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException(String.format("Voucher with code: '%s' not found!", code)));

        RedemptionEntity redemption = new RedemptionEntity();
        redemption.setUser(user);
        redemption.setVoucher(voucher);

        return redemptionRepository.save(redemption);
    }

    @Override
    public VoucherEntity create(UserEntity user, MultipleRedemptionVoucherData data, String code) throws VoucherAlreadyExistsException, AuthorizationException {
        voucherValidator.checkUserIsAdmin(user);
        voucherValidator.checkVoucherCodeIsUnique(code);

        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode(code);
        voucher.setVoucherType(VoucherType.MULTIPLE_REDEMPTION);

        return voucherRepository.save(voucher);
    }

    @Override
    public VoucherTypeSpecificData convertTypeSpecificData(Map<String, String> typeSpecificData) throws InvalidVoucherTypeDataException {
        return new MultipleRedemptionVoucherData();
    }
}
