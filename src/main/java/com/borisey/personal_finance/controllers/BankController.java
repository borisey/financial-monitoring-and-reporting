package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.Account;
import com.borisey.personal_finance.models.Bank;
import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.repo.BankRepository;
import com.borisey.personal_finance.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class BankController {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private UserService userService;

    // Страница банков
    @GetMapping("/banks")
    public String getBanks(Model model) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Банки");
        model.addAttribute("metaTitle", "Банки");
        model.addAttribute("metaDescription", "Банки");
        model.addAttribute("metaKeywords", "Банки");

        return "banks";
    }

    // Добавление банка
    @GetMapping("/bank/add")
    public String bankAdd(Model model) {

        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        String username = currentUser.getUsername();
        Long userId = currentUser.getId();

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид все банки пользователя
        Iterable<Bank> allUserBanks = bankRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("allUserBanks", allUserBanks);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Добавление банка");
        model.addAttribute("metaTitle", "Добавление банка");
        model.addAttribute("metaDescription", "Добавление банка");
        model.addAttribute("metaKeywords", "Добавление банка");

        return "bank-add";
    }

    @PostMapping("/bank/add")
    public String bankAdd(
            HttpServletRequest request,
            @RequestParam String title
    ) {
        Bank bank = new Bank();

        // todo сделать проверку, что запись с таким названием не вносится повторно
        bank.setTitle(title);

        // Сохраняю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        bank.setUserId(currentUser.getId());

        bankRepository.save(bank);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }

    // Редактирование банка
    @GetMapping("/bank/{id}/edit")
    public String bankEdit(
            @PathVariable(value = "id") Long id,
            Model model
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        String username = currentUser.getUsername();

        // Пользователь не может редактировать чужие записи
        Bank bank = bankRepository.findByIdAndUserId(id, userId).orElseThrow();

        model.addAttribute("bank", bank);

        // Передаю в вид имя пользователя
        model.addAttribute("username", username);

        // Передаю в вид метатэги
        model.addAttribute("h1", "Редактирование банка");
        model.addAttribute("metaTitle", "Редактирование банка");
        model.addAttribute("metaDescription", "Редактирование банка");
        model.addAttribute("metaKeywords", "Редактирование банка");

        return "bank-edit";
    }

    @PostMapping("/bank/{id}/edit")
    public String bankEdit(
            HttpServletRequest request,
            @PathVariable(value = "id") Long id,
            @RequestParam
            String title
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может редактировать чужие записи
        Bank bank = bankRepository.findByIdAndUserId(id, userId).orElseThrow();

        bank.setTitle(title);
        bankRepository.save(bank);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }


    @GetMapping("/bank/{id}/delete")
    public String bankDelete(
            HttpServletRequest request,
            @PathVariable(value = "id") long id
    ) {
        // Получаю ID текущего пользователя
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // Пользователь не может удалять чужие банки
        Bank bank = bankRepository.findByIdAndUserId(id, userId).orElseThrow();

        try {
            bankRepository.delete(bank);
        } catch (Exception e) {
            return "redirect:/deletion-disallow";
        }

        bankRepository.delete(bank);

        String referrer = request.getHeader("Referer");

        return "redirect:" + referrer;
    }
}
