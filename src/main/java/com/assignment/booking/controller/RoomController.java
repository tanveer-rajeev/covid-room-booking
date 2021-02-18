package com.assignment.booking.controller;

import com.assignment.booking.entity.Room;
import com.assignment.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomController( RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    @GetMapping("/{roomName}")
    public Room getRoom(@PathVariable String roomName){
        return roomRepository.findByRoomName(roomName);
    }

    @GetMapping
    public List<Room> getAllRoom(){
        return roomRepository.findAll();
    }
}
