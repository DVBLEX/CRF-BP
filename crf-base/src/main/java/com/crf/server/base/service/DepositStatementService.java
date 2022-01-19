package com.crf.server.base.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositStatement;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.DepositStatementJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.DepositStatementRepository;

@Service
public class DepositStatementService {

    private DepositStatementRepository depositStatementRepository;

    @Autowired
    public void setDepositStatementRepository(DepositStatementRepository depositStatementRepository) {
        this.depositStatementRepository = depositStatementRepository;
    }

    public PageList<DepositStatementJson> getDepositStatementsByCustomerId(Pageable pageable, long customerId) throws CRFException, Exception {

        List<DepositStatementJson> resultList = new ArrayList<>();

        Page<DepositStatement> depositStatementPage = depositStatementRepository.findAllByCustomerId(customerId, pageable);

        for (DepositStatement depositStatement : depositStatementPage) {

            DepositStatementJson depositStatementJson = new DepositStatementJson();

            depositStatementJson.setCode(depositStatement.getCode());
            depositStatementJson.setType(depositStatement.getType());
            depositStatementJson.setDescription(depositStatement.getDescription());
            depositStatementJson.setAccountNumber(depositStatement.getAccountNumber());
            depositStatementJson.setAmountTransaction(depositStatement.getAmountTransaction().toString());
            depositStatementJson.setAmountBalance(depositStatement.getAmountBalance().toString());
            depositStatementJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyyHHmm, depositStatement.getDateCreated()));

            resultList.add(depositStatementJson);
        }

        return new PageList<>(resultList, new PageInfo(depositStatementPage.getTotalPages(), depositStatementPage.getTotalElements()));
    }

    @Transactional
    public void saveDepositStatement(DepositAccount depositAccount, Integer depositStatementType, BigDecimal amountInterest) throws CRFException, Exception {

        DepositStatement depositStatement = new DepositStatement();
        depositStatement.setCode(SecurityUtil.generateUniqueCode());
        depositStatement.setType(depositStatementType);
        depositStatement.setDepositAccountId(depositAccount.getId());
        depositStatement.setAccountNumber(depositAccount.getAccountNumber());
        depositStatement.setCustomerId(depositAccount.getCustomerId());

        switch (depositStatementType) {
            case ServerConstants.DEPOSIT_STATEMENT_TYPE_DEPOSIT_APPROVED:
                depositStatement.setDescription("Deposit");
                depositStatement.setAmountTransaction(depositAccount.getDepositAmount());
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).add(depositStatement.getAmountTransaction()));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED:
                depositStatement.setDescription(ServerUtil.getInterestFrequencyDescription(depositAccount.getInterestPayoutFrequency()) + " interest earned @ "
                    + depositAccount.getInterestRate().toString() + "%");
                depositStatement.setAmountTransaction(amountInterest);
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).add(depositStatement.getAmountTransaction()));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_PAYMENT:
                depositStatement.setDescription(ServerUtil.getInterestFrequencyDescription(depositAccount.getInterestPayoutFrequency()) + " interest paid");
                depositStatement.setAmountTransaction(amountInterest.negate());
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).subtract(amountInterest));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_OFFSET_DUE_TO_EARLY_WITHDRAWAL:
                // when withdrawing the deposit prematurely,
                // subtract any previous interest paid out at the normal rate
                // then apply the lower interest rate for the active duration of the deposit (DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED_DUE_TO_EARLY_WITHDRAWAL)
                depositStatement.setDescription("Interest payment offset due to premature withdrawal");
                depositStatement.setAmountTransaction(amountInterest.negate());
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).subtract(depositStatement.getAmountTransaction().abs()));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED_DUE_TO_EARLY_WITHDRAWAL:
                depositStatement.setDescription("Interest earned @ " + depositAccount.getPrematureWithdrawalInterestRate().toString() + "% due to premature withdrawal");
                depositStatement.setAmountTransaction(amountInterest);
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).add(depositStatement.getAmountTransaction()));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_WITHDRAWAL_FEE:
                depositStatement.setDescription("Deposit withdrawal fee");
                depositStatement.setAmountTransaction(depositAccount.getWithdrawalFee().negate());
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).subtract(depositAccount.getWithdrawalFee()));
                break;

            case ServerConstants.DEPOSIT_STATEMENT_TYPE_WITHDRAWAL:
                depositStatement.setDescription("Deposit withdrawal");
                depositStatement.setAmountTransaction(depositAccount.getDepositWithdrawalAmount().negate());
                depositStatement.setAmountBalance(getAmountBalanceLast(depositAccount.getId()).subtract(depositAccount.getDepositWithdrawalAmount()));
                break;

            default:
                throw new CRFException(ServerResponseConstants.FAILURE_CODE, "Invalid deposit statement type.", "#saveDepositStatement");
        }

        depositStatement.setDateCreated(new Date());

        depositStatementRepository.save(depositStatement);
    }

    private BigDecimal getAmountBalanceLast(long depositAccountId) {

        BigDecimal amountBalance = BigDecimal.ZERO;
        try {
            amountBalance = depositStatementRepository.getAmountBalanceLast(depositAccountId);

        } catch (Exception e) {
            amountBalance = BigDecimal.ZERO;
        }
        return amountBalance == null ? BigDecimal.ZERO : amountBalance;
    }
}
