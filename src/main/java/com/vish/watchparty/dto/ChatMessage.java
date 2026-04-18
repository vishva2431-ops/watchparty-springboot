package com.vish.watchparty.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomCode;
    private String sender;
    private String text;
}