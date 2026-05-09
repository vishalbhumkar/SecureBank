package com.securebank.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.repository.TransactionRepository;

@Service
public class PdfStatementService {

    private final TransactionRepository
            transactionRepository;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter
                    .ofPattern("dd MMM yyyy, HH:mm");

    private static final BaseColor PRIMARY =
            new BaseColor(26, 60, 94);
    private static final BaseColor PRIMARY_LIGHT =
            new BaseColor(37, 99, 168);
    private static final BaseColor SUCCESS =
            new BaseColor(22, 163, 74);
    private static final BaseColor DANGER =
            new BaseColor(220, 38, 38);
    private static final BaseColor LIGHT_GRAY =
            new BaseColor(241, 245, 249);
    private static final BaseColor BORDER_COLOR =
            new BaseColor(226, 232, 240);

    public PdfStatementService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public byte[] generateStatement(
            User user,
            Account account) throws DocumentException {

        List<Transaction> transactions =
                transactionRepository
                        .findAllByAccount(account);

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        Document document = new Document(
                PageSize.A4, 40, 40, 60, 60);
        PdfWriter writer = PdfWriter.getInstance(
                document, out);

        // Add header/footer
        writer.setPageEvent(
                new HeaderFooterEvent(user.getFullName()));

        document.open();

        // ===== BANK HEADER =====
        addBankHeader(document);

        // ===== ACCOUNT INFO =====
        addAccountInfo(document, user, account);

        // ===== TRANSACTIONS TABLE =====
        addTransactionsTable(
                document, transactions, account);

        // ===== SUMMARY =====
        addSummary(document, transactions,
                account, user);

        document.close();
        return out.toByteArray();
    }

    // ===== BANK HEADER =====

