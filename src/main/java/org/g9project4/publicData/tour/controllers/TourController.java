package org.g9project4.publicData.tour.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController implements ExceptionProcessor {
    private final TourPlaceRepository tourPlaceRepository;
    private final TourPlaceInfoService placeInfoService;
    private final TourDetailInfoService detailInfoService;
    private final Utils utils;
    @PersistenceContext
    private EntityManager em;

    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("addCommonScript", List.of("map"));
        model.addAttribute("addScript", List.of("tour/view"));
        return "front/tour/view";
    }

    @GetMapping("/list")
    public String list(Model model, TourPlaceSearch search) {


        ListData<TourPlace> data = placeInfoService.getTotalList(search);
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());
        return "front/tour/list";
    }

    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, @RequestParam(value = "page", defaultValue = "10") int page, @RequestParam(value = "size", defaultValue = "10") int size, Model model) {

        try {
            TourPlaceSearch search = TourPlaceSearch.builder()
                    .page(page)
                    .limit(size)
                    .contentType(utils.typeCode(type))
                    .build();
            ListData<TourPlace> data = placeInfoService.getSearchedList(search);

            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());
            return "front/tour/list";
        } catch (BadRequestException e) {
            e.printStackTrace();
            return "redirect:/tour/list";
        }
    }

    @GetMapping("/detail/{contentId}")
    public String detail(@PathVariable("contentId") Long contentId, Model model) {
        DetailItem item = detailInfoService.getDetail(contentId);
        model.addAttribute("addCommonScript", List.of("map"));
        model.addAttribute("addScript", List.of("tour/detailview"));
        model.addAttribute("items", item);
        return "front/tour/detail";
    }
}
