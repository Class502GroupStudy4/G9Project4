package org.g9project4.publicData.tour.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.g9project4.board.exceptions.CommentNotFoundException;
import org.g9project4.member.MemberUtil;
import org.g9project4.publicData.tour.controllers.RequestTourComment;
import org.g9project4.publicData.tour.entities.TourCommentData;
import org.g9project4.publicData.tour.entities.TourPlace;
import org.g9project4.publicData.tour.exceptions.TourPlaceNotFoundException;
import org.g9project4.publicData.tour.repositories.TourCommentRepository;
import org.g9project4.publicData.tour.repositories.TourPlaceRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TourCommentSaveService {
    private final TourCommentRepository tourCommentRepository;
    private final TourPlaceRepository tourPlaceRepository;

    private final HttpServletRequest request;
    private final MemberUtil memberUtil;
    private final PasswordEncoder encoder;

    public TourCommentData save(RequestTourComment form) {
        String mode = form.getMode();
        Long seq = form.getSeq();//댓글 번호
        mode = StringUtils.hasText(mode) ? mode : "add";

        TourCommentData data = null;

        if (mode.equals("edit") && seq != null) {//댓글 수정일 경우
            //댓글 번호로 찾아온다
            data = tourCommentRepository.findById(seq).orElseThrow(CommentNotFoundException::new);
        } else {//댓글 작성일 경우
            //여행지 id 로 여행지를 찾아오고 정보를 넣는다
            TourPlace tourPlace = tourPlaceRepository.findById(form.getContentId()).orElseThrow(TourPlaceNotFoundException::new);
            data = TourCommentData.builder()
                    .tourPlace(tourPlace)
                    .member(memberUtil.getMember())
                    .ip(request.getRemoteAddr())
                    .ua(request.getHeader("User-Agent"))
                    .build();
        }
        String guestPw = form.getGuestPw();
        if(StringUtils.hasText(guestPw)){
            data.setGuestPw(encoder.encode(guestPw));
        }

        String commenter = form.getCommenter();
        if(StringUtils.hasText(commenter)){
            data.setCommenter(commenter);
        }
        data.setContent(form.getContent());

        tourCommentRepository.saveAndFlush(data);

        return data;
    }
}
