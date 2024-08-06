package org.g9project4.main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class MainController {
    @GetMapping
    public String index(){
        return "front/main/index";
    }

}
