package com.vish.watchparty.controller;

import com.vish.watchparty.dto.ChatMessage;
import com.vish.watchparty.dto.RoomSyncMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room.sync")
    public void roomSync(RoomSyncMessage message) {
        if (message.getRoomCode() == null || message.getRoomCode().isBlank()) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomCode(), message);
    }

    @MessageMapping("/room.chat")
    public void roomChat(ChatMessage message) {
        if (message.getRoomCode() == null || message.getRoomCode().isBlank()) {
            return;
        }
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomCode(), message);
    }
}