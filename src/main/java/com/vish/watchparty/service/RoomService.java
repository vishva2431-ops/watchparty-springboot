package com.vish.watchparty.service;

import com.vish.watchparty.model.Room;
import com.vish.watchparty.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Optional<Room> getRoom(String roomCode) {
        return roomRepository.findByRoomCode(roomCode);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }
}