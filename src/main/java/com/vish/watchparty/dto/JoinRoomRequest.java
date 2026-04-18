package com.vish.watchparty.dto;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomCode;
    private String userName;
}