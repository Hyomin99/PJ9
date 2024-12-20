package com.hm.pj9.repository;

import com.hm.pj9.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findAllByToUserId(String toUserId); //나에게 온 모든 정보 가져오기
    List<ChatMessage> findAllByToUserIdAndFromUserId(String toUserId, String fromUserId); //나에게 온 특정 한명의 대화 기록 가져오기

    void deleteAllByFromUserIdOrToUserId(String fromUserId, String toUserId);

}
