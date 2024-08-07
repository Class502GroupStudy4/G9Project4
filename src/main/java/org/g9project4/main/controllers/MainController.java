package org.g9project4.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class MainController {

    @GetMapping
    public String index(HttpServletRequest request, Model model){

        model.addAttribute("addCss", "main");//메인페이지
        model.addAttribute("addScript", "main"); //메인페이지

        return "front/main/index";
    }
}
