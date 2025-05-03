package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.Bank;
import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.repo.BankRepository;
import com.borisey.personal_finance.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BankControllerTest {

    private BankRepository bankRepository;
    private UserService userService;
    private BankController bankController;

    @BeforeEach
    void setUp() {
        bankRepository = mock(BankRepository.class);
        userService = mock(UserService.class);

        bankController = new BankController();

        // Инжекция зависимостей вручную (через рефлексию)
        injectDependencies(bankController, "bankRepository", bankRepository);
        injectDependencies(bankController, "userService", userService);
    }

    @Test
    void testBankAdd() {
        Model model = mock(Model.class);
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(bankRepository.findByUserId(eq(1L), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        String result = bankController.bankAdd(model);

        assertEquals("bank-add", result);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute(eq("allUserBanks"), any());
        verify(model).addAttribute("h1", "Добавление банка");
    }

    // Вспомогательный метод инжекции зависимости
    private void injectDependencies(Object target, String fieldName, Object dependency) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, dependency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}