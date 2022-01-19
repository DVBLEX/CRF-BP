package com.crf.server.base.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountDocument;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.DepositAccountDocumentJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.DepositAccountDocumentRepository;

@Service
public class DepositAccountDocumentService {

    private DepositAccountDocumentRepository depositAccountDocumentRepository;

    @Autowired
    public void setDepositAccountDocumentRepository(DepositAccountDocumentRepository depositAccountDocumentRepository) {
        this.depositAccountDocumentRepository = depositAccountDocumentRepository;
    }

    public DepositAccountDocument getDepositAccountDocumentByCode(String code) throws Exception {

        return depositAccountDocumentRepository.findByCode(code);
    }

    public PageList<DepositAccountDocumentJson> getDepositAccountDocumentsByCustomerId(Pageable pageable, long customerId) throws CRFException, Exception {

        List<DepositAccountDocumentJson> resultList = new ArrayList<>();

        Page<DepositAccountDocument> depositAccountDocumentPage = depositAccountDocumentRepository.findAllByCustomerId(customerId, pageable);

        for (DepositAccountDocument depositAccountDocument : depositAccountDocumentPage) {

            DepositAccountDocumentJson depositAccountDocumentJson = new DepositAccountDocumentJson();

            BeanUtils.copyProperties(depositAccountDocument, depositAccountDocumentJson);

            if (depositAccountDocument.getDateCreated() == null) {
                depositAccountDocumentJson.setDateCreatedString("");
            } else {
                depositAccountDocumentJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountDocument.getDateCreated()));
            }

            resultList.add(depositAccountDocumentJson);
        }

        return new PageList<>(resultList, new PageInfo(depositAccountDocumentPage.getTotalPages(), depositAccountDocumentPage.getTotalElements()));
    }

    @Transactional
    public void saveDepositAccountDocument(DepositAccount depositAccount, Integer type, Long depositAccountPaymentId, String filePath) throws CRFException, Exception {

        DepositAccountDocument depositAccountDocument = new DepositAccountDocument();
        depositAccountDocument.setCode(SecurityUtil.generateUniqueCode());
        depositAccountDocument.setType(type);
        depositAccountDocument.setCustomerId(depositAccount.getCustomerId());
        depositAccountDocument.setDepositAccountId(depositAccount.getId());
        depositAccountDocument.setDepositProductId(depositAccount.getDepositProductId());
        depositAccountDocument.setDepositAccountPaymentId(depositAccountPaymentId);
        depositAccountDocument.setAccountNumber(depositAccount.getAccountNumber());
        depositAccountDocument.setPath(filePath);
        depositAccountDocument.setDateCreated(new Date());

        depositAccountDocumentRepository.save(depositAccountDocument);
    }
}
