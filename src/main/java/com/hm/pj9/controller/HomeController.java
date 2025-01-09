package com.hm.pj9.controller;


import com.hm.pj9.model.*;
import com.hm.pj9.service.HomeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    /*
     * 홈 페이지 로드
     * */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Post> posts = homeService.getAllPostsSortedByDate();
        String sessionId = (String) session.getAttribute("userId");
        model.addAttribute("posts", posts);
        model.addAttribute("sessionId", sessionId);
        return "home";
    }


    /*
     * 홈 검색 부분
     * */

    @PostMapping("search")
    public String searchContent(@ModelAttribute SearchData data, Model model, HttpSession session) { //내용 검색 결과 반환

        String sessionId = (String) session.getAttribute("userId");
        model.addAttribute("sessionId", sessionId);

        if (data.getSearchType().equals("title")) { //제목만으로 검색 데이터
            model.addAttribute("posts", homeService.getPostByTitle(data.getSearchContent()));
        } else { //내용과 제목 둘중 하나로 검색 데이터
            model.addAttribute("posts", homeService.getPostByTitleOrContent(data.getSearchContent()));
        }
        return "search";

    }

    @GetMapping("search/{targetUserId}")
    public String searchId(Model model, HttpSession session, @PathVariable String targetUserId) { //아이디로 검색 결과 반환
        model.addAttribute("sessionId", session.getAttribute("userId"));
        model.addAttribute("posts", homeService.getPostById(targetUserId));
        return "search";

    }

}
