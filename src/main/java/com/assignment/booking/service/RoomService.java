package com.assignment.booking.service;

import com.assignment.booking.entity.Room;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoomService {
    Room addRoom(Room room);
    Room getRoomById(Integer room);
    List<Room> getAllRoom();
}
