package com.hm.pj9.model;

import java.util.*;

public class NotificationResult {
    private List<Notification> notifications;
    private Map<Integer, Map<String, Integer>> postCountMap;
    private Map<Integer, Map<String, Integer>> commentCountMap;

    public NotificationResult(List<Notification> notifications, Map<Integer, Map<String, Integer>> postCountMap, Map<Integer, Map<String, Integer>> commentCountMap) {
        this.notifications = notifications;
        this.postCountMap = postCountMap;
        this.commentCountMap = commentCountMap;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public Map<Integer, Map<String, Integer>> getPostCountMap() {
        return postCountMap;
    }

    public Map<Integer, Map<String, Integer>> getcommentCountMap() {
        return commentCountMap;
    }
}
