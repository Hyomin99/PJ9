package com.hm.pj9.repository;

import com.hm.pj9.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface NotificationRepository  extends JpaRepository<Notification, Integer> {

    List<Notification> getNotificationByTargetIdAndReadStatus(User user, byte readStatus);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.targetId = ?1 AND n.postNum = ?2 AND n.notificationType = ?3")
    void deleteNotification(User user, Post post, String type);

    void deleteAllByCommentNum(Comment comment);

    void deleteAllByReplyNum(Reply reply);

    void deleteAllByPostNum(Post post);

    void deleteAllByTargetId(User user);

}
