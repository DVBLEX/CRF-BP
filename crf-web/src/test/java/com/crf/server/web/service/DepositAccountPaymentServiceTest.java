package com.crf.server.web.service;

import static com.crf.server.base.common.ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY;
import static com.crf.server.base.common.ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY;
import static com.crf.server.base.common.ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import com.crf.server.base.service.DepositAccountPaymentService;
import com.crf.server.web.CrfWebApplication;

@ActiveProfiles("test")
@SpringBootTest(classes = { CrfWebApplication.class })
public class DepositAccountPaymentServiceTest {

    private static SimpleDateFormat         formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    @InjectMocks
    private DepositAccountPaymentService    depositAccountPaymentService;
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
    @MethodSource("dataProvider1YearYearly")
    public void testDepositPayment1YearYearly(DepositAccount depositAccount, BigDecimal expectedTotalInterestEarned) throws Exception {

        depositAccountPaymentService.saveDepositAccountPayment(depositAccount, LocalDate.of(2022, 1, 1));

        ArgumentCaptor<DepositAccount> depositAccountArgumentCaptor = ArgumentCaptor.forClass(DepositAccount.class);
        verify(depositAccountRepository, times(1)).save(depositAccountArgumentCaptor.capture());
        assertEquals(expectedTotalInterestEarned.doubleValue(), depositAccountArgumentCaptor.getValue().getInterestEarnedAmount().doubleValue());
    }

    @ParameterizedTest
    @MethodSource("dataProvider2YearYearly")
    public void testDepositPayment2YearYearly(DepositAccount depositAccount, BigDecimal expectedTotalInterestEarned) throws Exception {

        ArgumentCaptor<DepositAccount> depositAccountArgumentCaptor = ArgumentCaptor.forClass(DepositAccount.class);

        // 1st anniversary
        depositAccountPaymentService.saveDepositAccountPayment(depositAccount, LocalDate.of(2022, 1, 1));
        verify(depositAccountRepository, times(1)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-01-01"));
        // 2nd anniversary
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2023, 1, 1));
        verify(depositAccountRepository, times(2)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2023-01-01"));

        assertEquals(expectedTotalInterestEarned.doubleValue(), depositAccountArgumentCaptor.getValue().getInterestEarnedAmount().doubleValue());
    }

