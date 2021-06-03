package dev.adambertalan.interview.wecan.service.impl.single;

import dev.adambertalan.interview.wecan.entity.*;
import dev.adambertalan.interview.wecan.repository.RedemptionRepository;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.VoucherService;
import dev.adambertalan.interview.wecan.service.VoucherTypeSpecificData;
import dev.adambertalan.interview.wecan.service.VoucherValidator;
import dev.adambertalan.interview.wecan.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SingleRedemptionVoucherService implements VoucherService<SingleRedemptionVoucherData> {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private VoucherValidator voucherValidator;

    @Override
    public Boolean isApplicableForVoucherType(VoucherType type) {
        return VoucherType.SINGLE_REDEMPTION.equals(type);
    }

    @Override
    public RedemptionEntity redeem(UserEntity user, String code) throws VoucherNotFoundException, RedeemFailedException {
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException(String.format("Voucher with code: '%s' not found!", code)));
        List<RedemptionEntity> redemptionsOfVoucher = redemptionRepository.findByVoucher(voucher);

        if (!redemptionsOfVoucher.isEmpty()) {
            throw new RedeemFailedException("Single use voucher has already been used!");
        }

        RedemptionEntity redemption = new RedemptionEntity();
        redemption.setUser(user);
        redemption.setVoucher(voucher);

        return redemptionRepository.save(redemption);
    }

    @Override
    public VoucherEntity create(UserEntity user, SingleRedemptionVoucherData data, String code) throws VoucherAlreadyExistsException, AuthorizationException {
        voucherValidator.checkUserIsAdmin(user);
        voucherValidator.checkVoucherCodeIsUnique(code);

        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode(code);
        voucher.setVoucherType(VoucherType.SINGLE_REDEMPTION);

        return voucherRepository.save(voucher);
    }

    @Override
    public VoucherTypeSpecificData convertTypeSpecificData(Map<String, String> typeSpecificData) throws InvalidVoucherTypeDataException {
        return new SingleRedemptionVoucherData();
    }
}
