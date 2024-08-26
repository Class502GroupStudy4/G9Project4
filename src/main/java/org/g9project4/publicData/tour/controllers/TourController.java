package org.g9project4.publicData.tour.controllers;

import lombok.RequiredArgsConstructor;
import org.g9project4.config.controllers.ApiConfig;
import org.g9project4.config.service.ConfigInfoService;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.BadRequestException;
import org.g9project4.global.exceptions.ExceptionProcessor;
import org.g9project4.global.exceptions.TourPlaceNotFoundException;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.global.rests.gov.detailpetapi.DetailPetItem;
import org.g9project4.publicData.greentour.entities.GreenPlace;
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.entities.PlaceDetail;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.TourDetailInfoService;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController implements ExceptionProcessor {
    private final TourPlaceRepository tourPlaceRepository;
    private final TourPlaceInfoService placeInfoService;
    private final TourDetailInfoService detailInfoService;
    private final ConfigInfoService configInfoService;
    private final NewTourPlaceInfoService newTourPlaceInfoService;
    private final Utils utils;
    private final AreaCodeRepository areaCodeRepository;
    private final SigunguCodeRepository sigunguCodeRepository;
    private final CategoryRepository categoryRepository;

    @ModelAttribute("apiKeys")
    public ApiConfig getApiKeys() {
        return configInfoService.get("apiConfig", ApiConfig.class).orElseGet(ApiConfig::new);
    }

    @ModelAttribute("areaCodes")
    public List<AreaCode> getAreaCodes(){
        return areaCodeRepository.findAll();
    }
    @ModelAttribute("category1")
    public List<Object[]> getCategory1(){
        return categoryRepository.findDistinctName1();
    }

    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();
        //pagination.setBaseURL();
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    private void commonProcess(String mode, Model model) {
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        if (mode.equals("list")) {
            addCss.addAll(List.of("tour/list", "tour/_typelist"));
            addScript.addAll(List.of("tour/locBased","tour/form"));
        } else if (mode.equals("geolocation")) {
            addCss.addAll(List.of("tour/list", "tour/_typelist"));
            addScript.add("tour/locBased");
        } else if (mode.equals("detail")) {
            addCss.add("tour/map");
            addScript.add("tour/detailMap");
            addCommonScript.add("map");
        } else if (mode.equals("view")) {
            addCss.addAll(List.of("tour/map", "tour/sidebar"));
            addScript.addAll(List.of("tour/map", "tour/sidebar"));
        }
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }

    @GetMapping("/popup")
    public String popup(Model model) {
        return utils.tpl("tour/popup");
    }

    @GetMapping("/view")
    public String view(Model model, @ModelAttribute TourPlaceSearch search) {
        search.setContentType(null);
        ListData<TourPlace> data = placeInfoService.getSearchedList(search);
        commonProcess("view", model);
        addListProcess(model, data);
        return utils.tpl("tour/map");
    }

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute TourPlaceSearch search) {
        try {
            ListData<TourPlace> data = newTourPlaceInfoService.getSearchedList(search);
            commonProcess("list", model);
            addListProcess(model, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utils.tpl("tour/list");
    }

    @GetMapping("/detail/{contentId}")
    public String detail(@PathVariable("contentId") Long contentId, Model model) {
        PlaceDetail<DetailItem, DetailPetItem> item = detailInfoService.getDetail(contentId);
        commonProcess("detail", model);
        model.addAttribute("items", item);
        return utils.tpl("tour/detail");
    }

}