    private void addBankHeader(Document doc)
            throws DocumentException {

        // Bank name box
        PdfPTable headerTable =
                new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1f, 2f});
        headerTable.setSpacingAfter(20f);

        // Logo cell
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBackgroundColor(PRIMARY);
        logoCell.setPadding(16);
        logoCell.setBorder(Rectangle.NO_BORDER);

        Font logoFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 20,
                BaseColor.WHITE);
        Font logoAccent = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 20,
                new BaseColor(240, 165, 0));

        Paragraph logoPara = new Paragraph();
        logoPara.add(new Chunk("Secure", logoFont));
        logoPara.add(new Chunk("Bank", logoAccent));
        logoCell.addElement(logoPara);

        Font taglineFont = FontFactory.getFont(
                FontFactory.HELVETICA, 8,
                new BaseColor(200, 220, 240));
        logoCell.addElement(
                new Paragraph("YOUR TRUSTED BANKING PARTNER",
                        taglineFont));
        headerTable.addCell(logoCell);

        // Statement title cell
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(PRIMARY_LIGHT);
        titleCell.setPadding(16);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setVerticalAlignment(
                Element.ALIGN_MIDDLE);

        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 14,
                BaseColor.WHITE);
        titleCell.addElement(
                new Paragraph("ACCOUNT STATEMENT",
                        titleFont));

        Font subFont = FontFactory.getFont(
                FontFactory.HELVETICA, 9,
                new BaseColor(200, 220, 255));
        titleCell.addElement(
                new Paragraph("Generated: "
                        + java.time.LocalDateTime.now()
                               .format(FMT),
                        subFont));
        headerTable.addCell(titleCell);

        doc.add(headerTable);
    }

    // ===== ACCOUNT INFO =====

    private void addAccountInfo(Document doc,
                                User user,
                                Account account)
            throws DocumentException {

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 1f});
        infoTable.setSpacingAfter(16f);

        Font labelFont = FontFactory.getFont(
                FontFactory.HELVETICA, 8,
                new BaseColor(100, 116, 139));
        Font valueFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 9,
                new BaseColor(30, 41, 59));

        addInfoRow(infoTable,
                "Account Holder", user.getFullName(),
                labelFont, valueFont);
        addInfoRow(infoTable,
                "Email", user.getEmail(),
                labelFont, valueFont);
        addInfoRow(infoTable,
                "Account Number",
                account.getAccountNumber(),
                labelFont, valueFont);
        addInfoRow(infoTable,
                "Account Type",
                account.getAccountType().name(),
                labelFont, valueFont);
        addInfoRow(infoTable,
                "Current Balance",
                "₹" + account.getBalance()
                        .toPlainString(),
                labelFont, valueFont);
        addInfoRow(infoTable,
                "Phone",
                user.getPhone() != null
                        ? user.getPhone() : "—",
                labelFont, valueFont);

        doc.add(infoTable);

        // Divider
        LineSeparator line = new LineSeparator();
        line.setLineColor(BORDER_COLOR);
        doc.add(new Chunk(line));
        doc.add(Chunk.NEWLINE);
    }

    private void addInfoRow(PdfPTable table,
                             String label,
                             String value,
                             Font labelFont,
                             Font valueFont) {
        PdfPCell labelCell = new PdfPCell(
                new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBackgroundColor(BaseColor.WHITE);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    // ===== TRANSACTIONS TABLE =====

    private void addTransactionsTable(
            Document doc,
            List<Transaction> transactions,
            Account account)
            throws DocumentException {

        // Section title
        Font sectionFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11,
                PRIMARY);
        Paragraph sectionTitle =
                new Paragraph("Transaction History",
                        sectionFont);
        sectionTitle.setSpacingAfter(8f);
        doc.add(sectionTitle);

        if (transactions.isEmpty()) {
            Font noTxnFont = FontFactory.getFont(
                    FontFactory.HELVETICA_OBLIQUE, 10,
                    new BaseColor(100, 116, 139));
            doc.add(new Paragraph(
                    "No transactions found for this account.",
                    noTxnFont));
            doc.add(Chunk.NEWLINE);
            return;
        }

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{
                1.8f, 1.2f, 2f, 1.5f, 1.2f, 1.2f});
        table.setSpacingAfter(16f);

        // Table header
        String[] headers = {
                "Reference No.", "Date", "Description",
                "Type", "Amount", "Balance"};
        Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 8,
                BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(header, headerFont));
            cell.setBackgroundColor(PRIMARY);
            cell.setPadding(8);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(
                    Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Table rows
        Font rowFont = FontFactory.getFont(
                FontFactory.HELVETICA, 7.5f,
                new BaseColor(30, 41, 59));
        Font refFont = FontFactory.getFont(
                FontFactory.COURIER, 7f,
                new BaseColor(100, 116, 139));

        boolean alternate = false;
        for (Transaction txn : transactions) {
            BaseColor rowBg = alternate
                    ? LIGHT_GRAY : BaseColor.WHITE;
            alternate = !alternate;

            boolean isCredit =
                    txn.getToAccount() != null
                    && txn.getToAccount().getId()
                           .equals(account.getId());

            // Reference
            addTableCell(table,
                    txn.getReferenceNumber() != null
                            ? txn.getReferenceNumber()
                            : "—",
                    refFont, rowBg,
                    Element.ALIGN_LEFT);

            // Date
            addTableCell(table,
                    txn.getTimestamp() != null
                            ? txn.getTimestamp()
                                 .format(DateTimeFormatter
                                     .ofPattern("dd MMM yy"))
                            : "—",
                    rowFont, rowBg,
                    Element.ALIGN_CENTER);

            // Description
            String desc = txn.getDescription() != null
                    ? txn.getDescription() : "—";
            if (desc.length() > 30) {
                desc = desc.substring(0, 27) + "...";
            }
            addTableCell(table, desc,
                    rowFont, rowBg,
                    Element.ALIGN_LEFT);

            // Type
            addTableCell(table,
                    txn.getType().name(),
                    rowFont, rowBg,
                    Element.ALIGN_CENTER);

            // Amount (colored)
            Font amtFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 8,
                    isCredit ? SUCCESS : DANGER);
            PdfPCell amtCell = new PdfPCell(
                    new Phrase(
                            (isCredit ? "+" : "-")
                            + "₹" + txn.getAmount()
                                       .toPlainString(),
                            amtFont));
            amtCell.setBackgroundColor(rowBg);
            amtCell.setPadding(7);
            amtCell.setBorder(Rectangle.NO_BORDER);
            amtCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT);
            table.addCell(amtCell);

            // Status
            Font statusFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 7,
                    txn.getStatus().name()
                       .equals("SUCCESS")
                            ? SUCCESS : DANGER);
            addTableCellFont(table,
                    txn.getStatus().name(),
                    statusFont, rowBg,
                    Element.ALIGN_CENTER);
        }

        doc.add(table);
    }

    private void addTableCell(PdfPTable table,
                               String text,
                               Font font,
                               BaseColor bg,
                               int align) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private void addTableCellFont(PdfPTable table,
                                   String text,
                                   Font font,
                                   BaseColor bg,
                                   int align) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    // ===== SUMMARY =====

    private void addSummary(Document doc,
                             List<Transaction> txns,
                             Account account,
                             User user)
            throws DocumentException {

        BigDecimal totalCredit = txns.stream()
                .filter(t -> t.getToAccount() != null
                        && t.getToAccount().getId()
                             .equals(account.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebit = txns.stream()
                .filter(t -> t.getFromAccount() != null
                        && t.getFromAccount().getId()
                             .equals(account.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Summary box
        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(8f);

        Font sumLabelFont = FontFactory.getFont(
                FontFactory.HELVETICA, 8,
                new BaseColor(100, 116, 139));
        Font sumValueFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11,
                new BaseColor(30, 41, 59));
        Font creditFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11,
                SUCCESS);
        Font debitFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11,
                DANGER);

        // Total Credit
        PdfPCell creditCell = new PdfPCell();
        creditCell.setBackgroundColor(
                new BaseColor(240, 253, 244));
        creditCell.setBorder(Rectangle.NO_BORDER);
        creditCell.setPadding(14);
        creditCell.setHorizontalAlignment(
                Element.ALIGN_CENTER);
        Paragraph creditPara = new Paragraph();
        creditPara.add(new Chunk(
                "Total Credits\n", sumLabelFont));
        creditPara.add(new Chunk(
                "₹" + totalCredit.toPlainString(),
                creditFont));
        creditCell.addElement(creditPara);
        summaryTable.addCell(creditCell);

        // Total Debit
        PdfPCell debitCell = new PdfPCell();
        debitCell.setBackgroundColor(
                new BaseColor(254, 242, 242));
        debitCell.setBorder(Rectangle.NO_BORDER);
        debitCell.setPadding(14);
        debitCell.setHorizontalAlignment(
                Element.ALIGN_CENTER);
        Paragraph debitPara = new Paragraph();
        debitPara.add(new Chunk(
                "Total Debits\n", sumLabelFont));
        debitPara.add(new Chunk(
                "₹" + totalDebit.toPlainString(),
                debitFont));
        debitCell.addElement(debitPara);
        summaryTable.addCell(debitCell);

        // Current Balance
        PdfPCell balCell = new PdfPCell();
        balCell.setBackgroundColor(
                new BaseColor(239, 246, 255));
        balCell.setBorder(Rectangle.NO_BORDER);
        balCell.setPadding(14);
        balCell.setHorizontalAlignment(
                Element.ALIGN_CENTER);
        Font balFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 11,
                PRIMARY_LIGHT);
        Paragraph balPara = new Paragraph();
        balPara.add(new Chunk(
                "Current Balance\n", sumLabelFont));
        balPara.add(new Chunk(
                "₹" + account.getBalance()
                             .toPlainString(),
                balFont));
        balCell.addElement(balPara);
        summaryTable.addCell(balCell);

        doc.add(summaryTable);

        // Disclaimer
        Font discFont = FontFactory.getFont(
                FontFactory.HELVETICA_OBLIQUE, 7.5f,
                new BaseColor(148, 163, 184));
        Paragraph disc = new Paragraph(
                "\nThis is a computer-generated statement "
                + "and does not require a signature. "
                + "For any discrepancies, please contact "
                + "your branch within 30 days.",
                discFont);
        disc.setSpacingBefore(12f);
        doc.add(disc);
    }

    // ===== PAGE EVENT — HEADER/FOOTER =====

    static class HeaderFooterEvent
            extends PdfPageEventHelper {

        private final String customerName;

        HeaderFooterEvent(String customerName) {
            this.customerName = customerName;
        }

        @Override
        public void onEndPage(PdfWriter writer,
                               Document document) {
            PdfContentByte cb =
                    writer.getDirectContent();
            BaseFont bf;
            try {
                bf = BaseFont.createFont(
                        BaseFont.HELVETICA,
                        BaseFont.CP1252,
                        BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                return;
            }

            // Footer line
            cb.setColorStroke(
                    new BaseColor(226, 232, 240));
            cb.moveTo(40, 45);
            cb.lineTo(555, 45);
            cb.stroke();

            // Footer text
            cb.beginText();
            cb.setFontAndSize(bf, 7.5f);
            cb.setColorFill(
                    new BaseColor(148, 163, 184));
            cb.moveText(40, 32);
            cb.showText("SecureBank — Confidential "
                    + "| " + customerName);
            cb.moveText(420, 0);
            cb.showText("Page "
                    + writer.getPageNumber());
            cb.endText();
        }
    }
}