package org.g9project4.publicData.tour.controllers;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.BadRequestException;
import org.g9project4.global.exceptions.ExceptionProcessor;
import org.g9project4.global.exceptions.TourPlaceNotFoundException;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.publicData.greentour.entities.GreenPlace;
import org.g9project4.publicData.tour.constants.ContentType;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.TourDetailInfoService;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
import org.g9project4.search.services.SearchHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController implements ExceptionProcessor {
    private final TourPlaceRepository tourPlaceRepository;
    private final TourPlaceInfoService placeInfoService;
    private final TourDetailInfoService detailInfoService;
    private final Utils utils;
    private final SearchHistoryService historyService;

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
            addCommonCss.add("banner");
            addCss.addAll(List.of("tour/list","tour/_typelist"));
        } else if (mode.equals("detail")) {
            addCss.add("tour/map");
            addScript.add("tour/detailMap");
            addCommonScript.add("map");
        } else if (mode.equals("view")) {
            addCss.addAll(List.of("tour/map","tour/sidebar"));
            addScript.addAll(List.of("tour/map","tour/sidebar"));

        } else if(mode.equals("geoLocation")){
            addCommonCss.add("banner");
            addCss.addAll(List.of("tour/list","tour/_typelist"));
            addScript.add("tour/locBased");
        }
        model.addAttribute("addCss",addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }


    private String greenList(TourPlaceSearch search, Model model) {

        ListData<GreenPlace> items = null;
        try {
            items = placeInfoService.getGreenList(search);
        } catch (Exception e) {
            throw new TourPlaceNotFoundException();
        }
        commonProcess("list", model);
        model.addAttribute("items", items.getItems());
        model.addAttribute("pagination", items.getPagination());

        return utils.tpl("tour/list");
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
        search.setContentType(null);
        ListData<TourPlace> data = placeInfoService.getSearchedList(search);
        commonProcess("list", model);
        addListProcess(model, data);
        historyService.saveTour(search.getSkey());

        return utils.tpl("tour/list");
    }

    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, @ModelAttribute TourPlaceSearch search, Model model) {

        try {
            search.setContentType(utils.typeCode(type));
            if (search.getContentType() == ContentType.GreenTour) {
                return greenList(search, model);
            }
            ListData<TourPlace> data = placeInfoService.getSearchedList(search);
            commonProcess("list", model);
            addListProcess(model, data);
            System.out.println(search);
//            historyService.saveTour(search.getSkey());

            return utils.tpl("tour/list");
        } catch (BadRequestException e) {
            e.printStackTrace();
            return "redirect:" + utils.redirectUrl("/tour/list");
        }
    }

    @GetMapping("/list/loc/{type}")
    public String distanceList(@PathVariable("type") String type, @ModelAttribute TourPlaceSearch search, Model model) {
        try{
            commonProcess("getLocation",model);
            search.setLatitude(37.566826);
            search.setLongitude(126.9786567);
            search.setRadius(1000);
            ListData<TourPlace> data = placeInfoService.getLocBasedList(search);
            addListProcess(model,data);
        }catch(Exception e){
            e.printStackTrace();
            throw new TourPlaceNotFoundException();
        }
        return utils.tpl("tour/list");
    }

    @GetMapping("/detail/{contentId}")
    public String detail(@PathVariable("contentId") Long contentId, Model model) {
        DetailItem item = detailInfoService.getDetail(contentId);
        commonProcess("detail", model);
        model.addAttribute("items", item);
        return utils.tpl("tour/detail");
    }

}
