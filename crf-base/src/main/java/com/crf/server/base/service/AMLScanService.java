package com.crf.server.base.service;

import static com.crf.server.base.common.ServerConstants.dateFormatddMMyyyy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crf.server.base.repository.AMLScanCustomerRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.CustomerAmlResponse;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class AMLScanService {
    private AMLScanCustomerRepository amlScanCustomerRepository;

    @Autowired
    public void setAMLScanCustomerRepository(AMLScanCustomerRepository amlScanCustomerRepository) {
        this.amlScanCustomerRepository = amlScanCustomerRepository;
    }

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    @Value("${tc.person.scan.api.url}")
    private String           ApiUrl;

    @Value("${tc.person.scan.api.key}")
    private String           ApiKey;

    /*
     * Person Sapphire Scan - Body parameter
     * {
     * "firstName": "string",
     * "middleName": "string",
     * "lastName": "string",
     * "originalName": "string"
     * "gender": "string",
     * "dob": "string",
     * "country": "string",
     * "matchRate": 75,
     * "maxResultCount": 100
     * }
     */

    public CustomerAmlResponse sendPost(Customer customer) throws IOException, InterruptedException {
        CustomerAmlResponse dataToDB = new CustomerAmlResponse();
        try {
            DateFormat df = new SimpleDateFormat(dateFormatddMMyyyy);

            Date dob = customer.getDateOfBirth();

            String dobAsString = df.format(dob);

            String body = new StringBuilder().append("{").append("\"firstName\":").append("\"").append(customer.getFirstName()).append("\"").append(",").append("\"middleName\":\"\",")
                    .append("\"lastName\":").append("\"").append(customer.getLastName()).append("\"").append(",").append("\"originalName\":\"\",").append("\"gender\":\"\",")
                    .append("\"dob\":").append("\"").append(dobAsString).append("\"").append(",").append("\"country\":").append("\"").append(customer.getNationality()).append("\"")
                    .append(",").append("\"matchRate\":\"75\",").append("\"maxResultCount\":\"100\"").append("}").toString();

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body)).uri(URI.create(ApiUrl)).setHeader("api-key", ApiKey)
                    .header("Content-Type", "application/json").build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());

            dataToDB.setAmlScanResponse(
                    object.toString().length() > ServerConstants.AML_API_RESPONSE_MAX_LENGTH ? object.toString().substring(0, ServerConstants.AML_API_RESPONSE_MAX_LENGTH)
                            : object.toString());
            dataToDB.setNumberOfMatches(object.getInt("numberOfMatches"));
            dataToDB.setDateCreated(new Date());
            log.info("AMLScanService#AMLCheck###Success: "+"CustomerCode= "+ customer.getCode());
        }

        catch (Exception e) {
            log.error("AMLScanService#AMLCheck###Exception: " + e.getMessage() + "CustomerCode= " + customer.getCode());
        }
        return dataToDB;
    }
    public CustomerAmlResponse sendPostStub(Customer customer) {

        CustomerAmlResponse dataToDBStub = new CustomerAmlResponse();
        try {
            DateFormat df = new SimpleDateFormat(dateFormatddMMyyyy);

            Date dob = customer.getDateOfBirth();

            String dobAsString = df.format(dob);
            int numberOfMatches = 0;
            String body = new StringBuilder().append("{").append("\"firstName\":").append("\"").append(customer.getFirstName()).append("\"").append(",")
                    .append("\"lastName\":").append("\"").append(customer.getLastName()).append("\"").append(",").append("\"dob\":").append("\"")
                    .append(dobAsString).append("\"").append(",").append("\"country\":").append("\"").append(customer.getNationality()).append("\"")
                    .append("}").toString();

            JSONObject objectStub = new JSONObject(body);
            String firstName = objectStub.getString("firstName");
            String lastName = objectStub.getString("lastName");
            String dobStub = objectStub.getString("dob");

            if (firstName.equals("Victor") || lastName.equals("Yanukovich") || dobStub.equals("09/07/1950") || dobStub.equals("01/01/2000")) {
                numberOfMatches = 1;
            }

            dataToDBStub.setAmlScanResponse(
                    objectStub.toString().length() > ServerConstants.AML_API_RESPONSE_MAX_LENGTH ? objectStub.toString().substring(0, ServerConstants.AML_API_RESPONSE_MAX_LENGTH)
                            : objectStub.toString());
            dataToDBStub.setNumberOfMatches(numberOfMatches);
            dataToDBStub.setDateCreated(new Date());
            log.info("AMLScanService#AMLCheck###Success: "+"CustomerCode= "+ customer.getCode());
        }
        catch (Exception e) {
            log.error("AMLScanService#AMLCheck###Exception: " + e.getMessage() + "CustomerCode= " + customer.getCode());
        }
        return dataToDBStub;
    }

    public void setCustomerIdAndSave(Customer customer, CustomerAmlResponse customerAmlResponse) {
        customerAmlResponse.setCustomerId(customer.getId());
        amlScanCustomerRepository.save(customerAmlResponse);
    }

}
