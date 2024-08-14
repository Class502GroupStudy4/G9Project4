package org.g9project4.publicData.tour.controllers;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.BadRequestException;
import org.g9project4.global.exceptions.ExceptionProcessor;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.TourDetailInfoService;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController implements ExceptionProcessor {
    private final TourPlaceRepository tourPlaceRepository;
    private final TourPlaceInfoService placeInfoService;
    private final TourDetailInfoService detailInfoService;
    private final Utils utils;

    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();
        //pagination.setBaseURL();
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    private void commonProcess(String mode, Model model) {
        if (mode.equals("list")) {
            model.addAttribute("addCss", List.of("tour/list"));
        } else if (mode.equals("detail")) {
            model.addAttribute("addCss", List.of("tour/map"));
            model.addAttribute("addScript",List.of("tour/detailMap"));
            model.addAttribute("addCommonScript", List.of("map"));
        }else if(mode.equals("view")){
            model.addAttribute("addCss", List.of("tour/map", "tour/sidebar"));
            model.addAttribute("addScript", List.of("tour/map", "tour/sidebar"));
        }
    }

    @GetMapping("/view")
    public String view(Model model) {
        commonProcess("view", model);
        return utils.tpl("/tour/map");
    }

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute TourPlaceSearch search) {
        search.setContentType(null);
        ListData<TourPlace> data = placeInfoService.getSearchedList(search);
        commonProcess("list", model);
        addListProcess(model, data);
        return utils.tpl("tour/list");
    }

    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, @ModelAttribute TourPlaceSearch search, Model model) {

        try {
            search.setContentType(utils.typeCode(type));
            ListData<TourPlace> data = placeInfoService.getSearchedList(search);
            commonProcess("list", model);
            addListProcess(model, data);
            return utils.tpl("tour/list");
        } catch (BadRequestException e) {
            e.printStackTrace();
            return "redirect:" + utils.redirectUrl("/tour/list");
        }
    }

    @GetMapping("/detail/{contentId}")
    public String detail(@PathVariable("contentId") Long contentId, Model model) {
        DetailItem item = detailInfoService.getDetail(contentId);
        commonProcess("detail", model);
        model.addAttribute("items", item);
        return utils.tpl("tour/detail");
    }
}
