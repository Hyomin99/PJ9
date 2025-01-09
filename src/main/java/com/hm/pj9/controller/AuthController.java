package com.hm.pj9.controller;

import com.hm.pj9.model.User;
import com.hm.pj9.service.AuthService;
import com.hm.pj9.service.UserDeletionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController { // 사용자 관련 컨트롤러

    @Autowired
    private UserDeletionService userDeletionService;
    @Autowired
    private AuthService authService;

    /*
     * 로그인
     * */
    @GetMapping("signin")
    public String signIn() { //로그인 페이지
        return "signin";
    }

    @PostMapping("signin/check")
    public String signInCheck(@RequestParam String userId, @RequestParam String userPw, HttpSession session) { //로그인 누를 시 아이디 비번 확인
        int result = authService.signInCheck(userId, userPw);
        if (result == 1) {
            session.setAttribute("userId", userId);
            return "redirect:/";
        } else {
            return "redirect:/signin";
        }
    }

    /*
     * 회원가입
     * */
    @GetMapping("signup")
    public String signUp() { //회원가입 페이지
        return "signup";
    }

    @PostMapping("signup/check")
    public String signUpCheck(@RequestParam String userId, @RequestParam String userPw1, HttpSession session) { //계정 생성
        authService.createAccount(userId, userPw1);
        session.setAttribute("userId", userId);
        return "redirect:/";
    }

    @PostMapping("signup/duplication/check")
    @ResponseBody
    public Map<String, Boolean> duplicationCheck(@RequestBody User user) { //ID 중복 확인
        boolean result = authService.duplicationId(user.getUserId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", result);
        return response;
    }


    /*
     * 로그아웃
     * */
    @GetMapping("logout")
    public String logOut(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/";
    }


    /*
     * 회원 탈퇴
     * */
    @GetMapping("delete/user")
    public String deleteUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        userDeletionService.deleteUser(userId);
        session.invalidate();
        return "redirect:/";
    }

    /*
     * 내 정보
     * */
    @GetMapping("my/info")
    public String myInfo(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        Map<String, List<?>> userInfo = authService.getUserInfo(userId);
        model.addAttribute("posts", userInfo.get("posts"));
        model.addAttribute("comments", userInfo.get("comments"));
        model.addAttribute("replies", userInfo.get("replies"));
        return "myInfo";
    }


}
