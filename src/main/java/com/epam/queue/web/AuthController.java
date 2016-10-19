package com.epam.queue.web;

import com.epam.queue.service.UserService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String register(@RequestBody Document doc) {
        Document user = new Document();
        userService.setUsername(user, doc.getString("username"));
        userService.setPassword(user, doc.getString("password"));
        userService.insertOne(user);
        return "login";
    }

    @RequestMapping("/login")
    public String login() {
        return "registration";
    }
}
