package dev.adambertalan.interview.wecan.service;

import dev.adambertalan.interview.wecan.entity.RedemptionEntity;
import dev.adambertalan.interview.wecan.entity.UserEntity;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import dev.adambertalan.interview.wecan.entity.VoucherType;
import dev.adambertalan.interview.wecan.service.exception.*;

import java.util.Map;

public interface VoucherService<T extends VoucherTypeSpecificData> {

    Boolean isApplicableForVoucherType(VoucherType type);
    /**
     * Redeem a single voucher by user.
     */
    RedemptionEntity redeem(UserEntity user, String code) throws VoucherNotFoundException, RedeemFailedException;

    /**
     * Create a specific type of voucher.
     */
    VoucherEntity create(UserEntity user, T data, String code) throws VoucherAlreadyExistsException, AuthorizationException;

    VoucherTypeSpecificData convertTypeSpecificData(Map<String, String> typeSpecificData) throws InvalidVoucherTypeDataException;
}
