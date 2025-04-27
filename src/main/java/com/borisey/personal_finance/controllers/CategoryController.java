package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.Category;
import com.borisey.personal_finance.models.Type;
import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.repo.CategoryRepository;
import com.borisey.personal_finance.services.FormatService;
import com.borisey.personal_finance.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;

    // Страница категорий пользователя
    @GetMapping("/categories")
    public String getCategories(
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

        // Передаю в вид даты для запроса аналитики
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Передаю в вид все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.INCOME, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.EXPENSE, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Категории");
        model.addAttribute("metaTitle", "Категории");
        model.addAttribute("metaDescription", "Категории");
        model.addAttribute("metaKeywords", "Категории");

        return "categories";
    }

    // Добавление категории
    @GetMapping("/category/add")
    public String categoryAdd(Model model) {

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление категории");
        model.addAttribute("metaTitle", "Добавление категории");
        model.addAttribute("metaDescription", "Добавление категории");
        model.addAttribute("metaKeywords", "Добавление категории");

        return "category-add";
    }

    // Добавление новой категории доходов (зарплата, вклад) или расходов (еда, развлечения)
    @PostMapping("/category/add")
    public String categoryAdd(
            HttpServletRequest request,
            @RequestParam String title, Double budget, @Nullable Byte typeId
    )
    {
        Category category = new Category();
        LocalDateTime currentDateTime = LocalDateTime.now();
        category.setTitle(title);
        category.setTypeId(typeId);
        category.setBudget(budget);

        // Сохраняю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        category.setUserId(currentUser.getId());

        category.setCreated(currentDateTime);
        category.setUpdated(currentDateTime);
        categoryRepository.save(category);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Добавление категории дохода
    @GetMapping("/category/income/add")
    public String categoryIncomeAdd(
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
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид все категории доходов
        Iterable<Category> allUserIncomeCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.INCOME, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserIncomeCategories", allUserIncomeCategories);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление категории дохода");
        model.addAttribute("metaTitle", "Добавление категории дохода");
        model.addAttribute("metaDescription", "Добавление категории дохода");
        model.addAttribute("metaKeywords", "Добавление категории дохода");

        return "category-income-add";
    }

    // Добавление категории расхода
    @GetMapping("/category/expense/add")
    public String categoryExpenseAdd(
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
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид все категории расходов
        Iterable<Category> allUserExpensesCategories = categoryRepository.findByUserIdAndTypeIdAmount(userId, Type.EXPENSE, dateTimeFrom, dateTimeTo, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserExpensesCategories", allUserExpensesCategories);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление категории расхода");
        model.addAttribute("metaTitle", "Добавление категории расхода");
        model.addAttribute("metaDescription", "Добавление категории расхода");
        model.addAttribute("metaKeywords", "Добавление категории расхода");

        return "category-expense-add";
    }

    // Редактирование счета
    @GetMapping("/category/{id}/edit")
    public String categoryEdit(
            @PathVariable(value = "id") Long id,
            Model model
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Пользователь не может редактировать чужие записи
        Category category = categoryRepository.findByIdAndUserId(id, userId).orElseThrow();

        model.addAttribute("categories", category);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Редактирование категории");
        model.addAttribute("metaTitle", "Редактирование категории");
        model.addAttribute("metaDescription", "Редактирование категории");
        model.addAttribute("metaKeywords", "Редактирование категории");

        return "category-edit";
    }

    @PostMapping("/category/{id}/edit")
    public String categoryEdit(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id, Double budget,
            @RequestParam
            String title
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Category category = categoryRepository.findByIdAndUserId(id, userId).orElseThrow();

        category.setTitle(title);
        category.setBudget(budget);

        // Меняю дату обновления
        LocalDateTime currentDateTime = LocalDateTime.now();
        category.setUpdated(currentDateTime);

        categoryRepository.save(category);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    @GetMapping("/category/{id}/delete")
    public String categoryDelete(
            HttpServletRequest request,
            @PathVariable(value = "id") long id
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Category category = categoryRepository.findByIdAndUserId(id, userId).orElseThrow();

        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            return "redirect:/deletion-disallow";
        }

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }
}