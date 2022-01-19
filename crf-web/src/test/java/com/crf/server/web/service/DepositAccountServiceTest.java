package com.crf.server.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.repository.DepositAccountPaymentRepository;
import com.crf.server.base.repository.DepositAccountRepository;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.web.CrfWebApplication;

@ActiveProfiles("test")
@SpringBootTest(classes = { CrfWebApplication.class })
public class DepositAccountServiceTest {

    @Autowired
    @InjectMocks
    private DepositAccountService           depositAccountService;
    @Mock
    private DepositAccountPaymentRepository depositAccountPaymentRepository;
    @Mock
    private DepositAccountRepository        depositAccountRepository;
    @Mock
    private CustomerService                 customerService;

    @BeforeEach
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
        when(customerService.getCustomerById(1L)).thenReturn(generateCustomer());
    }

    @ParameterizedTest
    @MethodSource("withdrawalDataProvider")
    void testWithdrawalAmount(DepositAccount depositAccount, LocalDate localDateWithdrawalRequest, double expectedWithdrawal) throws Exception {

        ArgumentCaptor<DepositAccount> depositAccountArgumentCaptor = ArgumentCaptor.forClass(DepositAccount.class);
        depositAccountService.requestWithdrawal(1L, depositAccount, localDateWithdrawalRequest);
        verify(depositAccountRepository, times(1)).save(depositAccountArgumentCaptor.capture());
        assertEquals(expectedWithdrawal, depositAccountArgumentCaptor.getValue().getDepositWithdrawalAmount().doubleValue());
    }

    private Customer generateCustomer() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Test");
        customer.setLastName("Test");
        return customer;
    }

    static Stream<Arguments> withdrawalDataProvider() throws ParseException {

        DepositAccount depositAccountCase1 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY, "2021-01-01", 5, 2.5, 1, 500);

        DepositAccount depositAccountCase2 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY, "2021-01-01", 6, 3, 2, 1200);

        DepositAccount depositAccountCase3 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY, "2021-01-01", 6, 3, 2, 1200);

        DepositAccount depositAccountCase4 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY, "2021-03-01", 5.5, 3, 2, 1100.15);

        DepositAccount depositAccountCase5 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE, ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY,
            "2021-01-01", 5, 3, 2, 0);

        DepositAccount depositAccountCase6 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE, ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY,
            "2021-01-01", 5, 2.5, 1, 0);

        DepositAccount depositAccountCase7 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE, ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY,
            "2021-01-01", 6, 3, 2, 600);

        DepositAccount depositAccountCase8 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY, "2021-01-01", 6, 3, 2, 897.54);

        DepositAccount depositAccountCase9 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY, "2021-03-10", 6, 3, 2, 302.46);

        DepositAccount depositAccountCase10 = generateDepositAccount(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE,
            ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY, "2021-03-01", 5.5, 3, 2, 461.14);

        return Stream.of(arguments(depositAccountCase1, LocalDate.parse("2022-01-02"), 9950), arguments(depositAccountCase2, LocalDate.parse("2023-02-02"), 9950),
            arguments(depositAccountCase3, LocalDate.parse("2023-02-02"), 9950), arguments(depositAccountCase4, LocalDate.parse("2023-03-03"), 9950),
            arguments(depositAccountCase5, LocalDate.parse("2021-02-15"), 9950), arguments(depositAccountCase6, LocalDate.parse("2021-08-15"), 10104.8),
            arguments(depositAccountCase7, LocalDate.parse("2022-10-15"), 9885.89), arguments(depositAccountCase8, LocalDate.parse("2022-10-15"), 9588.35),
            arguments(depositAccountCase9, LocalDate.parse("2021-10-15"), 9827.54), arguments(depositAccountCase10, LocalDate.parse("2022-02-25"), 9785.56));
    }

    private static DepositAccount generateDepositAccount(int status, int interestPayoutFrequency, String dateStartString, double interestRate,
        double prematureWithdrawalInterestRate, int termYears, double interestEarnedAmount) throws ParseException {

        LocalDate dateStartLocal = LocalDate.parse(dateStartString);
        LocalDate dateMaturityLocal = dateStartLocal.plusYears(termYears);

        Date dateStart = Date.from(dateStartLocal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date dateMaturity = Date.from(dateMaturityLocal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setId(1L);
        depositAccount.setAccountNumber("11111111");
        depositAccount.setInterestPayoutFrequency(interestPayoutFrequency);
        depositAccount.setCustomerId(1L);
        depositAccount.setDateStart(dateStart);
        depositAccount.setDateMaturity(dateMaturity);
        depositAccount.setInterestRate(BigDecimal.valueOf(interestRate));
        depositAccount.setTermYears(BigDecimal.valueOf(termYears));
        depositAccount.setDepositAmount(BigDecimal.valueOf(10000));
        depositAccount.setInterestEarnedAmount(BigDecimal.valueOf(interestEarnedAmount));
        depositAccount.setPrematureWithdrawalMinDays(60);
        depositAccount.setPrematureWithdrawalInterestRate(BigDecimal.valueOf(prematureWithdrawalInterestRate));
        depositAccount.setWithdrawalFee(BigDecimal.valueOf(50));
        depositAccount.setStatus(status);
        return depositAccount;
    }
}
