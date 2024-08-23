package org.g9project4.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.services.MemberSaveService;
import org.g9project4.mypage.validators.ProfileUpdateValidator;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
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

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProfileUpdateValidator profileUpdateValidator;
    private final MemberSaveService memberSaveService;
    private final MemberUtil memberUtil;
    private final Utils utils;
    private final TourPlaceInfoService placeInfoService;
    private final TourplaceMRecordPointService mRecordPointService;
    private final TourplacePointMemberService pointMemberService;

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


    //pagination
    private void addListProcess(Model model, ListData<TourPlace> data) {
        Pagination pagination = data.getPagination();

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", pagination);
    }

    //회원정보 기반 추천
    @GetMapping("/myplace")
    public String list(@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(value = "page", defaultValue = "1") int pageNumber,
                       @RequestParam(value = "size", defaultValue = "10") int pageSize,
                       Model model) {
        // 기본값으로 현재 날짜를 사용
        if (date == null) {
            date = LocalDate.now();
        }

        // 페이지 번호와 페이지 크기 유효성 검증
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }

        try {
            // 서비스 메서드를 호출하여 추천 여행지 목록을 가져옵니다.
            ListData<TourPlace> data = pointMemberService.getTopTourPlacesByMemberSeq(date, pageNumber, pageSize);

            // 공통 프로세스 및 CSS 추가
            commonProcess("mypost", model);
            model.addAttribute("addCss", List.of("mypage/myplace"));

            // 모델에 데이터 추가
            model.addAttribute("data", data);
            model.addAttribute("date", date);

            // 추가적인 데이터 처리
            addListProcess(model, data);

            // 템플릿 반환
            return utils.tpl("mypage/myplace");

        } catch (Exception e) {
            // 예외 처리 (로그 기록 및 사용자에게 에러 메시지 전달 등)
            e.printStackTrace();
            model.addAttribute("errorMessage", "여행지 목록을 가져오는 데 오류가 발생했습니다.");
            return "error"; // 오류 페이지로 리다이렉션
        }
    }

    @GetMapping("/visitplace")
    public String Recordlist(Model model,
                             @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 날짜가 없으면 현재 날짜로 기본값 설정
        if (date == null) {
            date = LocalDate.now();
        }

        // 서비스 메서드를 호출하여 추천 여행지 목록을 가져옵니다.
        ListData<TourPlace> data = mRecordPointService.getTopTourPlacesByMemberSeq(date, pageNumber, pageSize);

        // 공통 처리 (commonProcess 메서드가 어떤 기능인지에 따라 다름)
        commonProcess("mypost", model);

        // 추가 스타일을 모델에 추가
        model.addAttribute("addCss", List.of("mypage/myplace"));

        // 목록 데이터 처리 (addListProcess 메서드가 어떤 기능인지에 따라 다름)
        addListProcess(model, data);

        // 날짜와 페이지 정보도 모델에 추가
        model.addAttribute("date", date);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);

        // 템플릿을 반환
        return utils.tpl("mypage/visitplace");
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
