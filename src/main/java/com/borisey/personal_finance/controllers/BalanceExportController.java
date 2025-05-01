package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.services.BalanceExportService;
import com.borisey.personal_finance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/export")
public class BalanceExportController {

    @Autowired
    private UserService userService;

    private final BalanceExportService balanceExportService;

    public BalanceExportController(BalanceExportService balanceExportService) {
        this.balanceExportService = balanceExportService;
    }

    @GetMapping("/balances")
    public ResponseEntity<InputStreamResource> exportBalances() throws IOException {

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        ByteArrayInputStream in = balanceExportService.exportUserBalancesToExcel(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=balances_user_" + userId + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}