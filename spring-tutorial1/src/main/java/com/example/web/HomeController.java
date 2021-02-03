package com.example.web;

import com.example.model.Message;
import com.example.model.User;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 1) Добаваил
 */
@Controller
public class HomeController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting(
                    Map<String, Object> model) {
        return "greetings";  //returns name of a view
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String tag,
                       Model model) {
        Iterable<Message> messages;
        if (tag == null || tag.isEmpty() ) {
            messages = messageRepository.findAll();
        } else {
            messages = messageRepository.findByTag(tag);
        }

        model.addAttribute("messages", messages);
        model.addAttribute("tag", tag);
        return "main";
    }

    //создает новый месадж и заносит его в базу
    //RequestParam from input/url
    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @RequestParam String text,
            @RequestParam String tag,
            Map<String, Object> model) throws IOException {
        Message message = new Message(text, tag);
        message.setUser(user);
        //если файл загруже
        if (file != null) {
            //проверяем сущствует ли директория для загрузки файлов
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();  //создадим если не существует директории
            }

            String uuidFile = UUID.randomUUID().toString(); //создадим уникальное имя файла (нет колизиям)
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            message.setFilename(resultFilename);
        }
        messageRepository.save(message);
        Iterable<Message> messages = messageRepository.findAll();

        model.put("messages", messages);
        return "main";
    }
}
