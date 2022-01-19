package com.crf.server.web.restcontroller;

import com.crf.server.web.CrfWebApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static com.crf.server.web.config.TestData.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = { CrfWebApplication.class})
class DepositProductRESTControllerIntegrationTest {
    private static final String CALC_INTEREST_URL = "/depositproduct/calc/interest";
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void givenWac_whenServletContext_thenItProvidesDepositProductRESTController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("depositProductRESTController"));
    }

    @ParameterizedTest
    @MethodSource("yearsRequestAndInterestProvider")
    void verifyCalcInterestResponse(int years, String request, String expectedInterest) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        Date nextYear = cal.getTime();
        String expectedDateString = simpleDateFormat.format(nextYear);

        this.mockMvc.perform(post(CALC_INTEREST_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.depositAmount").value("10000.00"))
            .andExpect(jsonPath("$.data.dateMaturityString").value(expectedDateString))
            .andExpect(jsonPath("$.data.totalInterest").value(expectedInterest));
    }

    static Stream<Arguments> yearsRequestAndInterestProvider() {
        return Stream.of(
            arguments(1, CALC_INTEREST_5_TERM_1Y_REQUEST, "500.00"),
            arguments(2, CALC_INTEREST_6_TERM_2Y_REQUEST, "1200.00"),
            arguments(2, CALC_INTEREST_5_5_TERM_2Y_REQUEST, "1100.00")
        );
    }

}
