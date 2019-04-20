package com.yangfan.chat.controller;

import com.yangfan.chat.exception.DuplicateUserException;
import com.yangfan.chat.exception.UserNotFoundException;
import com.yangfan.chat.model.dto.UserDto;
import com.yangfan.chat.model.dto.UserRegistrationDto;
import com.yangfan.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

/**
 * Views, override ViewResolver if needed
 */
@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/index", "/index.html", "/", ""})
    public String index(Model model, Principal principal) throws UserNotFoundException {
        String userName = principal.getName();
        UserDto userDto = userService.getUserDtoByUsername(userName);
        model.addAttribute("appName", "Yangfan's Chat");
        model.addAttribute("userName", userName);
        model.addAttribute("user", userDto);
        return "index";
    }

    @GetMapping(value = {"/sign-up", "/sign-up.html"})
    public String signUp(Model model) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        model.addAttribute("appName", "Yangfan's Chat");
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String register(Model model,
            @ModelAttribute("user") @Validated UserRegistrationDto userRegistrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userError", "Failed to bind form data!");
        }

        try {
            UserDto user = userService.addNewUser(userRegistrationDto);
        } catch (DuplicateUserException e) {
            model.addAttribute("userError", "Username already exists!");
            return "/sign-up";
        }

        return "redirect:/log-in";
    }

    @GetMapping("/log-in")
    public String logIn(Model model) {
        model.addAttribute("appName", "Yangfan's Chat");
        return "log-in";
    }


}
