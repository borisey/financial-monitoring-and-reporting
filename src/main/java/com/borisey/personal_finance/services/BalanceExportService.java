package com.borisey.personal_finance.services;

import com.borisey.personal_finance.models.Balance;
import com.borisey.personal_finance.repo.BalanceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class BalanceExportService {

    private final BalanceRepository balanceRepository;

    public BalanceExportService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public ByteArrayInputStream exportUserBalancesToExcel(Long userId) throws IOException {
        List<Balance> balances = balanceRepository.findByUserId(userId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Баланс");

            // Заголовки
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Название");
            header.createCell(2).setCellValue("Сумма");
            header.createCell(3).setCellValue("Категория");
            header.createCell(4).setCellValue("Тип");
            header.createCell(5).setCellValue("Дата");

            int rowIdx = 1;
            for (Balance b : balances) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getTitle());
                row.createCell(2).setCellValue(b.getAmount());
                row.createCell(3).setCellValue(b.getCategory() != null ? b.getCategory().getTitle() : "");
                row.createCell(4).setCellValue(b.getType() != null ? b.getType().getTitle() : "");
                row.createCell(5).setCellValue(b.getDate() != null ? b.getDate().toString() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}