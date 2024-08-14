package org.g9project4.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.digester.ArrayStack;
import org.g9project4.global.Utils;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.member.services.MemberSaveService;
import org.g9project4.mypage.validators.ProfileUpdateValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @GetMapping
    public String index(@ModelAttribute RequestProfile form, Model model) {
        commonProcess("index", model);

        return utils.tpl("mypage/index");
    }

    @GetMapping("/info")
    public String info(@ModelAttribute RequestProfile form, Model model) {
        commonProcess("info", model);

        Member member = memberUtil.getMember();
        form.setUserName(member.getUserName());
        form.setMobile(member.getMobile());
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

    private void commonProcess(String mode, Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("mypage/style");
        if (mode.equals("info")) {
            addCommonScript.add("fileManager");
            addCss.add("mypage/info");
            addScript.add("member/form");
            addScript.add("member/_side");
        } else if (mode.equals("mypost")) {
            addCss.add("mypage/mypost");

        } else if (mode.equals("index")) {
            addCss.add("mypage/index");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
    }
}
