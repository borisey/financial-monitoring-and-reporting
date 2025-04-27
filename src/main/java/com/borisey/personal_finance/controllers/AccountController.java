package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.*;
import com.borisey.personal_finance.repo.AccountRepository;
import com.borisey.personal_finance.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/account/add")
    public String accountAccountAdd(
            HttpServletRequest request,
            @RequestParam String title,
            Double amount
    ) {
        Account account = new Account();

        // todo сделать проверку, что запись с таким названием не вносится повторно
        account.setTitle(title);

        account.setAmount(amount);

        // Сохраняю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        account.setUserId(currentUser.getId());

        // Сохраняю дату и время
        LocalDateTime currentDateTime = LocalDateTime.now();
        account.setCreated(currentDateTime);
        account.setUpdated(currentDateTime);

        accountRepository.save(account);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Страница счетов пользователя
    @GetMapping("/accounts")
    public String getAccounts(Model model) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Счета");
        model.addAttribute("metaTitle", "Счета");
        model.addAttribute("metaDescription", "Счета");
        model.addAttribute("metaKeywords", "Счета");

        return "accounts";
    }

    // Добавление счета
    @GetMapping("/account/add")
    public String accountAdd(Model model) {

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        String username = currentUser.getUsername();
        Long userId = currentUser.getId();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Счета

        // Передаю в вид все счета пользователя
        Iterable<Account> allUserAccounts = accountRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserAccounts", allUserAccounts);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление счета");
        model.addAttribute("metaTitle", "Добавление счета");
        model.addAttribute("metaDescription", "Добавление счета");
        model.addAttribute("metaKeywords", "Добавление счета");

        return "account-add";
    }

    // Редактирование счета
    @GetMapping("/account/{id}/edit")
    public String accountEdit(
            @PathVariable(value = "id") Long id,
            Model model
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Пользователь не может редактировать чужие записи
        Account account = accountRepository.findByIdAndUserId(id, userId).orElseThrow();

        model.addAttribute("account", account);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Редактирование счета");
        model.addAttribute("metaTitle", "Редактирование счета");
        model.addAttribute("metaDescription", "Редактирование счета");
        model.addAttribute("metaKeywords", "Редактирование счета");

        return "account-edit";
    }

    @PostMapping("/account/{id}/edit")
    public String accountEdit(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam
            Double amount,
            String title
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Account account = accountRepository.findByIdAndUserId(id, userId).orElseThrow();

        account.setAmount(amount);
        account.setTitle(title);
        accountRepository.save(account);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    @GetMapping("/account/{id}/delete")
    public String linkLinkRemove(
            HttpServletRequest request,
            @PathVariable(value = "id") long id
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может удалять чужие записи
        Account account = accountRepository.findByIdAndUserId(id, userId).orElseThrow();

        try {
            accountRepository.delete(account);
        } catch (Exception e) {
            return "redirect:/deletion-disallow";
        }

        accountRepository.delete(account);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

}