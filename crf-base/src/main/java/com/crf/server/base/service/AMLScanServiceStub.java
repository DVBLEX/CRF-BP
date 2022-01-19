package com.crf.server.base.service;

import static com.crf.server.base.common.ServerConstants.dateFormatddMMyyyy;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.CustomerAmlResponse;
import com.crf.server.base.repository.AMLScanCustomerRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



@Service
@Profile({"local","dev"})
public class AMLScanServiceStub implements IAMLScanService {
    private AMLScanCustomerRepository amlScanCustomerRepository;

    @Autowired
    public void setAMLScanCustomerRepository(AMLScanCustomerRepository amlScanCustomerRepository) {
        this.amlScanCustomerRepository = amlScanCustomerRepository;
    }

    @Override
    public CustomerAmlResponse sendPost(Customer customer) throws Exception{


        DateFormat df = new SimpleDateFormat(dateFormatddMMyyyy);

        Date dob = customer.getDateOfBirth();

        String dobAsString = df.format(dob);
        String body = new StringBuilder().append("{").append("\"firstName\":").append("\"").append(customer.getFirstName()).append("\"").append(",").append("\"middleName\":\"\",")
                .append("\"lastName\":").append("\"").append(customer.getLastName()).append("\"").append(",").append("\"originalName\":\"\",").append("\"gender\":\"\",")
                .append("\"dob\":").append("\"").append(dobAsString).append("\"").append(",").append("\"country\":").append("\"").append(customer.getNationality()).append("\"")
                .append(",").append("\"matchRate\":\"75\",").append("\"maxResultCount\":\"100\"").append("}").toString();

        JSONObject objectStub = new JSONObject(body);
        String firstName = objectStub.getString("firstName");
        String lastName  = objectStub.getString("lastName");
        String dobStub = objectStub.getString("dob");
        int numberOfMatches;
        if (firstName.equals("Victor") || lastName.equals("Yanukovich") || dobStub.equals("09/07/1950")){
            numberOfMatches = 1;
        }
        else numberOfMatches = 0;

        CustomerAmlResponse dataToDBStub = new CustomerAmlResponse();

        dataToDBStub.setAmlScanResponse(
                objectStub.toString().length() > ServerConstants.AML_API_RESPONSE_MAX_LENGTH ? objectStub.toString().substring(0, ServerConstants.AML_API_RESPONSE_MAX_LENGTH)
                        : objectStub.toString());
        dataToDBStub.setNumberOfMatches(objectStub.getInt(String.valueOf(numberOfMatches)));
        dataToDBStub.setDateCreated(new Date());

        return dataToDBStub;
    }


    @Override
    public void setCustomerIdAndSave(Customer customer, CustomerAmlResponse customerAmlResponse) {
        customerAmlResponse.setCustomerId(customer.getId());
        amlScanCustomerRepository.save(customerAmlResponse);
    }
}
