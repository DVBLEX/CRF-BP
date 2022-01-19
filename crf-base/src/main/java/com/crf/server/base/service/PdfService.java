package com.crf.server.base.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountPayment;
import com.crf.server.base.entity.DepositProduct;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class PdfService {

    @Value("${tc.system.name}")
    private String applicationName;

    @Value("${tc.pdf.basedir}")
    private String pdfBaseDir;

    @Value("${tc.pdf.depositdir}")
    private String pdfDepositDir;

    @Value("${tc.system.url}")
    private String systemUrl;

    @Value("${tc.system.url.logo}")
    private String systemUrlLogo;

    public File generatePdfReport(int type, DepositProduct depositProduct, DepositAccount depositAccount, DepositAccountPayment depositAccountPayment) {

        Document document = new Document();

        File file = new File(generatePdfFileName(depositAccount.getCustomerId(), depositAccount.getAccountNumber()));
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();

            PdfWriter.getInstance(document, new FileOutputStream(file));

            document.open();

            addHeader(document);
            addDocTitle(document, depositAccount.getAccountNumber());

            if (type == ServerConstants.PDF_DOC_TYPE_DEPOSIT_INITIATED) {

                addDepositAccountContents(document, depositProduct, depositAccount);

            } else if (type == ServerConstants.PDF_DOC_TYPE_DEPOSIT_INTEREST_PAYMENT) {

                addInterestPaymentContents(document, depositProduct, depositAccount, depositAccountPayment);
            }

            document.close();

        } catch (Exception e) {

            log.error("generatePdfReport###Exception: ", e);
        }

        return file;
    }

    private String generatePdfFileName(Long customerId, String depositAccountNumber) {

        String localDateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ServerConstants.dateFormatyyyyMMddHHmmss));

        return pdfBaseDir + pdfDepositDir + customerId + "/" + depositAccountNumber + "_" + localDateTimeString + ".pdf";
    }

    private void addDepositAccountTable(Document document, Font font, DepositProduct depositProduct, DepositAccount depositAccount) throws DocumentException, ParseException {

        PdfPTable depositAccountTable = new PdfPTable(2);
        depositAccountTable.setWidths(new int[] { 5, 5 });
        depositAccountTable.setWidthPercentage(100);

        PdfPCell depositAccountItem = new PdfPCell();
        depositAccountItem.setBorder(Rectangle.BOTTOM);
        depositAccountItem.setBorderColor(BaseColor.LIGHT_GRAY);
        depositAccountItem.addElement(new Phrase("Product Name", font));
        depositAccountItem.addElement(new Phrase("Interest Rate", font));
        depositAccountItem.addElement(new Phrase("Term (years)", font));
        depositAccountItem.addElement(new Phrase("Deposit Amount", font));
        depositAccountItem.addElement(new Phrase("Interest Payout", font));

        if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED) {
            depositAccountItem.addElement(new Phrase("Start Date*", font));
            depositAccountItem.addElement(new Phrase("Maturity Date**", font));

        } else {
            depositAccountItem.addElement(new Phrase("Start Date", font));
            depositAccountItem.addElement(new Phrase("Maturity Date", font));
        }

        depositAccountItem.addElement(new Phrase("Status", font));
        depositAccountItem.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell depositAccountValue = new PdfPCell();
        depositAccountValue.setBorder(Rectangle.BOTTOM);
        depositAccountValue.setBorderColor(BaseColor.LIGHT_GRAY);
        depositAccountValue.addElement(new Phrase(depositProduct.getName(), font));
        depositAccountValue.addElement(new Phrase(depositAccount.getInterestRate().toString() + "%", font));
        depositAccountValue.addElement(new Phrase(depositAccount.getTermYears().toString(), font));
        depositAccountValue.addElement(new Phrase("€" + depositAccount.getDepositAmount().toString(), font));

        if (depositAccount.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY) {
            depositAccountValue.addElement(new Phrase("Quarterly", font));

        } else if (depositAccount.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY) {
            depositAccountValue.addElement(new Phrase("Half-Yearly", font));

        } else if (depositAccount.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY) {
            depositAccountValue.addElement(new Phrase("Yearly", font));
        }

        LocalDate localDateOpen = depositAccount.getDateOpen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date potentialDateMaturity = Date.from(localDateOpen.plusYears(depositAccount.getTermYears().intValue()).atStartOfDay(ZoneId.systemDefault()).toInstant());

        depositAccountValue.addElement(new Phrase(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateOpen()), font));
        depositAccountValue.addElement(new Phrase(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, potentialDateMaturity), font));

        if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED) {
            depositAccountValue.addElement(new Phrase("Initiated", font));

        } else if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE) {
            depositAccountValue.addElement(new Phrase("Active", font));

        } else if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED) {
            depositAccountValue.addElement(new Phrase("Withdrawal Requested", font));

        } else if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWN) {
            depositAccountValue.addElement(new Phrase("Withdrawn", font));

        } else if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED) {
            depositAccountValue.addElement(new Phrase("Matured", font));
        }

        depositAccountValue.setVerticalAlignment(Element.ALIGN_MIDDLE);

        depositAccountTable.addCell(depositAccountItem);
        depositAccountTable.addCell(depositAccountValue);

        document.add(depositAccountTable);
    }

    private void addDepositAccountContents(Document document, DepositProduct depositProduct, DepositAccount depositAccount) throws DocumentException, ParseException {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.NORMAL);
        Font smallerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        addDepositAccountTable(document, font, depositProduct, depositAccount);

        document.add(Chunk.NEWLINE);

        document.add(new Paragraph(new Chunk("* Indicates the start of the term. Please note, the actual term will start from the date the deposit is received.", smallerFont)));
        document.add(new Paragraph(new Chunk("** Indicates the end of the term. It is based on the start date plus the term (years).", smallerFont)));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph(new Chunk(
            "The status of your deposit product will remain as INITIATED until your deposit is confirmed that it has been received. Once that is confirmed, the deposit will become ACTIVE and you will receive a confirmation email. "
                + "The interest earnings will begin from the date of confirmation and the maturity date will be set accordingly.",
            font)));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph(new Chunk("Please transfer your deposit to the following bank account and include your unique reference "
            + depositAccount.getBankTransferReference() + " as part of the transfer.", font)));
        document.add(Chunk.NEWLINE);

        PdfPTable bankAccountTable = new PdfPTable(2);
        bankAccountTable.setWidths(new int[] { 5, 5 });
        bankAccountTable.setWidthPercentage(100);

        PdfPCell bankAccountItem = new PdfPCell();
        bankAccountItem.setBorder(Rectangle.NO_BORDER);
        bankAccountItem.addElement(new Phrase("Account", font));
        bankAccountItem.addElement(new Phrase("IBAN", font));
        bankAccountItem.addElement(new Phrase("BIC", font));
        bankAccountItem.addElement(new Phrase("Bank Name", font));
        bankAccountItem.addElement(new Phrase("Bank Address", font));
        bankAccountItem.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell bankAccountValue = new PdfPCell();
        bankAccountValue.setBorder(Rectangle.NO_BORDER);
        bankAccountValue.addElement(new Phrase(ServerConstants.BANK_ACCOUNT_NAME, font));
        bankAccountValue.addElement(new Phrase(ServerConstants.BANK_IBAN, font));
        bankAccountValue.addElement(new Phrase(ServerConstants.BANK_BIC, font));
        bankAccountValue.addElement(new Phrase(ServerConstants.BANK_NAME, font));
        bankAccountValue.addElement(new Phrase(ServerConstants.BANK_ADDRESS, font));

        bankAccountTable.addCell(bankAccountItem);
        bankAccountTable.addCell(bankAccountValue);

        document.add(bankAccountTable);
    }

    private void addInterestPaymentContents(Document document, DepositProduct depositProduct, DepositAccount depositAccount, DepositAccountPayment depositAccountPayment)
        throws DocumentException, ParseException {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.NORMAL);

        addDepositAccountTable(document, font, depositProduct, depositAccount);

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph(new Chunk("Interest has been earned on your active deposit and the accrued interest has been transferred to your bank account.", font)));

        document.add(Chunk.NEWLINE);

        PdfPTable interestEarningsTable = new PdfPTable(2);
        interestEarningsTable.setWidths(new int[] { 5, 5 });
        interestEarningsTable.setWidthPercentage(100);

        PdfPCell item = new PdfPCell();
        item.setBorder(Rectangle.NO_BORDER);
        item.addElement(new Phrase("Interest Period", font));
        item.addElement(new Phrase("Interest Amount Paid", font));
        item.addElement(new Phrase("Interest Payment Date", font));
        item.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell value = new PdfPCell();
        value.setBorder(Rectangle.NO_BORDER);
        value.addElement(new Phrase(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDatePeriodFrom()) + " - "
            + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDatePeriodTo()), font));
        value.addElement(new Phrase("€" + depositAccountPayment.getAmount().toString(), font));
        value.addElement(new Phrase(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDateProcessed()), font));

        interestEarningsTable.addCell(item);
        interestEarningsTable.addCell(value);

        document.add(interestEarningsTable);
    }

    private void addHeader(Document document) {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
        font.setColor(new BaseColor(20, 48, 72));

        try {
            PdfPTable header = new PdfPTable(2);
            header.setWidths(new int[] { 2, 16 });
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            PdfPCell cellImage = new PdfPCell();
            try {
                cellImage.setImage(Image.getInstance(systemUrl + systemUrlLogo));

            } catch (ConnectException ce) {
                // ConnectException is thrown here every time the "save deposit" integration test is performed during local builds
                // supress the exception so it doesnt show up in the console
            }
            cellImage.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellImage.setBorder(Rectangle.BOTTOM);
            cellImage.setBorderColor(BaseColor.LIGHT_GRAY);
            cellImage.setPaddingBottom(5);
            header.addCell(cellImage);

            PdfPCell text = new PdfPCell();
            text.setBorder(Rectangle.BOTTOM);
            text.setBorderColor(BaseColor.LIGHT_GRAY);
            text.addElement(new Phrase(applicationName, font));
            text.setVerticalAlignment(Element.ALIGN_MIDDLE);
            text.setPaddingBottom(13);
            header.addCell(text);

            document.add(header);

        } catch (Exception e) {

            log.error("addHeader###Exception: ", e);
        }
    }

    private void addDocTitle(Document document, String depositAccountNumber) throws DocumentException {

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);

        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(getCell("Deposit #" + depositAccountNumber, font, Element.ALIGN_LEFT));
        table.addCell(getCell(localDateString, font, Element.ALIGN_RIGHT));

        document.add(table);
    }

    private PdfPCell getCell(String text, Font font, int alignment) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }
}
