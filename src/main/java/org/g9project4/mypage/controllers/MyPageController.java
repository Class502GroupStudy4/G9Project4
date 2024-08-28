package org.g9project4.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.board.entities.Board;
import org.g9project4.board.services.BoardInfoService;
import org.g9project4.global.ListData;
import org.g9project4.global.Pagination;
import org.g9project4.global.Utils;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.constants.Interest;
import org.g9project4.member.entities.Member;
import org.g9project4.member.repositories.InterestsRepository;
import org.g9project4.member.services.MemberSaveService;
import org.g9project4.mypage.validators.ProfileUpdateValidator;
import org.g9project4.publicData.myvisit.TourplaceDto;
import org.g9project4.publicData.myvisit.services.TourplaceInterestsPointService;
import org.g9project4.publicData.myvisit.services.TourplacePointMemberService;
import org.g9project4.publicData.tour.controllers.TourPlaceSearch;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.services.TourPlaceInfoService;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.services.SearchHistoryService;
import org.g9project4.visitrecord.constants.RecommendType;
//km import org.g9project4.visitrecord.services.VisitRecordService;
import org.g9project4.wishlist.entities.WishList;
import org.g9project4.wishlist.services.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.g9project4.member.entities.Interests;


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
    private final SearchHistoryService searchHistoryService;
  //km   private final VisitRecordService visitRecordService;
    private final TourplacePointMemberService pointMemberService;
    private final TourplaceInterestsPointService interestsPointService;
    private final InterestsRepository interestsRepository;
    private final WishListService wishListService;
    private final BoardInfoService boardInfoService;
private final TourPlaceInfoService tourInfoService;
    @GetMapping
    public String index(@ModelAttribute RequestProfile form, Model model) {
        commonProcess("index", model);

        Member member = memberUtil.getMember();
        form.setUserName(member.getUserName());
        form.setMobile(member.getMobile());
        form.setBirth(member.getBirth());
        form.setGende(member.getGende());
        form.setIsForeigner(member.getIsForeigner());
        form.setGid(member.getGid());

        //관심사 노출
        List<Interests> interestsEntities = interestsRepository.findByMember(member);
        List<Interest> interests = interestsEntities.stream()
                .map(Interests::getInterest)
                .collect(Collectors.toList());
        form.setInterests(interests);

        List<SearchHistory> searchHistory = searchHistoryService.getSearchHistoryForMember(memberUtil.getMember());

        List<WishList> wishList = wishListService.getWishListForMember(memberUtil.getMember());

        model.addAttribute("searchHistory", searchHistory);
        model.addAttribute("wishList", wishList);

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

        //관심사 노출
        List<Interests> interestsEntities = interestsRepository.findByMember(member);
        List<Interest> interests = interestsEntities.stream()
                .map(Interests::getInterest)
                .collect(Collectors.toList());
        form.setInterests(interests);

        return utils.tpl("mypage/info");
    }


    @PostMapping("/info")
    public String updateInfo(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("info", model);

        Member member = memberUtil.getMember();

        profileUpdateValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/info");
        }

        memberSaveService.save(form);

        //복수 관심사 저장
        List<Interest> interests = form.getInterests();
        memberSaveService.saveInterests(member, interests);


        //SecurityContextHolder.getContext().setAuthentication();

        return "redirect:" + utils.redirectUrl("/mypage");
    }


    @GetMapping("/mypost")
    public String mypost(Model model, Member member) {
        commonProcess("mypost", model);

       // List<Board> boards = boardInfoService.getBoardsByMember(member);
       // model.addAttribute("boards", boards);

        return utils.tpl("mypage/mypost");
    }

    @GetMapping("/mycomment")
    public String mycomment(Model model) {
        commonProcess("mycomment", model);

        return utils.tpl("mypage/mycomment");
    }


    //추천
    @GetMapping("/myplace")
    public String myplaceList(@ModelAttribute TourPlaceSearch search, Model model) {

        if (!memberUtil.isLogin()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        try {
            ListData<TourplaceDto> listData = pointMemberService.getTopTourPlacesByMember(search);
            model.addAttribute("data", listData);
            commonProcess("myplace", model);

           // model.addAttribute("pagination", listData.getPagination());

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

//    @GetMapping("/visitplace") // 검색기록 + 검색키워드 기준 추천
//    public String RecordList(@ModelAttribute TourPlaceSearch search, @RequestParam RecommendType recommendType, Model model) {
//
//        // 서비스 메서드를 호출하여 추천 여행지 목록을 가져옵니다.
//        ListData<TourPlace> data = tourInfoService.getTotalList(search,  recommendType);
//        System.out.println("Data from mRecordService: " + data); // 로그 추가
//        System.out.println("TourPlaceSearch: " + search); // 로그 추가
//
//        // 공통 처리 (commonProcess 메서드가 어떤 기능인지에 따라 다름)
//        commonProcess("visitplace", model);
//
//        model.addAttribute("recommendType", recommendType);
//        model.addAttribute("data", data);
//        // 목록 데이터 처리 (addListProcess 메서드가 어떤 기능인지에 따라 다름)
//      //  addListProcess(model, data);
//        model.addAttribute("tourPlaceSearch", search);
//
//        // tourPlaceSearch를 모델에 추가
//
//        // 템플릿을 반환
//        return utils.tpl("mypage/visitplace");
//    }


    @GetMapping("/myinterests") // 관심사 기준 추천
    public String interestsList(@ModelAttribute TourPlaceSearch search, Model model) {

        if (!memberUtil.isLogin()) {
            throw new IllegalStateException("로그인이 필요합니다.");

        }

        try {
            ListData<TourplaceDto> listData = interestsPointService.getTopTourPlacesByInterests(search);

            // 목록 데이터 처리 (addListProcess 메서드가 어떤 기능인지에 따라 다름)
            model.addAttribute("data", listData);
            commonProcess("myinterests", model);


            System.out.println("listData:" + listData);
            List<TourplaceDto> items = listData.getItems();
            Pagination pagination = listData.getPagination();
            System.out.println("Items: " + items);
            System.out.println("Pagination: " + pagination);

            // 템플릿을 반환
            return utils.tpl("mypage/myinterests");
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


    private void commonProcess(String mode, Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("mypage/style");
        addScript.add("member/_side");
        if (mode.equals("info")) {
            addCommonScript.add("fileManager");
            addCss.add("mypage/info");
            addScript.add("member/form");
        } else if (mode.equals("mypost")) {
            addCss.add("mypage/mypost");

        } else if (mode.equals("index")) {
            addCss.add("mypage/index");

        } else if (mode.equals("mycomment")) {
            addCss.add("mypage/mycomment");

        } else if (mode.equals("myplace")) {
            addCss.addAll(List.of("mypage/myplace"));

        } else if (mode.equals("visitplace")) {
            addCss.addAll(List.of("mypage/myplace"));

        } else if (mode.equals("myinterests")) {
            addCss.addAll(List.of("mypage/myplace"));
        }


        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
    }
}
