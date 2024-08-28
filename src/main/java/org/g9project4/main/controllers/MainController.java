package org.g9project4.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.visitCount.services.VisitorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/")
@Controller
@AllArgsConstructor
public class MainController {
    private final VisitorService visitorService;

    @GetMapping
    public String index(Model model, @ModelAttribute TourPlaceSearch search) {

        model.addAttribute("addCommonCss",List.of("banner"));
        model.addAttribute("addCss", "main"); // CSS 파일 목록
        model.addAttribute("addScript", "main"); // JS 파일 목록

        visitorService.recordVisit();

        return "front/main/index";
    }
}
