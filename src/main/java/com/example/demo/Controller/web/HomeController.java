package com.example.demo.Controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/index")
    public String home() {
        return "home/index";
    }

}
