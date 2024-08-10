package org.g9project4.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/")
@Controller
public class MainController {

    @GetMapping
    public String index( Model model){

        model.addAttribute("addCss", "main"); // CSS 파일 목록
        model.addAttribute("addScript", "main"); // JS 파일 목록

        return "front/main/index";
    }
}
