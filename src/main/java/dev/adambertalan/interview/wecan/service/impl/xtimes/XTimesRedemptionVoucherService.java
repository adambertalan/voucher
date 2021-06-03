package dev.adambertalan.interview.wecan.service.impl.xtimes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.Map;

@Service
public class XTimesRedemptionVoucherService implements VoucherService<XTimesRedemptionVoucherData> {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private VoucherValidator voucherValidator;

    @Override
    public Boolean isApplicableForVoucherType(VoucherType type) {
        return VoucherType.X_TIMES_REDEMPTION.equals(type);
    }

    @Override
    public RedemptionEntity redeem(UserEntity user, String code) throws VoucherNotFoundException, RedeemFailedException {
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException(String.format("Voucher with code: '%s' not found!", code)));

        try {
            XTimesRedemptionVoucherData data = mapper.readValue(voucher.getTypeSpecificData(), XTimesRedemptionVoucherData.class);

            List<RedemptionEntity> redemptionsOfVoucher = redemptionRepository.findByVoucher(voucher);

            if (redemptionsOfVoucher.size() >= data.getTimesRedeemable()) {
                throw new RedeemFailedException("Voucher has been used by the maximum allowed times already!");
            }

            RedemptionEntity redemption = new RedemptionEntity();
            redemption.setUser(user);
            redemption.setVoucher(voucher);

            return redemptionRepository.save(redemption);
        } catch (JsonProcessingException e) {
            throw new RedeemFailedException("Invalid data stored for voucher!", e);
        }
    }

    @Override
    public VoucherEntity create(UserEntity user, XTimesRedemptionVoucherData data, String code) throws VoucherAlreadyExistsException, AuthorizationException {
        voucherValidator.checkUserIsAdmin(user);
        voucherValidator.checkVoucherCodeIsUnique(code);

        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode(code);
        voucher.setVoucherType(VoucherType.X_TIMES_REDEMPTION);

        try {
            String serializedData = mapper.writeValueAsString(data);
            voucher.setTypeSpecificData(serializedData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return voucherRepository.save(voucher);
    }

    @Override
    public VoucherTypeSpecificData convertTypeSpecificData(Map<String, String> typeSpecificData) throws InvalidVoucherTypeDataException {
        if (!typeSpecificData.containsKey("timesRedeemable")) {
            throw new InvalidVoucherTypeDataException("Missing property 'timesRedeemable' for X times redeemable voucher type!");
        }
        try {
            Integer timesRedeemable = Integer.parseInt(typeSpecificData.get("timesRedeemable"));

            if (timesRedeemable <= 0) {
                throw new InvalidVoucherTypeDataException("Invalid property 'timesRedeemable' for X times redeemable voucher type! Value must be positive!");
            }

            return new XTimesRedemptionVoucherData(timesRedeemable);
        } catch (NumberFormatException nfe) {
            throw new InvalidVoucherTypeDataException("Invalid property 'timesRedeemable' for X times redeemable voucher type! Value must be a number");
        }
    }
}
