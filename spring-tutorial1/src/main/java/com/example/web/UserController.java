package com.example.web;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    UserRepository userRepository;


    @GetMapping
    public String userList(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    //form - все данные с формы
    @PostMapping
    public String editUser(
            @RequestParam Map<String, String> form,
            @RequestParam String username,
            @RequestParam("userId") User user) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::getAuthority)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles()
                        .add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
        return "redirect:user";
    }
}
