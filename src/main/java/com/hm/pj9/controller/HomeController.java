package com.hm.pj9.controller;


import com.hm.pj9.model.*;
import com.hm.pj9.service.BoardService;
import com.hm.pj9.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller // 뷰를 반환한다고 선언
public class HomeController {


    @Value("${default.web.url}")
    private String webUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) { //홈페이지
        List<Post> posts = boardService.getAllPosts();
        String sessionId = (String) session.getAttribute("userId");
        model.addAttribute("posts", posts);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("webUrl", webUrl);
        return "home";
    }

    @GetMapping("signin")
    public String signIn(Model model) { //로그인 페이지
        System.out.println("로그인으로 이동");
        model.addAttribute("webUrl", webUrl);
        return "signin";
    }


    @PostMapping("signin/check")
    public String signInCheck(@RequestParam String userId, @RequestParam String userPw, HttpSession session,
                              @RequestParam(required = false) String remember, RedirectAttributes redirectAttributes) { //로그인 누를 시 아이디 비번 확인
        int result = userService.signInCheck(userId, userPw);

        if (result == 1) {
            session.setAttribute("userId", userId);
            return "redirect:" +webUrl ;
        } else {
            return "redirect:"+webUrl+"/signin";
        }

    }

    @GetMapping("signup")
    public String signUp() { //회원가입 페이지
        return "signup";
    }

    @PostMapping("signup/check")
    public String signUpCheck(@RequestParam String userId, @RequestParam String userPw1) { //계정 생성
        System.out.println("아이디 " + userId);
        System.out.println("비밀번호 " + userPw1);
        userService.createAccount(userId, userPw1);

        return "redirect:" +webUrl;
    }

    @PostMapping("signup/duplication/check")
    @ResponseBody
    public Map<String, Boolean> duplicationCheck(@RequestBody User user) { //ID 중복 확인
        boolean result = userService.duplicationId(user.getUserId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", result);
        return response;
    }

    @GetMapping("logout")
    public String logOut(HttpSession session) { //로그아웃
        session.invalidate(); // 세션 무효화
        return "redirect:" +webUrl;
    }

    @GetMapping("/notice")
    public String notice(Model model, HttpSession session) { // 사용자 알림 가져오기
        NotificationResult notificationResult = userService.getNotification((String) session.getAttribute("userId"));
        model.addAttribute("notification", notificationResult.getNotifications());
        model.addAttribute("postCountMap", notificationResult.getPostCountMap());
        model.addAttribute("commentCountMap", notificationResult.getcommentCountMap());
        model.addAttribute("webUrl", webUrl);
        return "notification";
    }

    @PostMapping("search")
    public String searchContent(@ModelAttribute SearchData data, Model model, HttpSession session) { //검색 결과 반환

        String sessionId = (String) session.getAttribute("userId");
        model.addAttribute("sessionId", sessionId);

        if (data.getSearchType().equals("title")) { //제목만으로 검색 데이터
            model.addAttribute("posts", boardService.getPostByTitle(data.getSearchContent()));
            model.addAttribute("webUrl", webUrl);
        } else { //내용과 제목 둘중 하나로 검색 데이터
            model.addAttribute("posts", boardService.getPostByTitleOrContent(data.getSearchContent()));
            model.addAttribute("webUrl", webUrl);
        }
        return "search";
    }

    @GetMapping("search/{targetUserId}")
    public String searchId(Model model, HttpSession session, @PathVariable String targetUserId) { //검색 결과 반환
        model.addAttribute("sessionId", (String) session.getAttribute("userId"));
        model.addAttribute("posts", boardService.getPostById(targetUserId));
        model.addAttribute("webUrl", webUrl);
        return "search";

    }

    @GetMapping("my/info")
    public String myInfo(Model model, HttpSession session){
        String userId = (String)session.getAttribute("userId");
        Map<String,List<?>> userInfo = userService.getUserInfo(userId);
        model.addAttribute("posts", userInfo.get("posts"));
        model.addAttribute("comments", userInfo.get("comments"));
        model.addAttribute("replies", userInfo.get("replies"));
        model.addAttribute("webUrl", webUrl);
        return "myInfo";
    }


    @GetMapping("delete/user")
    public String deleteUser(HttpSession session){
        String userId = (String) session.getAttribute("userId");
        userService.deleteUser(userId);
        session.invalidate(); // 세션 무효화
        return "redirect:" + webUrl;
    }


}
