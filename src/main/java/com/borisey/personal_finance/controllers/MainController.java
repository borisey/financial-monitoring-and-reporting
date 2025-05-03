package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.*;
import com.borisey.personal_finance.repo.*;
import com.borisey.personal_finance.services.FormatService;
import com.borisey.personal_finance.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class MainController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private PersonTypeRepository personTypeRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;

    @GetMapping("/my")
    public String myPage(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            Model model
    ) {
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
            dateTo = currentDateTime.format(formatter);
            dateTimeTo = currentDateTime;
        } else {
            dateTimeTo = FormatService.formatDate(dateTo);
        }

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Категории

        // Передаю в вид все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.INCOME, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.EXPENSE, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Передаю в вид все типы лиц
        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        // Счета

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Общая сумма доходов
        Type typeIncome = typeRepository.findById(Type.INCOME).orElseThrow();
        Iterable<Balance> allUserIncome = balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(userId, typeIncome, dateTimeFrom, dateTimeTo);
        model.addAttribute("allUserIncome", allUserIncome);

        // Общая сумма расходов
        Type typeExpense = typeRepository.findById(Type.EXPENSE).orElseThrow();
        Iterable<Balance> allUserExpense = balanceRepository.findSumByUserIdTypeIdDateTimeFromDateTimeTo(userId, typeExpense, dateTimeFrom, dateTimeTo);
        model.addAttribute("allUserExpense", allUserExpense);

        // Общая сумма на всех счетах
        Iterable<Account> allUserAmount = accountRepository.findSumByUserId(userId);
        model.addAttribute("allUserAmount", allUserAmount);

        // Передаю в вид все транзакции пользователя
        Iterable<Balance> allUserTransactions = balanceRepository.findByUserIdDateTimeFromDateTimeTo(userId, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "date", "id"));
        model.addAttribute("allUserTransactions", allUserTransactions);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид все статусы транзакций
        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Дашборд");
        model.addAttribute("metaTitle", "Дашборд");
        model.addAttribute("metaDescription", "Дашборд");
        model.addAttribute("metaKeywords", "Дашборд");

        return "my";
    }

    @GetMapping("/")
    public String accountHome(Model model) {
        return "landing";
    }

    @GetMapping("/deletion-disallow")
    public String deletionDisallow() {
        return "deletion-disallow";
    }
}