package com.borisey.personal_finance.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.borisey.personal_finance.models.*;
import com.borisey.personal_finance.repo.AccountRepository;
import com.borisey.personal_finance.repo.BalanceRepository;
import com.borisey.personal_finance.repo.CategoryRepository;
import com.borisey.personal_finance.repo.TypeRepository;
import com.borisey.personal_finance.services.FormatService;
import com.borisey.personal_finance.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalyticsController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/analytics")
    public String getAnalytics(
            HttpServletRequest request,
            Model model
    ) {
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        LocalDateTime dateTimeFrom;
        LocalDateTime dateTimeTo;

        LocalDateTime currentDateTime = LocalDateTime.now();

        if (StringUtils.isEmpty(dateFrom)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateFrom = currentDateTime.withDayOfMonth(1).format(formatter);
            dateTimeFrom = currentDateTime.withDayOfMonth(1);
        } else {
            dateTimeFrom = FormatService.formatDate(dateFrom);
        }

        if (StringUtils.isEmpty(dateTo)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateTimeTo = currentDateTime;
            dateTo = currentDateTime.format(formatter);
        } else {
            dateTimeTo = FormatService.formatDate(dateTo);
        }

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Передаю в вид имя пользователя
        String username = currentUser.getUsername();
        model.addAttribute("username", username);

        // Получаю все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.INCOME, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        // Передаю в вид все категории расходов
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        Map<String, Double> chartIncomeData = new TreeMap<>();
        for (Category category : allUserIncomeCategories) {
            chartIncomeData.put(category.getTitle(), category.getAllamount());
        }

        // Передаю в вид доходы
        model.addAttribute("chartIncomeData", chartIncomeData);

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Получаю все категории расходов
        Iterable<Category> allUserExpenseCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.EXPENSE, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        // Передаю в вид все категории расходов
        model.addAttribute("allUserExpenseCategories", allUserExpenseCategories);

        Map<String, Double> chartExpenseData = new TreeMap<>();
        for (Category category : allUserExpenseCategories) {
            chartExpenseData.put(category.getTitle(), -category.getAllamount());
        }

        // Передаю в вид расходы
        model.addAttribute("chartExpenseData", chartExpenseData);

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Общая сумма доходов
        Type typeIncome = typeRepository.findById(Type.INCOME).orElseThrow();
        Iterable<Balance> allUserIncome = balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(userId, typeIncome, dateTimeFrom, dateTimeTo);
        model.addAttribute("allUserIncome", allUserIncome);

        // Общая сумма расходов
        Type typeExpense = typeRepository.findById(Type.EXPENSE).orElseThrow();
        Iterable<Balance> allUserExpense = balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(userId, typeExpense, dateTimeFrom, dateTimeTo);
        model.addAttribute("allUserExpense", allUserExpense);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Аналитика");
        model.addAttribute("metaTitle", "Аналитика");
        model.addAttribute("metaDescription", "Аналитика");
        model.addAttribute("metaKeywords", "Аналитика");

        return "analytics";
    }
}