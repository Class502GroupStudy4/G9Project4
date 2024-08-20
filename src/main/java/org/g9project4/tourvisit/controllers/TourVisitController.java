package org.g9project4.tourvisit.controllers;


import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.BadRequestException;



//import org.g9project4.tourvisit.services.VisitInfoService;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.tourvisit.services.VisitInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tourvisit")
@RequiredArgsConstructor
public class TourVisitController {

  private final VisitInfoService visitInfoService;
    private final Utils utils;

    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();
        //pagination.setBaseURL();
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    private void commonProcess(String mode, Model model) {
        if (mode.equals("list")) {
            model.addAttribute("addCss", List.of("tourvisit/list"));
        } else if (mode.equals("detail")) {
            model.addAttribute("addCss", List.of("tour/map"));
            model.addAttribute("addScript", List.of("tour/detailMap"));
            model.addAttribute("addCommonScript", List.of("map"));
        }
    }


    // 일반 목록을 가져오는 메서드
    @GetMapping("/list")
    public String list(Model model, @ModelAttribute VisitSearch search) {
        search.setContentType(null);
        ListData<TourPlace> data = visitInfoService.getSearchedList(search);
        commonProcess("list", model);
        addListProcess(model, data);
        return utils.tpl("tourvisit/list"); // Thymeleaf 템플릿 이름
    }

    // 타입별 목록을 가져오는 메서드
    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, @ModelAttribute VisitSearch search, Model model) {
        try {
            search.setContentType(utils.typeCode(type));
            ListData<TourPlace> data = visitInfoService.getSearchedList(search);
            commonProcess("list", model);
            addListProcess(model, data);
            return "/list"; // Thymeleaf 템플릿 이름
        } catch (BadRequestException e) {
            e.printStackTrace();
            return "redirect:" + utils.redirectUrl("/tourvisit/list");
        }
    }


}
