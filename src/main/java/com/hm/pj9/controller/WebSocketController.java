package com.hm.pj9.controller;

import com.hm.pj9.model.ChatMessage;
import com.hm.pj9.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {

    @Value("${default.web.url}")
    private String webUrl;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;


    @GetMapping("chat/list")
    public String chatList(){ //채팅 목록 페이지

        return "chatList";
    }


    @ResponseBody
    @GetMapping("get/chat/list")
    public List<ChatMessage> getChatList(HttpSession session){ // 채팅 목록 가져오기
        String userId = (String) session.getAttribute("userId");
        return chatService.getChatById(userId);
    }

    @ResponseBody
    @GetMapping("get/chat/{toUserId}")
    public Map<String, List<ChatMessage>> getChats(HttpSession session , @PathVariable String toUserId){ // 본인아이디, 상대 아이디에 해당하는 채팅 가져오기
        String userId = (String) session.getAttribute("userId");

        return chatService.getSpecificChats(userId, toUserId);
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) { // 처음 소켓 연결 시 실행되는 메서드
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = sha.getNativeHeader("userId").get(0); // 클라이언트에서 전송한 사용자 ID
        sha.getSessionAttributes().put("userId", userId); // 세션 속성에 저장
    }


    @MessageMapping("message/{toUserId}")
    @SendTo("/queue/messages/{toUserId}")
    public ChatMessage sendMessage(@DestinationVariable String toUserId, @Payload ChatMessage messagePayload, SimpMessageHeaderAccessor headerAccessor) {
        // 구독자한 채널에 메시지 전송
        String fromUserId = (String) headerAccessor.getSessionAttributes().get("userId");
        String message = messagePayload.getText(); // 메시지 텍스트 추출

        if (fromUserId != null && toUserId != null && message != null) {
            chatService.saveChat(messagePayload); //몽고 디비에 채팅 저장
            return messagePayload;
        }
        return null;
    }





    @GetMapping("chat/{toUserId}")
    public String chat(HttpSession session, Model model, @PathVariable String toUserId){ //채팅 페이지로 전환
        model.addAttribute("userId", (String)session.getAttribute("userId"));
        model.addAttribute("toUserId", toUserId);
        model.addAttribute("webUrl", webUrl);
        return "chat";
    }
}