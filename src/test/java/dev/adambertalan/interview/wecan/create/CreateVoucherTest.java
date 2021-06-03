package dev.adambertalan.interview.wecan.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.adambertalan.interview.wecan.controller.dto.CreateVoucherRequestBody;
import dev.adambertalan.interview.wecan.controller.dto.CreateVoucherResponseBody;
import dev.adambertalan.interview.wecan.entity.*;
import dev.adambertalan.interview.wecan.repository.RedemptionRepository;
import dev.adambertalan.interview.wecan.repository.UserRepository;
import dev.adambertalan.interview.wecan.repository.VoucherRepository;
import dev.adambertalan.interview.wecan.service.impl.expiring.ExpiringVoucherService;
import dev.adambertalan.interview.wecan.service.impl.multiple.MultipleRedemptionVoucherService;
import dev.adambertalan.interview.wecan.service.impl.single.SingleRedemptionVoucherService;
import dev.adambertalan.interview.wecan.service.impl.xtimes.XTimesRedemptionVoucherService;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.test.properties")
public class CreateVoucherTest {

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

    @Autowired
    private SingleRedemptionVoucherService singleRedemptionVoucherService;

    @Autowired
    private MultipleRedemptionVoucherService multipleRedemptionVoucherService;

    @Autowired
    private XTimesRedemptionVoucherService xTimesRedemptionVoucherService;

    @Autowired
    private ExpiringVoucherService expiringVoucherService;

    @BeforeEach
    public void beforeMethod() {
        redemptionRepository.deleteAll();
        userRepository.deleteAll();
        voucherRepository.deleteAll();
    }

    @Test
    public void testCreateSingleRedemptionVoucher() throws Exception {
        UserEntity admin = createAdminUser("Admin");
        UserEntity user = createRegularUser("Test");

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(admin.getUserName());
        body.setCode("SINGLE-99");
        body.setType(VoucherType.SINGLE_REDEMPTION);
        String serializedRequestBody = mapper.writeValueAsString(body);

        MvcResult result = mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();
        CreateVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, CreateVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), "SINGLE-99");
        Assertions.assertTrue(responseBody.getSuccessful());

        RedemptionEntity redemption = singleRedemptionVoucherService.redeem(user, "SINGLE-99");

        Assertions.assertNotNull(redemption);
    }

    @Test
    public void testCreateMultipleRedemptionVoucher() throws Exception {
        UserEntity admin = createAdminUser("Admin");
        UserEntity user = createRegularUser("Test");

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(admin.getUserName());
        body.setCode("ALL-TIME-DISCOUNT");
        body.setType(VoucherType.MULTIPLE_REDEMPTION);
        String serializedRequestBody = mapper.writeValueAsString(body);

        MvcResult result = mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();
        CreateVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, CreateVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), "ALL-TIME-DISCOUNT");
        Assertions.assertTrue(responseBody.getSuccessful());

        RedemptionEntity redemption = multipleRedemptionVoucherService.redeem(user, "ALL-TIME-DISCOUNT");

        Assertions.assertNotNull(redemption);
    }

    @Test
    public void testCreateXTimesRedemptionVoucher() throws Exception {
        UserEntity admin = createAdminUser("Admin");
        UserEntity user = createRegularUser("Test");

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(admin.getUserName());
        body.setCode("TODAY-3");
        body.setType(VoucherType.X_TIMES_REDEMPTION);
        Map<String, String> data = new HashMap<>();
        data.put("timesRedeemable", "3");
        body.setTypeSpecificData(data);
        String serializedRequestBody = mapper.writeValueAsString(body);

        MvcResult result = mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();
        CreateVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, CreateVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), "TODAY-3");
        Assertions.assertTrue(responseBody.getSuccessful());

        RedemptionEntity redemption = xTimesRedemptionVoucherService.redeem(user, "TODAY-3");

        Assertions.assertNotNull(redemption);
    }

    @Test
    public void testCreateExpiringRedemptionVoucher() throws Exception {
        UserEntity admin = createAdminUser("Admin");
        UserEntity user = createRegularUser("Test");

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(admin.getUserName());
        body.setCode("1-MINUTE-SALE");
        body.setType(VoucherType.REDEEMABLE_BEFORE_DATE);
        Map<String, String> data = new HashMap<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime expiresAt = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        data.put("expiresAt", format.format(expiresAt));
        body.setTypeSpecificData(data);
        String serializedRequestBody = mapper.writeValueAsString(body);

        MvcResult result = mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String serializedResponseBody = result.getResponse().getContentAsString();
        CreateVoucherResponseBody responseBody = mapper.readValue(serializedResponseBody, CreateVoucherResponseBody.class);

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getCode(), "1-MINUTE-SALE");
        Assertions.assertTrue(responseBody.getSuccessful());

        RedemptionEntity redemption = expiringVoucherService.redeem(user, "1-MINUTE-SALE");

        Assertions.assertNotNull(redemption);
    }

    @Test
    public void testCreationFailsIfCodeAlreadyExists() throws Exception {
        UserEntity admin = createAdminUser("Admin");

        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode("EXISTING");
        voucher.setVoucherType(VoucherType.SINGLE_REDEMPTION);
        voucherRepository.save(voucher);

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(admin.getUserName());
        body.setCode("EXISTING");
        body.setType(VoucherType.SINGLE_REDEMPTION);
        String serializedRequestBody = mapper.writeValueAsString(body);

        mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testVoucherCanOnlyBeCreatedByAdminUser() throws Exception {
        UserEntity user = createRegularUser("Regular");

        CreateVoucherRequestBody body = new CreateVoucherRequestBody();
        body.setUserName(user.getUserName());
        body.setCode("INFINITE-COFFEE");
        body.setType(VoucherType.SINGLE_REDEMPTION);
        String serializedRequestBody = mapper.writeValueAsString(body);

        mvc.perform(post("/voucher/create")
                .content(serializedRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    private UserEntity createAdminUser(String name) {
        UserEntity user = new UserEntity();
        user.setUserName(name);
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }

    private UserEntity createRegularUser(String name) {
        UserEntity user = new UserEntity();
        user.setUserName(name);
        user.setRole(UserRole.REGULAR);
        return userRepository.save(user);
    }
}
