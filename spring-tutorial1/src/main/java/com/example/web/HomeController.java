package com.example.web;

import com.example.model.Message;
import com.example.model.User;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 1) Добаваил
 */
@Controller
public class HomeController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting(
                    Map<String, Object> model) {
        return "greetings";  //returns name of a view
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        Iterable<Message> messages = messageRepository.findAll();
        model.put("messages", messages);
        return "main";
    }

    //создает новый месадж и заносит его в базу
    //RequestParam from input/url
    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
            @RequestParam String text,@RequestParam String tag, Map<String, Object> model) {
        Message message = new Message(text, tag);
        message.setUser(user);
        messageRepository.save(message);
        Iterable<Message> messages = messageRepository.findAll();

        model.put("messages", messages);
        return "main";
    }

    //фильтрует по тэгу и возвращает вью где выводятся все сообщения
    @PostMapping("filter")
    public String filter (@RequestParam String tag, Map<String, Object> model) {
        //iterable, потому как findAll вернет List, а findByTag - Iterable
        Iterable<Message> messages;
        if (tag == null || tag.isEmpty() ) {
            messages = messageRepository.findAll();
        }
        messages = messageRepository.findByTag(tag);
        model.put("messages", messages);
        return "main";
    }
}
