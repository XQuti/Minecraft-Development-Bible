package io.xquti.mdb.websocket;

import io.xquti.mdb.dto.ForumPostDto;
import io.xquti.mdb.dto.ForumThreadDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WebSocket controller for real-time forum updates.
 * Handles broadcasting of new threads and posts to connected clients.
 */
@Controller
public class ForumWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ForumWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast new thread creation to all connected clients.
     */
    @MessageMapping("/forum/thread/new")
    @SendTo("/topic/forum/threads")
    public ForumThreadDto broadcastNewThread(@Payload ForumThreadDto thread) {
        return thread;
    }

    /**
     * Broadcast new post to thread subscribers.
     */
    @MessageMapping("/forum/post/new")
    public void broadcastNewPost(@Payload ForumPostDto post) {
        // Send to specific thread topic
        messagingTemplate.convertAndSend("/topic/forum/thread/" + post.getThreadId(), post);
        
        // Also send to general forum updates
        messagingTemplate.convertAndSend("/topic/forum/posts", post);
    }

    /**
     * Send notification to specific user.
     */
    public void sendUserNotification(String username, Object notification) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }

    /**
     * Broadcast thread update (like title change, status change).
     */
    public void broadcastThreadUpdate(ForumThreadDto thread) {
        messagingTemplate.convertAndSend("/topic/forum/thread/" + thread.getId() + "/updates", thread);
    }

    /**
     * Broadcast user activity (typing indicators, online status).
     */
    public void broadcastUserActivity(Long threadId, String username, String activity) {
        UserActivity userActivity = new UserActivity(username, activity, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/forum/thread/" + threadId + "/activity", userActivity);
    }

    /**
     * Inner class for user activity messages.
     */
    public static class UserActivity {
        private String username;
        private String activity;
        private long timestamp;

        public UserActivity(String username, String activity, long timestamp) {
            this.username = username;
            this.activity = activity;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}