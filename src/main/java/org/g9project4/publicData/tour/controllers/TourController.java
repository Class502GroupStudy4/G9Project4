package org.g9project4.publicData.tour.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
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

    private final TourPlaceInfoService placeInfoService;
    private final Utils utils;


    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();
        //pagination.setBaseURL();
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    private void commonProcess(String mode, Model model) {
        if (mode.equals("list")) {
            model.addAttribute("addCommonCss",List.of("banner"));
            model.addAttribute("addCss", List.of("tour/list","tour/_typelist"));
        } else if (mode.equals("detail")) {
            model.addAttribute("addCss", List.of("tour/map"));
            model.addAttribute("addScript", List.of("tour/detailMap"));
            model.addAttribute("addCommonScript", List.of("map"));
        } else if (mode.equals("view")) {
            model.addAttribute("addCss", List.of("tour/map", "tour/sidebar"));
            model.addAttribute("addScript", List.of("tour/map", "tour/sidebar"));
        }
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
    public String view(Model model) {
        commonProcess("view", model);
        return utils.tpl("/tour/map");
    }


    @GetMapping("/list")
    public String list(@ModelAttribute TourPlaceSearch search, Model model) {
        // 기본 정렬 기준과 방향 설정
        String sortField = search.getSort() != null ? search.getSort() : "contentId";
        String sortDirection = search.getSortDirection() != null ? search.getSortDirection() : "asc";

        // 특정 필드에 대해 정렬 방향을 설정
        if ("contentId".equals(sortField)) {
            sortDirection = "asc"; // contentId는 오름차순
        } else if ("placePointValue".equals(sortField)) {
            sortDirection = "desc"; // placePointValue는 내림차순
        }

        search.setSort(sortField);
        search.setSortDirection(sortDirection);

        // 데이터 가져오기
        ListData<TourPlace> data = placeInfoService.getSearchedList(search);

        // 공통 처리 및 데이터 추가
        commonProcess("list", model);
        addListProcess(model, data);
        // 템플릿 반환
        return utils.tpl("tour/list"); // 템플릿 경로 수정 필요시 조정
    }


    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, @ModelAttribute TourPlaceSearch search, Model model) {
        try {
            // ContentType을 설정
            search.setContentType(utils.typeCode(type));

            // 정렬 기준 및 방향 처리
            String sortField = (search.getSort() != null && !search.getSort().isEmpty()) ? search.getSort() : "contentId";
            String sortDirection = (search.getSortDirection() != null && !search.getSortDirection().isEmpty()) ? search.getSortDirection() : "asc";

            // 설정된 정렬 기준과 방향을 search 객체에 설정
            search.setSort(sortField);
            search.setSortDirection(sortDirection);

            // GreenTour 타입에 대한 별도 처리
            if (search.getContentType() == ContentType.GreenTour) {
                return greenList(search, model);
            }

            // 데이터 가져오기
            ListData<TourPlace> data = placeInfoService.getSearchedList(search);

            // 공통 처리 및 데이터 추가
            commonProcess("list", model);
            addListProcess(model, data);

            // 템플릿 반환
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
           // ListData<TourPlace> data = placeInfoService.getLocBasedList(search);
          //  addListProcess(model,data);
        }catch(Exception e){
            e.printStackTrace();
            throw new TourPlaceNotFoundException();
        }
        return utils.tpl("tour/list");
    }



//    @GetMapping("/detail/{contentId}")
//    public String detail(@PathVariable("contentId") Long contentId, Model model) {
//        PlaceDetail<DetailItem> item = detailInfoService.getDetail(contentId);
//        commonProcess("detail", model);
//        model.addAttribute("items", item);
//        return utils.tpl("tour/detail");
//    }
}


