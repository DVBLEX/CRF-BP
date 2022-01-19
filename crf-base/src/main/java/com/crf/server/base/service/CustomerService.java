package com.crf.server.base.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.BankAccount;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.CustomerVerificationDenial;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.KycDetails;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.entity.VerificationDenialReason;
import com.crf.server.base.jsonentity.BankAccountJson;
import com.crf.server.base.jsonentity.CustomerDetailsJson;
import com.crf.server.base.jsonentity.CustomerJson;
import com.crf.server.base.jsonentity.CustomerVerificationDenialJson;
import com.crf.server.base.jsonentity.DenyCustomerJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.jsonentity.VerifyCustomerJson;
import com.crf.server.base.repository.AMLScanCustomerRepository;
import com.crf.server.base.repository.BankAccountRepository;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.CustomerVerificationDenialRepository;
import com.crf.server.base.repository.VerificationDenialReasonRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class CustomerService {

    private AMLScanCustomerRepository            amlScanCustomerRepository;
    private BankAccountRepository                bankAccountRepository;
    private CustomerRepository                   customerRepository;
    private CustomerVerificationDenialRepository customerVerificationDenialRepository;
    private VerificationDenialReasonRepository   verificationDenialReasonRepository;

    private EmailService                         emailService;
    private FileService                          fileService;
    private OperatorService                      operatorService;

    @Value("${tc.system.url}")
    private String                               systemUrl;

    @Autowired
    public void setBankAccountRepository(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setCustomerVerificationDenialRepository(CustomerVerificationDenialRepository customerVerificationDenialRepository) {
        this.customerVerificationDenialRepository = customerVerificationDenialRepository;
    }

    @Autowired
    public void setVerificationDenialReasonRepository(VerificationDenialReasonRepository verificationDenialReasonRepository) {
        this.verificationDenialReasonRepository = verificationDenialReasonRepository;
    }

    @Autowired
    public void setAmlScanCustomerRepository(AMLScanCustomerRepository amlScanCustomerRepository) {
        this.amlScanCustomerRepository = amlScanCustomerRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    public Customer getCustomerById(long customerId) throws Exception {

        return customerRepository.findById(customerId).orElse(null);
    }

    public Customer getCustomerByCode(String customerCode) throws Exception {

        return customerRepository.findByCode(customerCode);
    }

    public void saveKYCDetails(long customerId, KycDetails kycDetails) throws Exception {

        Customer customer = getCustomerById(customerId);

        if (customer == null) {
            log.error("saveIdDetails##Exception: customer is not found by id. id: " + customerId);
            throw new Exception("CustomerServiceImpl#saveIdDetails###Exception: customer is not found by id. id: " + customerId);
        }

        customer.setKycOption(kycDetails.getKycOption());
        customer.setId1Type(kycDetails.getId1Type());
        customer.setId1Number(kycDetails.getId1Number());
        customer.setId2Type(kycDetails.getId2Type() == null ? ServerConstants.DEFAULT_INT : kycDetails.getId2Type());
        customer.setId2Number(kycDetails.getId2Number() == null ? ServerConstants.DEFAULT_STRING : kycDetails.getId2Number());
        customer.setDateID1Expiry(kycDetails.getDateId1Expiry());
        customer.setDateID2Expiry(kycDetails.getDateId2Expiry());
        customer.setPoa1Type(kycDetails.getPoa1Type());
        customer.setPoa2Type(kycDetails.getPoa2Type() == null ? ServerConstants.DEFAULT_INT : kycDetails.getPoa2Type());

        customerRepository.save(customer);
    }

    public void accountReSubmit(long customerId) throws Exception {

        Customer customer = getCustomerById(customerId);

        customer.setIsPassportScanDenied(false);

        customerRepository.save(customer);
    }

    public PageList<CustomerJson> listRegisteredCustomers(Pageable pageable) throws Exception {

        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);

        List<CustomerJson> resultList = new ArrayList<>();

        for (Customer customer : customerPage) {

            CustomerJson customerJson = new CustomerJson();

            BeanUtils.copyProperties(customer, customerJson);

            try {
                customerJson.setDateOfBirthString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateOfBirth()));
            } catch (ParseException e) {
                customerJson.setDateOfBirthString("");
            }

            try {
                customerJson.setDateId1ExpiryString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateID1Expiry()));
            } catch (ParseException e) {
                customerJson.setDateId1ExpiryString("");
            }

            try {
                customerJson.setDateId2ExpiryString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateID2Expiry()));
            } catch (ParseException e) {
                customerJson.setDateId2ExpiryString("");
            }

            resultList.add(customerJson);
        }

        return new PageList<>(resultList, new PageInfo(customerPage.getTotalPages(), customerPage.getTotalElements()));
    }

    public VerifyCustomerJson getCustomerForVerification(String customerCode) throws Exception {

        Customer customer = customerRepository.findByCode(customerCode);

        CustomerJson customerJson = new CustomerJson();

        try {
            customerJson.setDateOfBirthString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateOfBirth()));
        } catch (ParseException e) {
            customerJson.setDateOfBirthString("");
        }

        try {
            customerJson.setDateId1ExpiryString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateID1Expiry()));
        } catch (ParseException e) {
            customerJson.setDateId1ExpiryString("");
        }

        try {
            customerJson.setDateId2ExpiryString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, customer.getDateID2Expiry()));
        } catch (ParseException e) {
            customerJson.setDateId2ExpiryString("");
        }
        customerJson.setIsAmlVerified(customer.getIsAmlVerified());

        BeanUtils.copyProperties(customer, customerJson);

        VerifyCustomerJson verifyCustomerJson = new VerifyCustomerJson();
        verifyCustomerJson.setCustomer(customerJson);
        verifyCustomerJson.setIdScans(fileService.getCustomerFileDataList(ServerConstants.FILE_ROLE_ID_DOCUMENT, customer.getId()));
        verifyCustomerJson.setPoaScans(fileService.getCustomerFileDataList(ServerConstants.FILE_ROLE_POA_DOCUMENT, customer.getId()));
        verifyCustomerJson.setPhoto(fileService.getCustomerFileData(ServerConstants.FILE_ROLE_PHOTOGRAPH, customer.getId()));
        verifyCustomerJson.setCustomerAMLResponse(String.valueOf(amlScanCustomerRepository.findByCustomerId(customer.getId())));

        List<CustomerVerificationDenialJson> customerVerificationDenialJsonList = new ArrayList<>();
        List<CustomerVerificationDenial> customerVerificationDenialList = customerVerificationDenialRepository.findAllByCustomerId(customer.getId());

        for (CustomerVerificationDenial customerVerificationDenial : customerVerificationDenialList) {

            CustomerVerificationDenialJson customerVerificationDenialJson = new CustomerVerificationDenialJson();
            customerVerificationDenialJson.setId(customerVerificationDenial.getId());
            customerVerificationDenialJson.setReasonId(customerVerificationDenial.getVerificationDenialReasonId());

            VerificationDenialReason verificationDenialReason = verificationDenialReasonRepository.findById(customerVerificationDenial.getVerificationDenialReasonId())
                .orElse(null);

            customerVerificationDenialJson.setDenialReason(verificationDenialReason.getDescription());
            customerVerificationDenialJson.setAdditionalDescription(customerVerificationDenial.getAdditionalDescription());

            customerVerificationDenialJsonList.add(customerVerificationDenialJson);
        }

        verifyCustomerJson.setCustomerVerificationDenialList(customerVerificationDenialJsonList);

        return verifyCustomerJson;
    }

    @Transactional
    public void verifyCustomer(long operatorId, String customerCode) throws Exception {

        Customer customer = customerRepository.findByCode(customerCode);
        customer.setIsPassportScanVerified(true);
        customer.setDatePassportScanVerified(new Date());
        customer.setIsPassportScanDenied(false);

        customerRepository.save(customer);

        deleteCustomerVerificationDenialByCustomerId(customer.getId());

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_VERIFICATION_SUCCESSFUL_TEMPLATE_ID, params);

        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"customerId\":" + customerRepository.findByCode(customerCode).getId());
        jsonStringBuilder.append("}");

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_VERIFY_CUSTOMER, jsonStringBuilder.toString());
    }

    @Transactional
    public void denyCustomerVerification(long operatorId, DenyCustomerJson denyCustomerJson) throws Exception {

        Customer customer = customerRepository.findByCode(denyCustomerJson.getCustomerCode());

        String emailDenialReasons = "";

        List<CustomerVerificationDenial> customerVerificationDenials = customerVerificationDenialRepository.findAllByCustomerId(customer.getId());

        if (!customerVerificationDenials.isEmpty()) {

            // remove elements which are no longer reason

            List<CustomerVerificationDenial> removeList = customerVerificationDenials.stream()
                .filter(d -> denyCustomerJson.getReasonList().stream().filter(r -> r.getReasonId() == d.getId()).count() == 0l).collect(Collectors.toList());

            for (CustomerVerificationDenial customerDenialEntity : removeList) {

                customerVerificationDenials.remove(customerDenialEntity);

                customerVerificationDenialRepository.deleteById(customerDenialEntity.getId());
            }
        }

        // filter out the reasons which already exist

        List<CustomerVerificationDenialJson> newReasonList = denyCustomerJson.getReasonList().stream()
            .filter(r -> customerVerificationDenials.stream().filter(d -> d.getId() == r.getId()).count() == 0l).collect(Collectors.toList());

        for (CustomerVerificationDenialJson customerVerificationDenialJson : newReasonList) {

            if (StringUtils.isBlank(customerVerificationDenialJson.getAdditionalDescription())) {
                emailDenialReasons += customerVerificationDenialJson.getDenialReason() + "<br>";

            } else {
                emailDenialReasons += customerVerificationDenialJson.getDenialReason() + ": " + customerVerificationDenialJson.getAdditionalDescription() + "<br>";
            }

            CustomerVerificationDenial customerVerificationDenialEntity = new CustomerVerificationDenial();
            customerVerificationDenialEntity.setCustomerId(customer.getId());

            VerificationDenialReason verificationDenialReasonEntity = verificationDenialReasonRepository.findById(customerVerificationDenialJson.getReasonId()).orElse(null);

            customerVerificationDenialEntity.setVerificationDenialReasonId(verificationDenialReasonEntity.getId());

            customerVerificationDenialEntity.setAdditionalDescription(customerVerificationDenialJson.getAdditionalDescription());
            customerVerificationDenialEntity.setDateCreated(new Date());

            customerVerificationDenialRepository.save(customerVerificationDenialEntity);
        }

        customer.setIsPassportScanDenied(true);
        customer.setDatePassportScanDenied(new Date());

        customerRepository.save(customer);

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("denialReasons", emailDenialReasons);
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_VERIFICATION_DENIED_TEMPLATE_ID, params);

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_DENY_CUSTOMER, ServerUtil.toJson(denyCustomerJson));
    }

    public void deleteCustomerVerificationDenialByCustomerId(long customerId) {

        customerVerificationDenialRepository.deleteByCustomerId(customerId);
    }

    @Transactional
    public void saveBankDetails(BankAccountJson bankAccountJson, long operatorId, long customerId) throws Exception {

        Customer customer = getCustomerById(customerId);

        if (customer == null)
            throw new Exception("CustomerService#saveBankDetails###Exception: customer is not found by id. id: " + customerId);

        BankAccount bankAccount = null;

        if (customer.getIsBankAccountSetup()) {
            bankAccount = bankAccountRepository.findByCustomerId(customerId);

            if (bankAccount == null)
                throw new Exception("CustomerService#saveBankDetails###Exception: customer bankAccount is not found by customerId. customerId: " + customerId);

        } else {
            bankAccount = new BankAccount();
            bankAccount.setCode(SecurityUtil.generateUniqueCode());
            bankAccount.setCustomerId(customerId);
            bankAccount.setDateCreated(new Date());
        }

        bankAccount.setBankName(bankAccountJson.getBankName().trim());
        bankAccount.setBankAccountName(bankAccountJson.getBankAccountName().trim());
        bankAccount.setBankAddress(bankAccountJson.getBankAddress().trim());
        bankAccount.setIban(bankAccountJson.getIban().trim());
        bankAccount.setBic(bankAccountJson.getBic().trim());
        bankAccount.setDateEdited(new Date());

        bankAccountRepository.save(bankAccount);

        if (!customer.getIsBankAccountSetup()) {

            customer.setIsBankAccountSetup(true);

            customerRepository.save(customer);
        }

        operatorService.logOperatorActivity(operatorId, OperatorService.INVESTOR_ACTIVITY_ID_SAVE_BANK_ACCOUNT, ServerUtil.toJson(bankAccountJson));
    }

    public CustomerDetailsJson getCustomerDetails(long customerId) {

        CustomerDetailsJson customerDetailsJson = new CustomerDetailsJson();

        BankAccount bankAccount = bankAccountRepository.findByCustomerId(customerId);

        BankAccountJson bankAccountJson = new BankAccountJson();

        if (bankAccount != null) {

            BeanUtils.copyProperties(bankAccount, bankAccountJson);
        }

        customerDetailsJson.setBankAccount(bankAccountJson);

        return customerDetailsJson;
    }
}
