package dev.adambertalan.interview.wecan.controller;

import dev.adambertalan.interview.wecan.controller.dto.CreateVoucherRequestBody;
import dev.adambertalan.interview.wecan.controller.dto.CreateVoucherResponseBody;
import dev.adambertalan.interview.wecan.controller.dto.RedeemVoucherRequestBody;
import dev.adambertalan.interview.wecan.controller.dto.RedeemVoucherResponseBody;
import dev.adambertalan.interview.wecan.entity.RedemptionEntity;
import dev.adambertalan.interview.wecan.entity.UserEntity;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import dev.adambertalan.interview.wecan.entity.VoucherType;
import dev.adambertalan.interview.wecan.repository.UserRepository;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.VoucherService;
import dev.adambertalan.interview.wecan.service.VoucherTypeSpecificData;
import dev.adambertalan.interview.wecan.service.exception.UserNotFoundException;
import dev.adambertalan.interview.wecan.service.exception.VoucherNotFoundException;
import dev.adambertalan.interview.wecan.service.exception.VoucherTypeNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("voucher")
public class VoucherController {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private List<VoucherService> voucherServices;

    /**
     * Redeem a single voucher.
     */
    @PostMapping("redeem")
    @ResponseBody
    public RedeemVoucherResponseBody redeem(@RequestBody RedeemVoucherRequestBody body) {
        String code = body.getVoucherCode();
        String userName = body.getUserName();

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with name '%s' not found!", userName)));
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException(String.format("Voucher with code: '%s' not found!", code)));
        VoucherType type = voucher.getVoucherType();

        VoucherService applicableService = voucherServices.stream()
                .filter(service -> service.isApplicableForVoucherType(type))
                .findFirst()
                .orElseThrow(() -> new VoucherTypeNotSupportedException("There is no appropriate service for given type"));

        applicableService.redeem(user, code);

        RedeemVoucherResponseBody responseBody = new RedeemVoucherResponseBody();
        responseBody.setCode(code);
        responseBody.setUserName(userName);
        responseBody.setSuccessful(Boolean.TRUE);

        return responseBody;
    }

    /**
     * Creates a specific type of voucher.
     */
    @PostMapping("create")
    @ResponseBody
    public CreateVoucherResponseBody create(@RequestBody CreateVoucherRequestBody body, HttpServletResponse response) {
        VoucherType type = body.getType();
        String userName = body.getUserName();

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with name '%s' not found!", userName)));

        VoucherService applicableService = voucherServices.stream()
                .filter(service -> service.isApplicableForVoucherType(type))
                .findFirst()
                .orElseThrow(() -> new VoucherTypeNotSupportedException("There is no appropriate service for given type"));

        VoucherTypeSpecificData data = applicableService.convertTypeSpecificData(body.getTypeSpecificData());

        VoucherEntity voucher = applicableService.create(user, data, body.getCode());

        CreateVoucherResponseBody responseBody = new CreateVoucherResponseBody();
        responseBody.setSuccessful(Boolean.TRUE);
        responseBody.setCode(voucher.getCode());

        response.setStatus(HttpStatus.CREATED.value());
        return responseBody;
    }
}
