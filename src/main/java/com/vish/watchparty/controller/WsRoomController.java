package com.vish.watchparty.controller;

import com.vish.watchparty.dto.ChatMessage;
import com.vish.watchparty.dto.PlaybackMessage;
import com.vish.watchparty.model.Room;
import com.vish.watchparty.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WsRoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;

    public WsRoomController(SimpMessagingTemplate messagingTemplate, RoomService roomService) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }

    @MessageMapping("/room.sync")
    public void syncPlayback(@Payload PlaybackMessage message) {
        roomService.getRoom(message.getRoomCode()).ifPresent((Room room) -> {
            room.setCurrentTime(message.getCurrentTime());

            if ("PLAY".equals(message.getAction())) {
                room.setPlaying(true);
            }

            if ("PAUSE".equals(message.getAction())) {
                room.setPlaying(false);
            }

            if ("RATE".equals(message.getAction())) {
                room.setPlaybackRate(message.getPlaybackRate());
            }

            if (message.getQuality() != null && !message.getQuality().isBlank()) {
                room.setCurrentQuality(message.getQuality());
            }

            roomService.save(room);
        });

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomCode(), message);
    }

    @MessageMapping("/room.chat")
    public void sendChat(@Payload ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomCode(), message);
    }
}