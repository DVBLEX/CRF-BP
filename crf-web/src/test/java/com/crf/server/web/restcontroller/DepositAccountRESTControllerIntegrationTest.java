package com.crf.server.web.restcontroller;

import static com.crf.server.web.config.TestData.SAVE_DEPOSIT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crf.server.web.CrfWebApplication;

@ActiveProfiles("test")
@SpringBootTest(classes = { CrfWebApplication.class })
class DepositAccountRESTControllerIntegrationTest {

    private static final String   EXPECTED_DEPOSIT_AMOUNT = "10000.00";
    private static final String   TEST_USER               = "user@test.com";
    private MockMvc               mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void givenWac_whenServletContext_thenItProvidesDepositAccountRESTController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("depositAccountRESTController"));
    }

    @Test
    @WithUserDetails(TEST_USER)
    @Sql(statements = { "TRUNCATE TABLE deposit_accounts" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void verifySaveResponseAndListedResult() throws Exception {
        this.mockMvc.perform(post("/depositaccount/save").contentType(MediaType.APPLICATION_JSON_VALUE).content(SAVE_DEPOSIT_REQUEST)).andDo(print()).andExpect(status().isOk());

        this.mockMvc.perform(post("/depositaccount/list").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).param("page", "0").param("size", "20")).andDo(print())
            .andExpect(status().isOk()).andExpect(jsonPath("$.dataList", hasSize(1))).andExpect(jsonPath("$.dataList[0].depositAmount").value(EXPECTED_DEPOSIT_AMOUNT))
            .andExpect(jsonPath("$.dataList[0].interestPayoutFrequency").value("2")).andExpect(jsonPath("$.dataList[0].termYears").value("1"));
    }
}
