package org.g9project4.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;



@Component
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //모든 컨틀로러 공통 처리..
        checkDevice(request);

        return true;
    }
    /**
     * pc와 모바일 수동 변환, 모바일 아니면 전부 pc 화면
     * @param request
     */
    private void checkDevice(HttpServletRequest request){
        String device = request.getParameter("device");
        if(!StringUtils.hasText(device)){
            return;
        }

        device= device.toUpperCase().equals("MOBILE") ? "MOBILE" : "PC";
        HttpSession session = request.getSession();
        session.setAttribute("device", device);
    }
}

