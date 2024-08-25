package org.g9project4.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.constants.Interest;
import org.g9project4.member.entities.Interests;
import org.g9project4.member.entities.Member;
import org.g9project4.member.services.MemberSaveService;
import org.g9project4.mypage.validators.ProfileUpdateValidator;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
import org.g9project4.publicData.tourvisit.services.TourplaceInterestsPointService;
import org.g9project4.publicData.tourvisit.services.TourplaceMRecordPointService;
import org.g9project4.publicData.tourvisit.services.TourplacePointMemberService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProfileUpdateValidator profileUpdateValidator;
    private final MemberSaveService memberSaveService;
    private final MemberUtil memberUtil;
    private final Utils utils;
    private final TourplaceMRecordPointService mRecordPointService;
    private final TourplacePointMemberService pointMemberService;
    private final TourplaceInterestsPointService interestsPointService;

    @GetMapping
    public String index(Model model) {
        commonProcess("index", model);

        return utils.tpl("mypage/index");
    }

    @GetMapping("/info")
    public String info(@ModelAttribute RequestProfile form, Model model) {
        commonProcess("info", model);

        Member member = memberUtil.getMember();
        form.setUserName(member.getUserName());
        form.setMobile(member.getMobile());
        form.setBirth(member.getBirth());
        form.setGende(member.getGende());
        form.setIsForeigner(member.getIsForeigner());
        form.setGid(member.getGid());
        form.setInterests(member.getInterests());

        return utils.tpl("mypage/info");
    }


    @PostMapping("/info")
    public String updateInfo(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("info", model);

        profileUpdateValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/info");
        }

        memberSaveService.save(form);


        //SecurityContextHolder.getContext().setAuthentication();

        return "redirect:" + utils.redirectUrl("/mypage");
    }


    @GetMapping("/mypost")
    public String mypost(Model model) {
        commonProcess("mypost", model);

        return utils.tpl("mypage/mypost");
    }




    @GetMapping("/myplace")
    public String myplaceList(@ModelAttribute TourPlaceSearch search, @RequestParam(value = "currentDate", required = false) LocalDate currentDate, Model model) {

        if (currentDate == null) {
            currentDate = LocalDate.now();
        }

        try {
            ListData<TourPlace> data = pointMemberService.getTopTourPlacesByMember(search, currentDate);
            System.out.println("Data from pointMemberService: " + data); // 로그 추가
            System.out.println("TourPlaceSearch: " + search); // 로그 추가
            commonProcess("mypost", model);
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("addCss", List.of("mypage/myplace"));
            model.addAttribute("data", data);
            addListProcess(model, data);
            model.addAttribute("tourPlaceSearch", search);

            return utils.tpl("mypage/myplace");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "잘못된 요청이 발생했습니다.");
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "여행지 목록을 가져오는 데 오류가 발생했습니다.");
            return "error";
        }
    }

    @GetMapping("/visitplace") // 검색기록 + 검색키워드 기준 추천
    public String RecordList(@ModelAttribute TourPlaceSearch search, @RequestParam(value = "currentDate", required = false) LocalDate currentDate, Model model) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }

        // 서비스 메서드를 호출하여 추천 여행지 목록을 가져옵니다.
        ListData<TourPlace> data = mRecordPointService.getTopTourPlacesByRecord(search, currentDate);
        System.out.println("Data from mRecordService: " + data); // 로그 추가
        System.out.println("TourPlaceSearch: " + search); // 로그 추가

        // 공통 처리 (commonProcess 메서드가 어떤 기능인지에 따라 다름)
        commonProcess("mypost", model);

        // 추가 스타일을 모델에 추가
        model.addAttribute("addCss", List.of("mypage/myplace"));
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("data", data);
        // 목록 데이터 처리 (addListProcess 메서드가 어떤 기능인지에 따라 다름)
        addListProcess(model, data);
        model.addAttribute("tourPlaceSearch", search);

        // tourPlaceSearch를 모델에 추가


        // 템플릿을 반환
        return utils.tpl("mypage/visitplace");
    }

    @GetMapping("/myinterests") // 관심사 기준 추천
    public String interestsList( TourPlaceSearch search, Model model) {
        Member member = memberUtil.getMember();
        String userName = member.getUserName();

        // 서비스 메서드를 호출하여 추천 여행지 목록을 가져옵니다.
        ListData<TourPlace> data = interestsPointService.getTopTourPlacesByInterests(member.getSeq(), search);
        System.out.println("Data from interestsService: " + data); // 로그 추가
        System.out.println("TourPlaceSearch: " + search); // 로그 추가
        // 공통 처리 (commonProcess 메서드가 어떤 기능인지에 따라 다름)
        commonProcess("mypost", model);

        // 추가 스타일을 모델에 추가
        model.addAttribute("addCss", List.of("mypage/myplace"));
        model.addAttribute("tourPlaceSearch", search);
        // 목록 데이터 처리 (addListProcess 메서드가 어떤 기능인지에 따라 다름)
        model.addAttribute("data", data);
        addListProcess(model, data);

        // 템플릿을 반환
        return utils.tpl("mypage/myinterests");
    }

    //pagination
    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    private void commonProcess(String mode, Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("mypage/style");
        if (mode.equals("info")) {
            addCommonScript.add("fileManager");
            addCss.add("mypage/info");
            addScript.add("member/form");
        } else if (mode.equals("mypost")) {
            addCss.add("mypage/mypost");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
    }


}