    @ParameterizedTest
    @MethodSource("dataProvider2YearTwiceYearly")
    public void testDepositPayment2YearTwiceYearly(DepositAccount depositAccount, BigDecimal expectedTotalInterestEarned) throws Exception {

        ArgumentCaptor<DepositAccount> depositAccountArgumentCaptor = ArgumentCaptor.forClass(DepositAccount.class);

        // Interest payment 1
        depositAccountPaymentService.saveDepositAccountPayment(depositAccount, LocalDate.of(2021, 7, 1));
        verify(depositAccountRepository, times(1)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2021-07-01"));
        // Interest payment 2
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 1, 1));
        verify(depositAccountRepository, times(2)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-01-01"));
        // Interest payment 3
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 7, 1));
        verify(depositAccountRepository, times(3)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-07-01"));
        // Interest payment 4
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2023, 1, 1));
        verify(depositAccountRepository, times(4)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2023-01-01"));

        assertEquals(expectedTotalInterestEarned.doubleValue(), depositAccountArgumentCaptor.getValue().getInterestEarnedAmount().doubleValue());
    }

    @ParameterizedTest
    @MethodSource("dataProvider2YearQuarterly")
    public void testDepositPayment2YearQuarterly(DepositAccount depositAccount, BigDecimal expectedTotalInterestEarned) throws Exception {
        ArgumentCaptor<DepositAccount> depositAccountArgumentCaptor = ArgumentCaptor.forClass(DepositAccount.class);

        // Interest payment Q1 year 1
        depositAccountPaymentService.saveDepositAccountPayment(depositAccount, LocalDate.of(2021, 4, 1));
        verify(depositAccountRepository, times(1)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2021-04-01"));
        // Interest payment Q2 year 1
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2021, 7, 1));
        verify(depositAccountRepository, times(2)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2021-07-01"));
        // Interest payment Q3 year 1
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2021, 10, 1));
        verify(depositAccountRepository, times(3)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2021-10-01"));
        // Interest payment Q4 year 1
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 1, 1));
        verify(depositAccountRepository, times(4)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-01-01"));
        // Interest payment Q1 year 2
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 4, 1));
        verify(depositAccountRepository, times(5)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-04-01"));
        // Interest payment Q2 year 2
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 7, 1));
        verify(depositAccountRepository, times(6)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-07-01"));
        // Interest payment Q3 year 2
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2022, 10, 1));
        verify(depositAccountRepository, times(7)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2022-10-01"));
        // Interest payment Q4 year 2
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2023, 1, 1));
        verify(depositAccountRepository, times(8)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2023-01-01"));
        // Deposit has matured on 1st March 2023, so add the interest not paid out (accrued interest) for the current quarter until 1st March 2023
        depositAccountPaymentService.saveDepositAccountPayment(depositAccountArgumentCaptor.getValue(), LocalDate.of(2023, 3, 1));
        verify(depositAccountRepository, times(9)).save(depositAccountArgumentCaptor.capture());
        depositAccountArgumentCaptor.getValue().setDateLastInterestPayment(formatter.parse("2023-03-01"));

        assertEquals(expectedTotalInterestEarned.doubleValue(), depositAccountArgumentCaptor.getValue().getInterestEarnedAmount().doubleValue());
    }

    static Stream<Arguments> dataProvider1YearYearly() throws ParseException {

        DepositAccount depositAccountCase1 = generateDepositAccount(DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY, "2021-01-01", "2022-01-01", 5, 1);
        return Stream.of(arguments(depositAccountCase1, BigDecimal.valueOf(500.0)));
    }

    static Stream<Arguments> dataProvider2YearYearly() throws ParseException {

        DepositAccount depositAccountCase2 = generateDepositAccount(DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY, "2021-01-01", "2023-01-01", 6, 2);
        return Stream.of(arguments(depositAccountCase2, BigDecimal.valueOf(1200.0)));
    }

    static Stream<Arguments> dataProvider2YearTwiceYearly() throws ParseException {

        DepositAccount depositAccountCase3 = generateDepositAccount(DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY, "2021-01-01", "2023-01-01", 6, 2);
        return Stream.of(arguments(depositAccountCase3, BigDecimal.valueOf(1200.0)));
    }

    static Stream<Arguments> dataProvider2YearQuarterly() throws ParseException {

        DepositAccount depositAccountCase4 = generateDepositAccount(DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY, "2021-03-01", "2023-03-01", 5.5, 2);
        return Stream.of(arguments(depositAccountCase4, BigDecimal.valueOf(1100.15)));
    }

    private Customer generateCustomer() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Test");
        customer.setLastName("Test");
        return customer;
    }

    private static DepositAccount generateDepositAccount(int interestPayoutFrequency, String dateStartString, String dateMaturityString, double interestRate, int termYears)
        throws ParseException {

        Date dateStart = formatter.parse(dateStartString);
        Date dateMaturity = formatter.parse(dateMaturityString);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setId(1L);
        depositAccount.setAccountNumber("11111111");
        depositAccount.setInterestPayoutFrequency(interestPayoutFrequency);
        depositAccount.setStatus(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE);
        depositAccount.setCustomerId(1L);
        depositAccount.setDateStart(dateStart);
        depositAccount.setDateMaturity(dateMaturity);
        depositAccount.setInterestRate(BigDecimal.valueOf(interestRate));
        depositAccount.setTermYears(BigDecimal.valueOf(termYears));
        depositAccount.setDepositAmount(BigDecimal.valueOf(10000));
        depositAccount.setInterestEarnedAmount(BigDecimal.valueOf(0));
        depositAccount.setPrematureWithdrawalMinDays(60);
        return depositAccount;
    }
}
