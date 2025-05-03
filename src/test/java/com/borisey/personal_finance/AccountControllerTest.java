package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.Account;
import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.repo.AccountRepository;
import com.borisey.personal_finance.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AccountControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountController = new AccountController(accountRepository, userService);
    }

    @Test
    void testAccountAccountAdd() {
        String title = "Test Account";
        Double amount = 1000.0;
        User testUser = new User();
        testUser.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(request.getHeader("Referer")).thenReturn("http://localhost/account/add");

        String result = accountController.accountAccountAdd(request, title, amount);

        verify(accountRepository).save(any(Account.class));
        assertEquals("redirect:http://localhost/account/add", result);
    }

    @Test
    void testGetAccounts() {
        // Arrange
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUserId(eq(1L), any())).thenReturn(java.util.Collections.emptyList());

        String result = accountController.getAccounts(model);

        assertEquals("accounts", result);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute("allUserAccounts", java.util.Collections.emptyList());
        verify(model).addAttribute("h1", "Счета");
    }

    @Test
    void testAccountAdd() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUserId(eq(1L), any())).thenReturn(java.util.Collections.emptyList());

        String result = accountController.accountAdd(model);

        assertEquals("account-add", result);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute("allUserAccounts", java.util.Collections.emptyList());
        verify(model).addAttribute("h1", "Добавление счета");
    }

    @Test
    void testAccountEdit() {
        Long accountId = 1L;
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        Account account = new Account();
        account.setId(accountId);
        account.setUserId(1L);
        account.setTitle("Test Account");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserId(eq(accountId), eq(1L))).thenReturn(java.util.Optional.of(account));

        String result = accountController.accountEdit(accountId, model);

        assertEquals("account-edit", result);
        verify(model).addAttribute("account", account);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute("h1", "Редактирование счета");
    }

    @Test
    void testAccountEditPost() {
        Long accountId = 1L;
        Double amount = 2000.0;
        String title = "Updated Account";
        User testUser = new User();
        testUser.setId(1L);

        Account account = new Account();
        account.setId(accountId);
        account.setUserId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserId(eq(accountId), eq(1L))).thenReturn(java.util.Optional.of(account));
        when(request.getHeader("Referer")).thenReturn("http://localhost/account/1/edit");

        String result = accountController.accountEdit(request, accountId, amount, title);

        assertEquals("redirect:http://localhost/account/1/edit", result);
        verify(accountRepository).save(account);
    }

    @Test
    void testLinkLinkRemove() {
        Long accountId = 1L;
        User testUser = new User();
        testUser.setId(1L);

        Account account = new Account();
        account.setId(accountId);
        account.setUserId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUserId(eq(accountId), eq(1L))).thenReturn(java.util.Optional.of(account));
        when(request.getHeader("Referer")).thenReturn("http://localhost/accounts");

        String result = accountController.linkLinkRemove(request, accountId);

        assertEquals("redirect:http://localhost/accounts", result);
        verify(accountRepository, times(1)).delete(account);
    }
}