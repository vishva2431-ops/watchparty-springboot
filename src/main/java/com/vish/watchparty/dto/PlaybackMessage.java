package com.vish.watchparty.dto;

import lombok.Data;

@Data
public class PlaybackMessage {
    private String roomCode;
    private String action;
    private Double currentTime;
    private Double playbackRate;
    private String quality;
}