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
import java.util.List;

@Controller
public class BalanceController {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PersonTypeRepository personTypeRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;

    // Страница счетов пользователя
    @GetMapping("/transactions")
    public String getTransactions(HttpServletRequest request, Model model) {

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

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Передаю в вид все транзакции пользователя
        Iterable<Balance> allUserTransactions = balanceRepository.findByUserIdDateTimeFromDateTimeTo(userId, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "date", "id"));
        model.addAttribute("allUserTransactions", allUserTransactions);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Передаю в вид все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.INCOME, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.EXPENSE, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид все типы лиц
        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        // Передаю в вид все статусы транзакций
        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Транзакции");
        model.addAttribute("metaTitle", "Транзакции");
        model.addAttribute("metaDescription", "Транзакции");
        model.addAttribute("metaKeywords", "Транзакции");

        return "transactions";
    }

    // Добавление транзакцию
    @GetMapping("/transaction/add")
    public String transactionAdd(Model model) {

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление транзакции");
        model.addAttribute("metaTitle", "Добавление транзакции");
        model.addAttribute("metaDescription", "Добавление транзакции");
        model.addAttribute("metaKeywords", "Добавление транзакции");

        return "transaction-add";
    }

    // Добавление дохода
    @GetMapping("/transaction/income/add")
    public String transactionIncomeAdd(HttpServletRequest request, Model model) {

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

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        Type type = typeRepository.findById(Type.INCOME).orElseThrow();
        // Передаю в вид все транзакции пользователя
        Iterable<Balance> allUserTransactions = balanceRepository.findByUserIdTypeFromTo(userId, type, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "date", "id"));
        model.addAttribute("allUserTransactions", allUserTransactions);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Категории

        // Передаю в вид все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeId(userId, (byte) 1, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        // Счета

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Передаю в вид все типы лиц
        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        // Передаю в вид все статусы транзакций
        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление дохода");
        model.addAttribute("metaTitle", "Добавление дохода");
        model.addAttribute("metaDescription", "Добавление дохода");
        model.addAttribute("metaKeywords", "Добавление дохода");

        return "transaction-income-add";
    }

    // Добавление расхода
    @GetMapping("/transaction/expense/add")
    public String transactionExpenseAdd(HttpServletRequest request, Model model) {

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

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        Type type = typeRepository.findById(Type.EXPENSE).orElseThrow();
        // Передаю в вид все транзакции пользователя
        Iterable<Balance> allUserTransactions = balanceRepository.findByUserIdTypeFromTo(userId, type, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "date", "id"));
        model.addAttribute("allUserTransactions", allUserTransactions);

        // Категории

        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeId(userId, (byte) 2, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Счета

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Передаю в вид все типы лиц
        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид все статусы транзакций
        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление расхода");
        model.addAttribute("metaTitle", "Добавление расхода");
        model.addAttribute("metaDescription", "Добавление расхода");
        model.addAttribute("metaKeywords", "Добавление расхода");

        return "transaction-expense-add";
    }

    // Пополнение
    @PostMapping("/transaction/add-income")
    public String balanceAddIncome(
            HttpServletRequest request,
            @RequestParam Long categoryId,
            @RequestParam Long personTypeId,
            Long accountId,
            Double amount,
            String date,
            String inn,
            String phone,
            String comment,
            Long recipientBankId,
            Long senderBankId,
            String recipientAccountNumber,
            Long transactionStatusId
    ) {
        Balance balance = new Balance();

        // Сохраняю категорию
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        balance.setCategory(category);

        // Сохраняю счет
        Account account = accountRepository.findById(accountId).orElseThrow();
        balance.setAccount(account);

        // Пополняю счет
        account.setAmount(account.getAmount() + amount);
        accountRepository.save(account);

        // Сохраняю сумму
        balance.setAmount(amount);
        balance.setDate(FormatService.formatDate(date));

        // Сохраняю тип лица
        PersonType personType = personTypeRepository.findById(personTypeId).orElseThrow();
        balance.setPersonType(personType);

        Type type = typeRepository.findById(Type.INCOME).orElseThrow();
        balance.setType(type);

        // Сохраняю ИНН
        balance.setInn(inn);

        // Сохраняю расчетный счет получателя
        balance.setRecipientAccountNumber(recipientAccountNumber);

        // Сохраняю телефон
        balance.setPhone(phone);

        // Сохраняю комментарий
        balance.setComment(comment);

        // Сохраняю банк получателя
        Bank recipientBank = bankRepository.findById(recipientBankId).orElseThrow();
        balance.setRecipientBank(recipientBank);

        // Сохраняю банк отправителя
        Bank senderBank = bankRepository.findById(senderBankId).orElseThrow();
        balance.setSenderBank(senderBank);

        // Сохраняю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        balance.setUserId(currentUser.getId());

        // Сохраняю статус транзакции
        TransactionStatus transactionStatus = transactionStatusRepository.findById(transactionStatusId).orElseThrow();
        balance.setTransactionStatus(transactionStatus);

        // Сохраняю дату и время
        LocalDateTime currentDateTime = LocalDateTime.now();
        balance.setCreated(currentDateTime);
        balance.setUpdated(currentDateTime);

        // todo сделать проверку, что запись не вносится повторно
        balanceRepository.save(balance);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Списание
    @PostMapping("/transaction/add-expense")
    public String balanceAddExpense(
            HttpServletRequest request,
            @RequestParam Long categoryId,
            @RequestParam Long personTypeId,
            Long accountId,
            String date,
            Double amount,
            String inn,
            String phone,
            String comment,
            Long recipientBankId,
            Long senderBankId,
            String recipientAccountNumber,
            Long transactionStatusId
    ) {
        Balance balance = new Balance();

        // Сохраняю категорию
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        balance.setCategory(category);

        // Сохраняю счет
        Account account = accountRepository.findById(accountId).orElseThrow();
        balance.setAccount(account);

        // Списываю сумму со счета
        account.setAmount(account.getAmount() - amount);
        accountRepository.save(account);

        // Списание с отрицательным знаком
        balance.setAmount(-amount);
        balance.setDate(FormatService.formatDate(date));

        Type type = typeRepository.findById(Type.EXPENSE).orElseThrow();
        balance.setType(type);

        // Сохраняю ИНН
        balance.setInn(inn);

        // Сохраняю расчетный счет получателя
        balance.setRecipientAccountNumber(recipientAccountNumber);

        // Сохраняю телефон
        balance.setPhone(phone);

        // Сохраняю комментарий
        balance.setComment(comment);

        // Сохраняю банк получателя
        Bank recipientBank = bankRepository.findById(recipientBankId).orElseThrow();
        balance.setRecipientBank(recipientBank);

        // Сохраняю банк отправителя
        Bank senderBank = bankRepository.findById(senderBankId).orElseThrow();
        balance.setSenderBank(senderBank);

        // Сохраняю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        balance.setUserId(currentUser.getId());

        // Сохраняю тип лица
        PersonType personType = personTypeRepository.findById(personTypeId).orElseThrow();
        balance.setPersonType(personType);

        // Сохраняю статус транзакции
        TransactionStatus transactionStatus = transactionStatusRepository.findById(transactionStatusId).orElseThrow();
        balance.setTransactionStatus(transactionStatus);

        // Сохраняю дату и время
        LocalDateTime currentDateTime = LocalDateTime.now();
        balance.setCreated(currentDateTime);
        balance.setUpdated(currentDateTime);

        // todo сделать проверку, что запись не вносится повторно
        balanceRepository.save(balance);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Редактирование дохода
    @GetMapping("/transaction/income/{id}/edit")
    public String incomeEdit(
            @PathVariable(value = "id") Long id,
            Model model
    ) {
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        Balance transaction = balanceRepository.findByIdAndUserId(id, userId).orElseThrow();

        // Проверка статуса на запрет редактирования
        String statusTitle = transaction.getTransactionStatus() != null
                ? transaction.getTransactionStatus().getTitle()
                : "";

        List<String> forbiddenStatuses = List.of(
                "Подтвержденная",
                "В обработке",
                "Отменена",
                "Платеж выполнен",
                "Платеж удален",
                "Возврат"
        );

        if (forbiddenStatuses.contains(statusTitle)) {
            return "redirect:/edit-disallowed";
        }

        // Передача данных в вид
        model.addAttribute("transaction", transaction);

        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeId(userId, (byte) 1, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        model.addAttribute("username", username);

        model.addAttribute("h1", "Редактирование дохода");
        model.addAttribute("metaTitle", "Редактирование дохода");
        model.addAttribute("metaDescription", "Редактирование дохода");
        model.addAttribute("metaKeywords", "Редактирование дохода");

        return "transaction-income-edit";
    }

    // Редактирование дохода
    @PostMapping("/transaction/income/{id}/edit")
    public String incomeEdit(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam Long typeId,
            Long personTypeId,
            Double amount,
            Long categoryId,
            Long accountId,
            String date,
            String inn,
            String phone,
            String comment,
            Long recipientBankId,
            Long senderBankId,
            String recipientAccountNumber,
            Long transactionStatusId
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Balance transaction = balanceRepository.findByIdAndUserId(id, userId).orElseThrow();

        // Сохраняю категорию
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        transaction.setCategory(category);

        // Сохраняю счет
        Account account = accountRepository.findById(accountId).orElseThrow();
        transaction.setAccount(account);

        // Сохраняю тип лица
        PersonType personType = personTypeRepository.findById(personTypeId).orElseThrow();
        transaction.setPersonType(personType);

        // ИНН
        transaction.setInn(inn);

        // Расчетный счет получателя
        transaction.setRecipientAccountNumber(recipientAccountNumber);

        // Телефон
        transaction.setPhone(phone);

        // Комментарий
        transaction.setComment(comment);

        // Банк отправителя
        Bank senderBank = bankRepository.findById(senderBankId).orElseThrow();
        transaction.setSenderBank(senderBank);

        // Банк получателя
        Bank recipientBank = bankRepository.findById(recipientBankId).orElseThrow();
        transaction.setRecipientBank(recipientBank);

        // Сохраняю тип транзакции
        Type type = typeRepository.findById(typeId).orElseThrow();
        transaction.setType(type);

        // Сохраняю статус транзакции
        TransactionStatus transactionStatus = transactionStatusRepository.findById(transactionStatusId).orElseThrow();
        transaction.setTransactionStatus(transactionStatus);

        transaction.setAmount(amount);
        transaction.setDate(FormatService.formatDate(date));

        // Сохраняю ID текущего пользователя
        transaction.setUserId(currentUser.getId());

        // Сохраняю дату и время обновления записи
        LocalDateTime currentDateTime = LocalDateTime.now();
        transaction.setUpdated(currentDateTime);

        balanceRepository.save(transaction);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Редактирование расхода
    @GetMapping("/transaction/expense/{id}/edit")
    public String expenseEdit(
            @PathVariable(value = "id") Long id,
            Model model
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Пользователь не может редактировать чужие записи
        Balance transaction = balanceRepository.findByIdAndUserId(id, userId).orElseThrow();

        // Запрещённые к редактированию статусы
        List<String> forbiddenStatuses = List.of(
                "Подтвержденная",
                "В обработке",
                "Отменена",
                "Платеж выполнен",
                "Платеж удален",
                "Возврат"
        );

        if (forbiddenStatuses.contains(transaction.getTransactionStatus().getTitle())) {
            return "redirect:/edit-disallowed";
        }

        // Возвращаем положительное значение для формы
        transaction.setAmount(-transaction.getAmount());

        model.addAttribute("transaction", transaction);

        // Счета

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Категории
        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeId(userId, (byte) 2, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Передаю в вид все типы лиц
        Iterable<PersonType> allPersonTypes = personTypeRepository.findAll();
        model.addAttribute("allPersonTypes", allPersonTypes);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид все статусы транзакций
        Iterable<TransactionStatus> allTransactionStatuses = transactionStatusRepository.findAll();
        model.addAttribute("allTransactionStatuses", allTransactionStatuses);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Редактирование расхода");
        model.addAttribute("metaTitle", "Редактирование расхода");
        model.addAttribute("metaDescription", "Редактирование расхода");
        model.addAttribute("metaKeywords", "Редактирование расхода");

        return "transaction-expense-edit";
    }

    // Редактирование расхода
    @PostMapping("/transaction/expense/{id}/edit")
    public String expenseEdit(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam Long typeId,
            Long personTypeId,
            Double amount,
            Long categoryId,
            Long accountId,
            String date,
            String inn,
            String phone,
            String comment,
            Long recipientBankId,
            Long senderBankId,
            String recipientAccountNumber,
            Long transactionStatusId
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Balance transaction = balanceRepository.findByIdAndUserId(id, userId).orElseThrow();

        // Запрещённые к редактированию статусы
        String currentStatusTitle = transaction.getTransactionStatus() != null
                ? transaction.getTransactionStatus().getTitle()
                : "";

        List<String> forbiddenStatuses = List.of(
                "Подтвержденная",
                "В обработке",
                "Отменена",
                "Платеж выполнен",
                "Платеж удален",
                "Возврат"
        );

        if (forbiddenStatuses.contains(currentStatusTitle)) {
            return "redirect:/edit-disallowed";
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow();
        transaction.setCategory(category);

        // Сохраняю счет
        Account account = accountRepository.findById(accountId).orElseThrow();
        transaction.setAccount(account);

        // Сохраняю тип лица
        PersonType personType = personTypeRepository.findById(personTypeId).orElseThrow();
        transaction.setPersonType(personType);

        // Сохраняю статус транзакции
        TransactionStatus transactionStatus = transactionStatusRepository.findById(transactionStatusId).orElseThrow();
        transaction.setTransactionStatus(transactionStatus);

        // Списываю сумму со счета
        account.setAmount(account.getAmount() - amount);
        accountRepository.save(account);

        // ИНН
        transaction.setInn(inn);

        // Расчетный счет получателя
        transaction.setRecipientAccountNumber(recipientAccountNumber);

        // Телефон
        transaction.setPhone(phone);

        // Комментарий
        transaction.setComment(comment);

        // Банк отправителя
        Bank senderBank = bankRepository.findById(senderBankId).orElseThrow();
        transaction.setSenderBank(senderBank);

        // Банк получателя
        Bank recipientBank = bankRepository.findById(recipientBankId).orElseThrow();
        transaction.setRecipientBank(recipientBank);

        // Сохраняю тип транзакции
        Type type = typeRepository.findById(typeId).orElseThrow();
        transaction.setType(type);

        // У списания меняется знак
        transaction.setAmount(-amount);
        transaction.setDate(FormatService.formatDate(date));

        // Сохраняю ID текущего пользователя
        transaction.setUserId(currentUser.getId());

        // Сохраняю дату и время обновления записи
        LocalDateTime currentDateTime = LocalDateTime.now();
        transaction.setUpdated(currentDateTime);

        balanceRepository.save(transaction);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    @GetMapping("/transaction/{id}/delete")
    public String transactionDelete(
            HttpServletRequest request,
            @PathVariable(value = "id") long id
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может удалять чужие записи
        Balance transaction = balanceRepository.findByIdAndUserId(id, userId).orElseThrow();

        // Запрещённые к редактированию статусы
        String currentStatusTitle = transaction.getTransactionStatus() != null
                ? transaction.getTransactionStatus().getTitle()
                : "";

        List<String> protectedStatuses = List.of(
                "Подтвержденная",
                "В обработке",
                "Отменена",
                "Платеж выполнен",
                "Возврат"
        );

        if (protectedStatuses.contains(currentStatusTitle)) {
            return "redirect:/deletion-disallowed";
        }

        // Меняем статус на "Платеж удален"
        TransactionStatus deletedStatus = transactionStatusRepository.findByTitle("Платеж удален")
                .orElseThrow(() -> new RuntimeException("Статус 'Платеж удален' не найден"));

        // Устанавливаю новый статус для транзакции
        transaction.setTransactionStatus(deletedStatus);

        // Сохраняю измененную транзакцию
        balanceRepository.save(transaction);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    @GetMapping("/edit-disallowed")
    public String editDisallowed(Model model) {
        model.addAttribute("message", "Редактирование этой транзакции запрещено. Ее статус не позволяет это действие.");
        return "edit-disallowed";
    }

    @GetMapping("/deletion-disallowed")
    public String deletionDisallowed(Model model) {
        model.addAttribute("message", "Удаление этой транзакции запрещено. Ее статус не позволяет это действие.");
        return "deletion-disallowed";
    }
}