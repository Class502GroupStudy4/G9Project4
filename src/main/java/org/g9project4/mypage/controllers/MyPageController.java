package org.g9project4.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.g9project4.global.Utils;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.services.MemberSaveService;
import org.g9project4.mypage.validators.ProfileUpdateValidator;
import org.g9project4.search.entities.SearchHistory;
import org.g9project4.search.services.SearchHistoryService;
import org.g9project4.wishlist.entities.WishList;
import org.g9project4.wishlist.services.WishListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.g9project4.search.entities.QSearchHistory.searchHistory;
import static org.g9project4.wishlist.entities.QWishList.wishList;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProfileUpdateValidator profileUpdateValidator;
    private final MemberSaveService memberSaveService;
    private final MemberUtil memberUtil;
    private final Utils utils;
    private final SearchHistoryService searchHistoryService;
    private final WishListService wishListService;

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

        return utils.tpl("mypage/info");
    }


    @PostMapping("/info")
    public String updateInfo(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("info", model);

        profileUpdateValidator.validate(form,errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/info");
        }

        memberSaveService.save(form);



        //SecurityContextHolder.getContext().setAuthentication();

        return "redirect:"+ utils.redirectUrl("/mypage");
    }


    @GetMapping("/mypost")
    public String mypost(Model model) {
        commonProcess("mypost", model);

        return utils.tpl("mypage/mypost");
    }

    @GetMapping("/mycomment")
    public String mycomment(Model model) {
        commonProcess("mycomment", model);

        return utils.tpl("mypage/mycomment");
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
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
    }
}
