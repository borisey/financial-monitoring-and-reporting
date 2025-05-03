package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.*;
import com.borisey.personal_finance.repo.*;
import com.borisey.personal_finance.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnalyticsControllerTest {

    @InjectMocks
    private AnalyticsController analyticsController;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TypeRepository typeRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAnalytics_DefaultDates() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(request.getParameter("dateFrom")).thenReturn(null);
        when(request.getParameter("dateTo")).thenReturn(null);

        Category incomeCategory = new Category();
        incomeCategory.setTitle("Salary");
        incomeCategory.setAllamount(1000.0);
        Iterable<Category> incomeCategories = List.of(incomeCategory);

        Category expenseCategory = new Category();
        expenseCategory.setTitle("Food");
        expenseCategory.setAllamount(500.0);
        Iterable<Category> expenseCategories = List.of(expenseCategory);

        when(categoryRepository.findByUserIdAndTypeIdAmount(eq(1L), eq(Type.INCOME), any(), any(), any()))
                .thenReturn(incomeCategories);
        when(categoryRepository.findByUserIdAndTypeIdAmount(eq(1L), eq(Type.EXPENSE), any(), any(), any()))
                .thenReturn(expenseCategories);

        when(accountRepository.findByUserId(eq(1L), any())).thenReturn(Collections.emptyList());

        Type incomeType = new Type();
        incomeType.setId(1L);
        when(typeRepository.findById((byte)1)).thenReturn(Optional.of(incomeType));

        Type expenseType = new Type();
        expenseType.setId(2L);
        when(typeRepository.findById((byte)2)).thenReturn(Optional.of(expenseType));

        when(balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(eq(1L), eq(incomeType), any(), any()))
                .thenReturn(Collections.emptyList());
        when(balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(eq(1L), eq(expenseType), any(), any()))
                .thenReturn(Collections.emptyList());

        String viewName = analyticsController.getAnalytics(request, model);

        assertEquals("analytics", viewName);
        verify(model).addAttribute(eq("username"), eq("testuser"));
        verify(model).addAttribute(eq("allUserIncomeCategories"), eq(incomeCategories));
        verify(model).addAttribute(eq("allUserExpenseCategories"), eq(expenseCategories));
        verify(model).addAttribute(eq("chartIncomeData"), anyMap());
        verify(model).addAttribute(eq("chartExpenseData"), anyMap());
        verify(model).addAttribute(eq("allUserAccounts"), any());
        verify(model).addAttribute(eq("allUserIncome"), any());
        verify(model).addAttribute(eq("allUserExpense"), any());
    }
}