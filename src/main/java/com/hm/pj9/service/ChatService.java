package com.hm.pj9.service;

import com.hm.pj9.model.ChatMessage;
import com.hm.pj9.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    public void saveChat(ChatMessage chatMessage){
        chatRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatById(String userId){
        return chatRepository.findAllByToUserId(userId);
    }

    public Map<String, List<ChatMessage>> getSpecificChats(String toUserId, String fromUserId) { //상대방과 한 대화 가져오기
        Map<String, List<ChatMessage>> specificChats = new HashMap<>();
        List<ChatMessage> otherChat = chatRepository.findAllByToUserIdAndFromUserId(toUserId, fromUserId); // 상대방 채팅 기록
        List<ChatMessage> myChat = chatRepository.findAllByToUserIdAndFromUserId(fromUserId, toUserId); // 내 채팅 기록

        specificChats.put("otherChat", otherChat);
        specificChats.put("myChat", myChat);
        return specificChats;
    }


}
