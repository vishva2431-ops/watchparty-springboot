package com.vish.watchparty.dto;

public class RoomSyncMessage {
    private String roomCode;
    private String action;
    private Double currentTime;
    private Double playbackRate;
    private String quality;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Double currentTime) {
        this.currentTime = currentTime;
    }

    public Double getPlaybackRate() {
        return playbackRate;
    }

    public void setPlaybackRate(Double playbackRate) {
        this.playbackRate = playbackRate;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}