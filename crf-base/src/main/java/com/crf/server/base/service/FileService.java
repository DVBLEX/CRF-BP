package com.crf.server.base.service;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.ImageUtil;
import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.FileData;
import com.crf.server.base.entity.FileEntity;
import com.crf.server.base.entity.FileEntity.FileType;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.FileRepository;
import com.crf.server.base.repository.OperatorRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class FileService {

    private KeyStore           keyStore1;
    private KeyStore           keyStore2;

    private CustomerRepository customerRepository;
    private FileRepository     fileRepository;
    private OperatorRepository operatorRepository;
    private CustomerService    customerService;

    @Value("${tc.fileupload.keystore.file1}")
    private String             keyStoreFile1;

    @Value("${tc.fileupload.keystore.password1}")
    private String             keyStorePass1;

    @Value("${tc.fileupload.keystore.file2}")
    private String             keyStoreFile2;

    @Value("${tc.fileupload.keystore.password2}")
    private String             keyStorePass2;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostConstruct
    public void init() {

        try {
            InputStream keyStoreFile1Stream = this.getClass().getClassLoader().getResourceAsStream(keyStoreFile1);
            InputStream keyStoreFile2Stream = this.getClass().getClassLoader().getResourceAsStream(keyStoreFile2);

            keyStore1 = KeyStore.getInstance("JCEKS");
            keyStore1.load(keyStoreFile1Stream, keyStorePass1.toCharArray());

            keyStore2 = KeyStore.getInstance("JCEKS");
            keyStore2.load(keyStoreFile2Stream, keyStorePass2.toCharArray());

            log.info("FileServiceImpl#init###KeyStores are loaded.");

        } catch (Exception e) {

            keyStore1 = null;
            keyStore2 = null;
            log.error("FileServiceImpl#init###Exception: " + e.getMessage());
            log.error("FileServiceImpl#init###Error: Keystores are not loaded.");
        }
    }

    private synchronized Key getSecretKey(long fileEncryptionId, int keyStoreId) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {

        // alias
        StringBuilder aliasSB = new StringBuilder();
        aliasSB.append(ServerConstants.SYSTEM_FILE_STORE_ALIAS_SALT).append(".").append(fileEncryptionId * 3).append(".").append(fileEncryptionId * 2).append(".")
            .append(fileEncryptionId);

        // password protection for the entry
        StringBuilder passwdSB = new StringBuilder();
        passwdSB.append(ServerConstants.SYSTEM_FILE_STORE_PASSWORD_PROTECTION_SALT).append(".").append(fileEncryptionId).append(".").append(fileEncryptionId * 2).append(".")
            .append(fileEncryptionId * 3);

        Key secretKey = null;

        if (keyStoreId == ServerConstants.KEYSTORE_ID_PERSONAL_DATA) {

            secretKey = keyStore1.getKey(DigestUtils.sha256Hex(aliasSB.toString()), DigestUtils.sha256Hex(passwdSB.toString()).toCharArray());

        } else if (keyStoreId == ServerConstants.KEYSTORE_ID_BUSINESS_DATA) {

            secretKey = keyStore2.getKey(DigestUtils.sha256Hex(aliasSB.toString()), DigestUtils.sha256Hex(passwdSB.toString()).toCharArray());
        }

        return secretKey;
    }

    private void saveFileEntity(byte[] data, FileType type, String mimeType, FileEntity file, int keyStoreId) throws Exception {

        // if the data is image then compress it.
        if ((type == FileType.JPEG || type == FileType.JPG) && data.length > ServerConstants.IMAGE_COMPRESSION_LOWER_BOUND) {
            data = ImageUtil.compressImageAndSmartResize(data);
        }

        // generate unique code
        StringBuilder fileCodeSB = new StringBuilder();
        fileCodeSB.append(RandomStringUtils.randomAlphanumeric(4)).append(".").append(RandomStringUtils.randomAlphanumeric(4)).append(".")
            .append(RandomStringUtils.randomAlphanumeric(4)).append(".").append(file.getRole());
        fileCodeSB.append(".").append(RandomStringUtils.randomAlphanumeric(4));

        file.setCode(DigestUtils.sha256Hex(fileCodeSB.toString()));
        file.setType(type);
        file.setMimeType(mimeType);
        file.setDateEdited(new Date());

        if (file.getId() <= 0l) {

            /*
             * As the encryption key is accessed by using the id of the file, when it is a new file
             * we need to persist it and read the id of it in order to save it in encrypted form.
             */
            file.setData(new byte[0]);
            fileRepository.save(file);
        }

        int numberOfKeys = 1;
        if (keyStoreId == ServerConstants.KEYSTORE_ID_PERSONAL_DATA) {
            numberOfKeys = ServerConstants.KEYSTORE_NUMBER_OF_KEYS_PERSONAL_DATA;
        } else if (keyStoreId == ServerConstants.KEYSTORE_ID_BUSINESS_DATA) {
            numberOfKeys = ServerConstants.KEYSTORE_NUMBER_OF_KEYS_BUSINESS_DATA;
        }

        long fileEncryptionId = file.getId() % numberOfKeys;
        if (fileEncryptionId == 0l) {
            fileEncryptionId = numberOfKeys;
        }

        Key secretKey = getSecretKey(fileEncryptionId, keyStoreId);

        file.setData(SecurityUtil.encrypt(secretKey, data));
        fileRepository.save(file);
    }

    private FileData getFileData(FileEntity file, int keyStoreId) throws Exception {

        int numberOfKeys = 1;
        if (keyStoreId == ServerConstants.KEYSTORE_ID_PERSONAL_DATA) {
            numberOfKeys = ServerConstants.KEYSTORE_NUMBER_OF_KEYS_PERSONAL_DATA;
        } else if (keyStoreId == ServerConstants.KEYSTORE_ID_BUSINESS_DATA) {
            numberOfKeys = ServerConstants.KEYSTORE_NUMBER_OF_KEYS_BUSINESS_DATA;
        }

        long fileEncryptionId = file.getId() % numberOfKeys;
        if (fileEncryptionId == 0l) {
            fileEncryptionId = numberOfKeys;
        }

        Key secretKey = getSecretKey(fileEncryptionId, keyStoreId);

        FileData fileData = new FileData();
        fileData.setData(SecurityUtil.decrypt(secretKey, file.getData()));
        fileData.setType(file.getType().toString());
        fileData.setMimeType(file.getMimeType());
        fileData.setEntityType(file.getEntityType());
        fileData.setEntityNumber(file.getEntityNumber());
        return fileData;
    }

    @Transactional
    public void saveCustomerFile(byte[] data, FileType type, String mimeType, int fileRole, long customerId) throws Exception {

        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            log.error("FileServiceImpl#saveCustomerFile###Exception: Customer not found by customerId: " + customerId);
            throw new Exception("Customer not found. customerId: " + customerId);
        }

        FileEntity file = fileRepository.findByCustomerIdAndRole(customerId, fileRole);
        if (file == null) {

            file = buildFileEntity(fileRole, ServerConstants.DEFAULT_INT, ServerConstants.DEFAULT_INT, customerId);

            saveCustomerDocumentsFlags(fileRole, customer);
        }

        saveFileEntity(data, type, mimeType, file, ServerConstants.KEYSTORE_ID_PERSONAL_DATA);
    }

    @Transactional
    public void saveMultipleCustomerFiles(List<FileData> files, int fileRole, int entityNumber, long customerId) throws Exception {

        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            log.error("FileServiceImpl#saveCustomerFile###Exception: Customer not found by customerId: " + customerId);
            throw new Exception("Customer not found. customerId: " + customerId);
        }
        if (!files.isEmpty()) {
            fileRepository.deleteAllByCustomerIdAndRoleAndEntityNumber(customerId, fileRole, entityNumber);

            for (FileData fileData : files) {

                FileEntity file = buildFileEntity(fileRole, fileData.getEntityType(), fileData.getEntityNumber(), customerId);
                FileType fileType = FileType.valueOf(fileData.getType());
                saveFileEntity(fileData.getData(), fileType, fileData.getMimeType(), file, ServerConstants.KEYSTORE_ID_PERSONAL_DATA);
            }
            saveCustomerDocumentsFlags(fileRole, customer);
        }
    }

    private void saveCustomerDocumentsFlags(int fileRole, Customer customer) throws Exception {
        if (fileRole == ServerConstants.FILE_ROLE_ID_DOCUMENT || fileRole == ServerConstants.FILE_ROLE_POA_DOCUMENT) {

            customer.setIsPassportScanUploaded(true);
            customer.setDatePassportScanUploaded(new Date());

        } else if (fileRole == ServerConstants.FILE_ROLE_PHOTOGRAPH) {

            customer.setIsPhotoUploaded(true);
            customer.setDatePhotoUploaded(new Date());

        } else {
            log.error("FileServiceImpl#saveCustomerFile###Exception: Unknown fileRole: " + fileRole);
            throw new Exception("Unknown fileRole: " + fileRole);
        }

        customerRepository.save(customer);
    }

    private FileEntity buildFileEntity(int fileRole, int entityType, int entityNumber, long customerId) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setCustomerId(customerId);
        fileEntity.setRole(fileRole);
        fileEntity.setEntityType(entityType);
        fileEntity.setEntityNumber(entityNumber);

        Operator operator = operatorRepository.findByCustomerId(customerId);

        fileEntity.setOperatorId(operator == null ? ServerConstants.DEFAULT_LONG : operator.getId());
        fileEntity.setDateCreated(new Date());
        return fileEntity;
    }

    public FileData getCustomerFileData(int fileRole, long customerId) throws Exception {

        FileEntity file = fileRepository.findByCustomerIdAndRole(customerId, fileRole);

        if (file == null) {

            StringBuilder sb = new StringBuilder();
            sb.append("FileServiceImpl#getCustomerFileData###Exception: File not found by customerId and fileRole. customerId: ");
            sb.append(customerId).append(", fileRole: " + fileRole);

            log.error(sb.toString());
            throw new Exception(sb.toString());
        }

        return getFileData(file, ServerConstants.KEYSTORE_ID_PERSONAL_DATA);
    }

    public List<FileData> getCustomerFileDataList(int fileRole, long customerId) throws Exception {

        List<FileEntity> files = fileRepository.findListByCustomerIdAndRole(customerId, fileRole);

        if (files.size() == 0) {

            StringBuilder sb = new StringBuilder();
            sb.append("FileServiceImpl#getCustomerFileData###Exception: Files not found by customerId and fileRole. customerId: ");
            sb.append(customerId).append(", fileRole: " + fileRole);

            log.error(sb.toString());
            throw new Exception(sb.toString());
        }
        List<FileData> fileDataList = new ArrayList<>();
        for (FileEntity fileEntity : files) {
            FileData fileData = getFileData(fileEntity, ServerConstants.KEYSTORE_ID_PERSONAL_DATA);
            fileDataList.add(fileData);
        }

        return fileDataList;
    }

    public FileType getCustomerFileType(int fileRole, long customerId) throws Exception {

        return fileRepository.findByCustomerIdAndRole(customerId, fileRole).getType();
    }

}
