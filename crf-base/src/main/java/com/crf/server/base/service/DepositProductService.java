package com.crf.server.base.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.DepositProductJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.DepositProductRepository;

@Service
// @CommonsLog
public class DepositProductService {

    private DepositProductRepository depositProductRepository;

    @Autowired
    public void setDepositProductRepository(DepositProductRepository depositProductRepository) {
        this.depositProductRepository = depositProductRepository;
    }

    public DepositProduct getDepositProductById(long depositProductId) {

        return depositProductRepository.findById(depositProductId).orElse(null);
    }

    public DepositProduct getDepositProductByCode(String depositProductCode) {

        return depositProductRepository.findByCode(depositProductCode);
    }

    public List<DepositProductJson> getDepositProducts() {

        List<DepositProductJson> resultList = new ArrayList<>();

        List<DepositProduct> depositProductList = (List<DepositProduct>) depositProductRepository.findAll();

        for (DepositProduct depositProduct : depositProductList) {

            resultList.add(mapEntityToJson(depositProduct));
        }

        return resultList;
    }

    public BigDecimal calculateDepositTotalInterest(BigDecimal depositAmount, BigDecimal interestRate, BigDecimal termYears) {

        BigDecimal rate = interestRate.divide(new BigDecimal(100));

        BigDecimal interest = depositAmount.multiply(rate).multiply(termYears);
        interest = interest.setScale(2, RoundingMode.HALF_UP);

        return interest;
    }

    public PageList<DepositProductJson> getAllDepositProductList(Pageable pageable) throws Exception {

        List<DepositProductJson> resultList = new ArrayList<>();

        Page<DepositProduct> depositProductPage = depositProductRepository.findAll(pageable);

        for (DepositProduct depositProduct : depositProductPage) {
            resultList.add(mapEntityToJson(depositProduct));
        }

        return new PageList<>(resultList, new PageInfo(depositProductPage.getTotalPages(), depositProductPage.getTotalElements()));
    }

    private DepositProductJson mapEntityToJson(DepositProduct depositProduct){
        DepositProductJson depositProductJson = new DepositProductJson();

        BeanUtils.copyProperties(depositProduct, depositProductJson);

        depositProductJson.setYearlyInterestRate(depositProduct.getYearlyInterestRate().toString());
        depositProductJson.setTwiceYearlyInterestRate(depositProduct.getTwiceYearlyInterestRate().toString());
        depositProductJson.setQuarterlyInterestRate(depositProduct.getQuarterlyInterestRate().toString());
        depositProductJson.setTermYears(depositProduct.getTermYears().toString());
        depositProductJson.setDepositMinAmount(depositProduct.getDepositMinAmount().toString());
        depositProductJson.setDepositMaxAmount(depositProduct.getDepositMaxAmount().toString());
        depositProductJson.setPrematureWithdrawalInterestRate(depositProduct.getPrematureWithdrawalInterestRate().toString());
        depositProductJson.setWithdrawalFee(depositProduct.getWithdrawalFee().toString());

        return depositProductJson;
    }

    public void editDepositProduct(DepositProductJson depositProductJson) throws CRFValidationException, CRFException, IOException {

        DepositProduct depositProduct = depositProductRepository.findByCode(depositProductJson.getCode());

        depositProduct.setName(depositProductJson.getName());
        depositProduct.setDescription(depositProductJson.getDescription());
        depositProduct.setDepositMinAmount(new BigDecimal(depositProductJson.getDepositMinAmount()));
        depositProduct.setDepositMaxAmount(new BigDecimal(depositProductJson.getDepositMaxAmount()));
        depositProduct.setQuarterlyInterestRate(new BigDecimal(depositProductJson.getQuarterlyInterestRate()));
        depositProduct.setYearlyInterestRate(new BigDecimal(depositProductJson.getYearlyInterestRate()));
        depositProduct.setTwiceYearlyInterestRate(new BigDecimal(depositProductJson.getTwiceYearlyInterestRate()));
        depositProduct.setTermYears(new BigDecimal(depositProductJson.getTermYears()));
        depositProduct.setPrematureWithdrawalMinDays(depositProductJson.getPrematureWithdrawalMinDays());
        depositProduct.setPrematureWithdrawalInterestRate(new BigDecimal(depositProductJson.getPrematureWithdrawalInterestRate()));
        depositProduct.setWithdrawalFee(new BigDecimal(depositProductJson.getWithdrawalFee()));

        depositProductRepository.save(depositProduct);
    }
}
