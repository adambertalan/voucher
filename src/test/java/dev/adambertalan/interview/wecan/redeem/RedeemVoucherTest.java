package dev.adambertalan.interview.wecan.redeem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.adambertalan.interview.wecan.controller.dto.RedeemVoucherRequestBody;
import dev.adambertalan.interview.wecan.controller.dto.RedeemVoucherResponseBody;
import dev.adambertalan.interview.wecan.entity.UserEntity;
import dev.adambertalan.interview.wecan.entity.UserRole;
import dev.adambertalan.interview.wecan.entity.VoucherEntity;
import dev.adambertalan.interview.wecan.entity.VoucherType;
import dev.adambertalan.interview.wecan.repository.RedemptionRepository;
import dev.adambertalan.interview.wecan.repository.UserRepository;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.impl.expiring.ExpiringVoucherData;
import dev.adambertalan.interview.wecan.service.impl.xtimes.XTimesRedemptionVoucherData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.test.properties")
public class RedeemVoucherTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @BeforeEach
    public void beforeMethod() {
        redemptionRepository.deleteAll();
        userRepository.deleteAll();
        voucherRepository.deleteAll();
    }

    @Test
    public void redeemSingleRedemptionVoucherTest() throws Exception {
        UserEntity user = createRegularUser("Test");

        VoucherEntity voucher = new VoucherEntity();
        voucher.setVoucherType(VoucherType.SINGLE_REDEMPTION);
        voucher.setCode("TEST-10");
        voucherRepository.save(voucher);

        String serializedRequestBody = createRedeemRequestBody(user, voucher);

        MvcResult result = mvc.perform(post("/voucher/redeem")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();

        RedeemVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, RedeemVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), voucher.getCode());
        Assertions.assertEquals(responseBody.getUserName(), user.getUserName());
        Assertions.assertTrue(responseBody.getSuccessful());

        mvc.perform(post("/voucher/redeem")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        Assertions.assertEquals(redemptionRepository.findAll().size(), 1);
    }

    @Test
    public void redeemMultipleRedemptionVoucherTest() throws Exception {
        UserEntity user = createRegularUser("Test");

        VoucherEntity voucher = new VoucherEntity();
        voucher.setVoucherType(VoucherType.MULTIPLE_REDEMPTION);
        voucher.setCode("TEST-2021");
        voucherRepository.save(voucher);

        String serializedRequestBody = createRedeemRequestBody(user, voucher);

        for (int i = 0; i < 5; i++) {
            MvcResult result = mvc.perform(post("/voucher/redeem")
                    .content(serializedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn();

            String serializedResponseBody = result.getResponse().getContentAsString();

            RedeemVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, RedeemVoucherResponseBody.class);

            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(responseBody.getCode(), voucher.getCode());
            Assertions.assertEquals(responseBody.getUserName(), user.getUserName());
            Assertions.assertTrue(responseBody.getSuccessful());
        }

        Assertions.assertEquals(redemptionRepository.findAll().size(), 5);
    }

    @Test
    public void redeemXTimesRedemptionVoucherTest() throws Exception {
        UserEntity user = createRegularUser("Test");

        VoucherEntity voucher = new VoucherEntity();
        voucher.setVoucherType(VoucherType.X_TIMES_REDEMPTION);
        voucher.setCode("TEST-ONLY-3");
        XTimesRedemptionVoucherData data = new XTimesRedemptionVoucherData(3);
        String serializedData = mapper.writeValueAsString(data);
        voucher.setTypeSpecificData(serializedData);
        voucherRepository.save(voucher);

        String serializedRequestBody = createRedeemRequestBody(user, voucher);

        for (int i = 0; i < 3; i++) {
            MvcResult result = mvc.perform(post("/voucher/redeem")
                    .content(serializedRequestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn();

            String serializedResponseBody = result.getResponse().getContentAsString();

            RedeemVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, RedeemVoucherResponseBody.class);

            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(responseBody.getCode(), voucher.getCode());
            Assertions.assertEquals(responseBody.getUserName(), user.getUserName());
            Assertions.assertTrue(responseBody.getSuccessful());
        }

        mvc.perform(post("/voucher/redeem")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        Assertions.assertEquals(redemptionRepository.findAll().size(), 3);
    }

    @Test
    public void redeemExpiringVoucherTest() throws Exception {
        UserEntity user = createRegularUser("Test");

        VoucherEntity voucher = new VoucherEntity();
        voucher.setVoucherType(VoucherType.REDEEMABLE_BEFORE_DATE);
        voucher.setCode("TEST-1-HOUR");
        ExpiringVoucherData data = new ExpiringVoucherData(LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
        String serializedData = mapper.writeValueAsString(data);
        voucher.setTypeSpecificData(serializedData);
        voucherRepository.save(voucher);

        VoucherEntity expiredVoucher = new VoucherEntity();
        expiredVoucher.setVoucherType(VoucherType.REDEEMABLE_BEFORE_DATE);
        expiredVoucher.setCode("TEST-TOO-LATE");
        ExpiringVoucherData expiredData = new ExpiringVoucherData(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
        String expiredSerializedData = mapper.writeValueAsString(expiredData);
        expiredVoucher.setTypeSpecificData(expiredSerializedData);
        voucherRepository.save(expiredVoucher);

        String serializedRequestBody = createRedeemRequestBody(user, voucher);

        MvcResult result = mvc.perform(post("/voucher/redeem")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();

        RedeemVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, RedeemVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), voucher.getCode());
        Assertions.assertEquals(responseBody.getUserName(), user.getUserName());
        Assertions.assertTrue(responseBody.getSuccessful());

        String expiredSerializedRequestBody = createRedeemRequestBody(user, expiredVoucher);

        mvc.perform(post("/voucher/redeem")
                .content(expiredSerializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        Assertions.assertEquals(redemptionRepository.findAll().size(), 1);
    }

    private UserEntity createRegularUser(String name) {
        UserEntity user = new UserEntity();
        user.setUserName(name);
        user.setRole(UserRole.REGULAR);
        return userRepository.save(user);
    }

    private String createRedeemRequestBody(UserEntity user, VoucherEntity voucher) throws JsonProcessingException {
        RedeemVoucherRequestBody body = new RedeemVoucherRequestBody();
        body.setVoucherCode(voucher.getCode());
        body.setUserName(user.getUserName());
        return mapper.writeValueAsString(body);
    }
}
