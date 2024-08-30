package org.g9project4.publicData.tour.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.g9project4.board.exceptions.CommentNotFoundException;
import org.g9project4.global.ListData;
import org.g9project4.member.MemberUtil;
import org.g9project4.member.entities.Member;
import org.g9project4.publicData.tour.entities.QTourCommentData;
import org.g9project4.publicData.tour.entities.TourCommentData;
import org.g9project4.publicData.tour.repositories.TourCommentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

@Service
@RequiredArgsConstructor
public class TourCommentInfoService {

    private final TourCommentRepository tourCommentRepository;
    private final MemberUtil memberUtil;
    private final HttpServletRequest request;
    /**
     * 댓글 단일 조회
     *
     * @param seq : 댓글 번호
     * @return
     */
    public TourCommentData get(Long seq) {
        TourCommentData data = tourCommentRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        return data;
    }

    public List<TourCommentData> getList(Long contentId) {
        QTourCommentData tourCommentData = QTourCommentData.tourCommentData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(tourCommentData.tourPlace.contentId.eq(contentId));

        List<TourCommentData> items = (List<TourCommentData>) tourCommentRepository.findAll(andBuilder, Sort.by(asc("createdAt")));

        items.forEach(this::addCommentInfo);

        return items;
    }
/*    public ListData<TourCommentData> getList(Long contentId){

    }*/

    private void addCommentInfo(TourCommentData data) {
        boolean editable = false, mine = false;

        Member _member = data.getMember();

        /*
         * 1) 관리자는 댓글 수정, 삭제 제한 없음
         *
         */
        if (memberUtil.isAdmin()) {
            editable = true;
        }

        /**
         * 회원이 작성한 댓글이면 현재 로그인 사용자의 아이디와 동일해야 수정, 삭제 가능
         *
         */
        if (_member != null && memberUtil.isLogin()
                && _member.getEmail().equals(memberUtil.getMember().getEmail())) {
            editable =  mine = true;
        }

        // 비회원 -> 비회원 비밀번호가 확인 된 경우 삭제, 수정 가능
        // 비회원 비밀번호 인증 여부 세션에 있는 guest_confirmed_게시글번호 true -> 인증
        HttpSession session = request.getSession();
        String key = "confirm_comment_data_" + data.getSeq();
        Boolean guestConfirmed = (Boolean)session.getAttribute(key);
        if (_member == null && guestConfirmed != null && guestConfirmed) {
            editable = true;
            mine = true;
        }

        // 수정 버튼 노출 여부
        // 관리자 - 노출, 회원 게시글 - 직접 작성한 게시글, 비회원
        boolean showEdit = memberUtil.isAdmin() || mine || _member == null;

        data.setShowEdit(showEdit);

        data.setEditable(editable);
        data.setMine(mine);
    }
}
