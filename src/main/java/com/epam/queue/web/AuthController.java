package com.epam.queue.web;

import com.epam.queue.domain.Role;
import com.epam.queue.service.UserService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public String register() {
        return "registration";
    }

    @RequestMapping(value = "/register", method = POST)
    public String register(@RequestParam String email, @RequestParam String password) {
        Document user = new Document();
        userService.setUsername(user, email);
        userService.setPassword(user, password);
        userService.setAuthorities(user, Collections.singletonList(Role.ROLE_USER.getAuthority()));
        userService.insertOne(user);
        return "redirect:/login";
    }
}
