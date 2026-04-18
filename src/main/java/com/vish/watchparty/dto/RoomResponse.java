package com.vish.watchparty.dto;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RoomResponse {
    private Room room;
    private Movie movie;
}