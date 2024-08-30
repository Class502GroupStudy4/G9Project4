package org.g9project4.publicData.tour.validators;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.validators.PasswordValidator;
import org.g9project4.member.MemberUtil;
import org.g9project4.publicData.tour.controllers.RequestTourComment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class TourCommentValidator implements Validator, PasswordValidator {
    private final MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestTourComment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (memberUtil.isLogin()) {// 로그인 상태 일때는 비회원 비밀번호 체크 X
            return;
        }
        RequestTourComment form = (RequestTourComment) target;

        String guestPw = form.getGuestPw();
        if (!StringUtils.hasText(guestPw)) {
            errors.rejectValue("guestPw", "NotBlank");
        }

        if (StringUtils.hasText(guestPw)) {
            if (guestPw.length() < 6) {
                errors.rejectValue("guestPw", "Size");
            }

            if (!alphaCheck(guestPw, true) || !numberCheck(guestPw)) {
                errors.rejectValue("guestPw", "Complexity");
            }
        } // endif
    }
}
