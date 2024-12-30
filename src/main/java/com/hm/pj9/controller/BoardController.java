package com.hm.pj9.controller;

import com.hm.pj9.model.Comment;
import com.hm.pj9.model.Post;
import com.hm.pj9.model.PostImage;
import com.hm.pj9.model.Reply;
import com.hm.pj9.service.BoardService;
import com.hm.pj9.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class BoardController {

    @Value("${default.web.url}")
    private String webUrl;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;


    @GetMapping("create/board")
    public String createBoard(HttpSession session) {
        String targetUrl =  "createBoard";
        return checkSession(session,targetUrl);
    }

    @PostMapping("save/board")
    public String saveBoard(@ModelAttribute Post post, HttpSession session,
                            @RequestParam("images") MultipartFile[] images) throws IOException { //게시글 저장

        try {
            Post savedPost = boardService.saveBoard(post, (String) session.getAttribute("userId"));
            boardService.saveFiles(images, savedPost);
        } catch (IOException e) {
            return "저장 중 오류 발생 : " + e.getMessage();
        }


        String targetUrl = "redirect:/";
        return checkSession(session,targetUrl);

    }

    @GetMapping("board/{post_num}")
    public String viewBoardPage(Model model, @PathVariable Integer post_num, HttpSession session) { //게시물 로드

        Post savedPost = boardService.getPost(post_num); //게시물 가져오기
        model.addAttribute("post", savedPost);

        List<PostImage> postImage = boardService.getPostImg(savedPost); //게시물 이미지 가져오기
        model.addAttribute("img", postImage);

        List<Comment> comments = boardService.getComments(savedPost); //게시물 댓글 가져오기
        model.addAttribute("comments", comments);

        List<Reply> replies = boardService.getReplies(savedPost); //게시물 대댓글 가져오기
        model.addAttribute("replies", replies);

        model.addAttribute("userId", (String) session.getAttribute("userId"));
        model.addAttribute("webUrl", webUrl);
        String targetUrl = "boardPage";
        return checkSession(session,targetUrl);
    }

    @GetMapping("check/notification/{post_num}")
    public String checkNotification(Model model, @PathVariable Integer post_num, HttpSession session, @RequestParam String type) { //알림 확인
        userService.readNotification(post_num, (String) session.getAttribute("userId"), type);
        String targetUrl = "redirect:/board/" + post_num;
        return checkSession(session,targetUrl);
    }


    @PostMapping("add/comment")
    public String addComment(@ModelAttribute Comment comment, HttpSession session, @RequestParam("postNum") Integer postNum) throws IOException { //댓글 저장
        boardService.saveComment(comment, (String) session.getAttribute("userId"), postNum);
        String targetUrl = "redirect:/board/" + postNum;
        return checkSession(session,targetUrl);
    }

    @GetMapping("comment/delete")
    public String deleteComment(@RequestParam Integer commentNum, @RequestParam Integer postNum, HttpSession session) {
        System.out.println("여기까지는 오는건가");
        boardService.deleteComment(commentNum);
        String targetUrl = "redirect:/board/" + postNum;
        return checkSession(session,targetUrl);
    }

    @GetMapping("reply/delete")
    public String deleteReply(@RequestParam Integer replyNum, @RequestParam Integer postNum, HttpSession session) {
        System.out.println("여기까지는 오는건가");
        boardService.deleteReply(replyNum);
        String targetUrl = "redirect:/board/" + postNum;
        return checkSession(session,targetUrl);
    }

    @PostMapping("add/reply")
    public String addReply(@ModelAttribute Reply reply, HttpSession session, @RequestParam("postNum") Integer postNum, @RequestParam("commentNum") Integer commentNum) { //댓글 저장
        boardService.saveReply(reply, (String) session.getAttribute("userId"), postNum, commentNum);
        String targetUrl = "redirect:/board/" + postNum;
        return checkSession(session,targetUrl);
    }

    @GetMapping("post/delete/{postNum}")
    public String deletePost(@PathVariable Integer postNum, HttpSession session) {
        boardService.deletePost(postNum);
        String targetUrl = "redirect:/";
        return checkSession(session,targetUrl);
    }

    @GetMapping("post/edit/{postNum}")
    public String editPost1(@PathVariable Integer postNum, Model model, HttpSession session) {
        Post post = boardService.getPost(postNum);
        List<PostImage> images = boardService.getPostImg(post);
        model.addAttribute("post", post);
        model.addAttribute("images", images);
        model.addAttribute("webUrl", webUrl);
        String targetUrl = "editBoard";
        return checkSession(session,targetUrl);
    }


    @PostMapping("update/post")
    public String editPost2(@ModelAttribute Post post, @RequestParam("images") MultipartFile[] images, HttpSession session) throws IOException { //게시글 저장


        try {
            boardService.updatePost(post.getPostNum(), post.getBoardType(), post.getContent(), post.getTitle(), images);
        } catch (IOException e) {
            return "저장 중 오류가 발생 : " + e.getMessage();
        }

        String targetUrl = "redirect:/board/" + post.getPostNum();
        return checkSession(session,targetUrl);

    }

    public String checkSession(HttpSession session, String targetUrl) {
        // 세션에서 userId를 확인
        Object userId = session.getAttribute("userId");

        // userId가 없으면 /login 페이지로 리디렉션
        if (userId == null) {
            return "redirect:/signin";
        }

        // userId가 있으면 해당 페이지로 진행
        return targetUrl;
    }

}
