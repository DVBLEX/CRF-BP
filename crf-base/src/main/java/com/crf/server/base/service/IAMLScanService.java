package com.crf.server.base.service;

import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.CustomerAmlResponse;

import java.io.IOException;

public interface IAMLScanService {
    CustomerAmlResponse sendPost(Customer customer) throws IOException, InterruptedException, Exception;
    void setCustomerIdAndSave(Customer customer, CustomerAmlResponse customerAmlResponse);
}
