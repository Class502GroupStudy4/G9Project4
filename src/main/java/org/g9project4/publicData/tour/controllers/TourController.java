package org.g9project4.publicData.tour.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.config.controllers.ApiConfig;
import org.g9project4.config.service.ConfigInfoService;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.global.exceptions.ExceptionProcessor;
import org.g9project4.global.exceptions.script.AlertException;
import org.g9project4.global.rests.gov.detailapi.DetailItem;
import org.g9project4.global.rests.gov.detailpetapi.DetailPetItem;
import org.g9project4.member.MemberUtil;
import org.g9project4.publicData.tour.entities.AreaCode;
import org.g9project4.publicData.tour.entities.PlaceDetail;
import org.g9project4.publicData.tour.entities.TourCommentData;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.repositories.AreaCodeRepository;
import org.g9project4.publicData.tour.repositories.CategoryRepository;
import org.g9project4.publicData.tour.repositories.SigunguCodeRepository;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.g9project4.publicData.tour.services.*;
import org.g9project4.publicData.tour.validators.TourCommentValidator;
import org.g9project4.search.services.SearchHistoryService;
import org.g9project4.visitrecord.services.VisitRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
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
    private final SearchHistoryService searchHistoryService;
    private final VisitRecordService recordService;
    private final SearchHistoryService historyService;
    private final TourCommentValidator validator;
    private final TourCommentSaveService tourCommentSaveService;
    private final MemberUtil memberUtil;
    private final TourCommentInfoService tourCommentInfoService;

    @ModelAttribute("apiKeys")
    public ApiConfig getApiKeys() {
        return configInfoService.get("apiConfig", ApiConfig.class).orElseGet(ApiConfig::new);
    }

    @ModelAttribute("areaCodes")
    public List<AreaCode> getAreaCodes() {
        return areaCodeRepository.findAll();
    }

    @ModelAttribute("category1")
    public List<Object[]> getCategory1() {
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

        addCommonScript.add("wish");

        if (mode.equals("list")) {
            addCss.addAll(List.of("tour/list", "tour/_typelist", "tour/banner", "tour/search"));
            addScript.addAll(List.of("tour/locBased", "tour/form", "tour/search"));
        } else if (mode.equals("detail")) {
            addCss.addAll(List.of("tour/map", "tour/detail"));
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
            if (search.getSkey() != null) {
                searchHistoryService.saveTour(search.getSkey());
            }
            commonProcess("list", model);
            addListProcess(model, data);

            //km 키워드 검색 저장
            if (StringUtils.hasText(search.getSkey())) {
                historyService.saveTour(search.getSkey());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return utils.tpl("tour/list");
    }

    @GetMapping("/detail/{contentId}")
    public String detail(@PathVariable("contentId") Long contentId, Model model) {

        ApiConfig apiConfig = configInfoService.get("apiConfig", ApiConfig.class).orElseGet(ApiConfig::new);
        PlaceDetail<DetailItem, DetailPetItem> item = detailInfoService.getDetail(contentId, apiConfig.getPublicOpenApiKey());
        commonProcess("detail", model);
        model.addAttribute("items", item);

        //km 추천 방문 데이터 저장
        recordService.record(contentId);
        List<TourCommentData> data = tourCommentInfoService.getList(contentId);
        model.addAttribute("commentData", data);
        RequestTourComment requestComment = new RequestTourComment();
        if (memberUtil.isLogin()) {
            requestComment.setCommenter(memberUtil.getMember().getUserName());
        }
        model.addAttribute("requestTourComment", requestComment);
        return utils.tpl("tour/detail");
    }

    /**
     * 여행지 댓글 저장, 수정 처리
     *
     * @return
     */
    @PostMapping("/comment")
    public String comment(@Valid RequestTourComment form, Errors errors, Model model) {
        validator.validate(form, errors);

        if (errors.hasErrors()) {
            FieldError error = errors.getFieldErrors().stream().findFirst().orElse(null);
            throw new AlertException(utils.getMessage(error == null || error.getCodes() == null ? "BadRequest" : error.getCodes()[0]), HttpStatus.BAD_REQUEST);
        }
        TourCommentData tourCommentData = tourCommentSaveService.save(form);

        String script = String.format("parent.location.replace('/tour/detail/%d?comment_id=%d');", tourCommentData.getTourPlace().getContentId(), tourCommentData.getSeq());

        model.addAttribute("script", script);
        return "common/_execute_script";
    }
}