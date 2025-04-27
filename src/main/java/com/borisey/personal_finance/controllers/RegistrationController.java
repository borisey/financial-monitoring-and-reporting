package com.borisey.personal_finance.controllers;

import com.borisey.personal_finance.models.User;
import com.borisey.personal_finance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegPage() {
        return "register";
    }

    @PostMapping
    public String registerUser(
            @RequestParam (required = true, defaultValue = "!@") String username,
            @RequestParam (required = true, defaultValue = "!@") String password
    ) {
        if (!(username.equals("!@") & password.equals("!@"))) {
            System.out.println(username + " " + password);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            if (userService.saveUser(user)) {
                System.out.println("GOOD");

                return "redirect:/my";
            } else {
                System.out.println("BAD");
            }
        }
        return "redirect:/my";
    }

    @GetMapping("/register-success")
    public String registerSuccess() {
        return "register-success";
    }

}
