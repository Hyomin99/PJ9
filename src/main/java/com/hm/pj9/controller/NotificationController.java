package com.hm.pj9.controller;

import com.hm.pj9.model.NotificationResult;
import com.hm.pj9.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {
    @Autowired
    UserService userService;

    /*
    * 사용자 알림
    * */

    @GetMapping("/notice")
    public String notice(Model model, HttpSession session) { // 사용자 알림 가져오기
        NotificationResult notificationResult = userService.getNotification((String) session.getAttribute("userId"));
        model.addAttribute("notification", notificationResult.getNotifications());
        model.addAttribute("postCountMap", notificationResult.getPostCountMap());
        model.addAttribute("commentCountMap", notificationResult.getcommentCountMap());
        return "notification";
    }


    @GetMapping("check/notification/{post_num}")
    public String checkNotification( @PathVariable Integer post_num, HttpSession session, @RequestParam String type) { //알림 확인
        userService.readNotification(post_num, (String) session.getAttribute("userId"), type);
        return  "redirect:/board/" + post_num;
    }


}
