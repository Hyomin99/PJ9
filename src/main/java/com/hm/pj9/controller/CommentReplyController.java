package com.hm.pj9.controller;


import com.hm.pj9.model.Comment;
import com.hm.pj9.model.Reply;
import com.hm.pj9.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommentReplyController {

    @Autowired
    BoardService boardService;

    /*
     * 댓글 작성, 삭제
     * */
    @PostMapping("add/comment")
    public String addComment(@ModelAttribute Comment comment, HttpSession session, @RequestParam("postNum") Integer postNum) { //댓글 저장
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }
        boardService.saveComment(comment, userId, postNum);
        return "redirect:/board/" + postNum;
    }

    @GetMapping("comment/delete")
    public String deleteComment(@RequestParam Integer commentNum, @RequestParam Integer postNum) {
        boardService.deleteComment(commentNum);
        return "redirect:/board/" + postNum;
    }


    /*
     * 대댓글 작성, 삭제
     * */
    @PostMapping("add/reply")
    public String addReply(@ModelAttribute Reply reply, HttpSession session, @RequestParam("postNum") Integer postNum, @RequestParam("commentNum") Integer commentNum) { //댓글 저장
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/signin";
        }
        boardService.saveReply(reply, userId, postNum, commentNum);
        return "redirect:/board/" + postNum;

    }

    @GetMapping("reply/delete")
    public String deleteReply(@RequestParam Integer replyNum, @RequestParam Integer postNum) {
        boardService.deleteReply(replyNum);
        return "redirect:/board/" + postNum;
    }


}
