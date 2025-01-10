package com.hm.pj9.controller;

import com.hm.pj9.model.Comment;
import com.hm.pj9.model.Post;
import com.hm.pj9.model.PostImage;
import com.hm.pj9.model.Reply;
import com.hm.pj9.service.BoardService;
import com.hm.pj9.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    /*
     * 게시글 작성, 삭제
     * */
    @GetMapping("create/board")
    public String createBoard() { //게시글 작성 페이지
        return "createBoard";
    }

    @PostMapping("save/board")
    public String saveBoard(@ModelAttribute Post post, HttpSession session, @RequestParam("images") MultipartFile[] images) { //게시글 저장

        try {
            Post savedPost = boardService.saveBoard(post, (String) session.getAttribute("userId"));
            boardService.saveFiles(images, savedPost);
        } catch (IOException e) {
            return "저장 중 오류 발생 : " + e.getMessage();
        }
        return "redirect:/";
    }

    @GetMapping("post/delete/{postNum}")
    public String deletePost(@PathVariable Integer postNum) { // 게시글 삭제
        boardService.deletePost(postNum);
        return "redirect:/";
    }

    /*
     * 게시글 열람
     * */

    @GetMapping("board/{post_num}")
    public String viewBoardPage(Model model, @PathVariable Integer post_num, HttpSession session) { //게시물 로드

        Post savedPost = boardService.getPost(post_num); //게시물 가져오기
        model.addAttribute("post", savedPost);

        List<PostImage> postImage = boardService.getPostImg(savedPost); //게시물 이미지 가져오기
        model.addAttribute("img", postImage);

        List<Comment> comments = commentService.getComments(savedPost); //게시물 댓글 가져오기
        model.addAttribute("comments", comments);

        List<Reply> replies = commentService.getReplies(savedPost); //게시물 대댓글 가져오기
        model.addAttribute("replies", replies);

        model.addAttribute("userId",session.getAttribute("userId"));
        return "boardPage";
    }

    /*
     * 게시글 수정
     * */

    @GetMapping("post/edit/{postNum}")
    public String editPostPage(@PathVariable Integer postNum, Model model) {
        Post post = boardService.getPost(postNum);
        List<PostImage> images = boardService.getPostImg(post);
        model.addAttribute("post", post);
        model.addAttribute("images", images);
        return "editBoard";
    }

    @PostMapping("update/post")
    public String editPostSave(@ModelAttribute Post post, @RequestParam("images") MultipartFile[] images) { //수정 게시글 저장
        try {
            boardService.updatePost(post.getPostNum(), post.getBoardType(), post.getContent(), post.getTitle(), images);
        } catch (IOException e) {
            return "저장 중 오류 발생 : " + e.getMessage();
        }
        return "redirect:/board/" + post.getPostNum();

    }

}
