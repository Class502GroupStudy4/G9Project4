package org.g9project4.mypage.validators;

import lombok.RequiredArgsConstructor;
import org.g9project4.global.exceptions.script.AlertException;
import org.g9project4.global.validators.PasswordValidator;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.controllers.RequestJoin;
import org.g9project4.member.repositories.MemberRepository;
import org.g9project4.mypage.controllers.RequestProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class MyPageUpdateValidator implements Validator, PasswordValidator {

    private final RequestProfile requestProfile;
    private final MemberUtil memberUtil;
    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestProfile.class);

    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        RequestProfile requestProfile = (RequestProfile) target;
        String password = requestProfile.getPassword();
        String confirmPassword = requestProfile.getConfirmPassword();

        if (password != null && !password.isBlank()) {
            errors.rejectValue("password", "field.required", "비밀번호를 확인하세요.");

            /* 비밀번호 복잡성 체크 */
            if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharsCheck(password)) {
                errors.rejectValue("password", "Complexity");
            }

            /* 비밀번호 및 비밀번호 확인 일치 여부 */
            if (!password.equals(confirmPassword)) {
                errors.rejectValue("confirmPassword", "Mismatch.password");
            }
        }


    }
}
