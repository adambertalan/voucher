package dev.adambertalan.interview.wecan.service.impl.expiring;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class ExpiringVoucherService implements VoucherService<ExpiringVoucherData> {

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
        return VoucherType.REDEEMABLE_BEFORE_DATE.equals(type);
    }

    @Override
    public RedemptionEntity redeem(UserEntity user, String code) throws VoucherNotFoundException, RedeemFailedException {
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException(String.format("Voucher with code: '%s' not found!", code)));

        try {
            ExpiringVoucherData data = mapper.readValue(voucher.getTypeSpecificData(), ExpiringVoucherData.class);

            if (LocalDateTime.now().isAfter(data.getExpiresAt())) {
                throw new RedeemFailedException("Voucher has expired, it cannot be redeemed!");
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
    public VoucherEntity create(UserEntity user, ExpiringVoucherData data, String code) throws VoucherAlreadyExistsException, AuthorizationException {
        voucherValidator.checkUserIsAdmin(user);
        voucherValidator.checkVoucherCodeIsUnique(code);

        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode(code);
        voucher.setVoucherType(VoucherType.REDEEMABLE_BEFORE_DATE);

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
        if (!typeSpecificData.containsKey("expiresAt")) {
            throw new InvalidVoucherTypeDataException("Missing property 'expiresAt' for expiring redeemable voucher type!");
        }
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime expiresAt = LocalDateTime.parse(typeSpecificData.get("expiresAt"), format);
            return new ExpiringVoucherData(expiresAt);
        } catch (DateTimeParseException dpe) {
            throw new InvalidVoucherTypeDataException("Invalid property 'expiresAt' for expiring redeemable voucher type! Value must be a valid date with the following format: 'yyyy-MM-dd HH:mm'");
        }
    }
}
