package com.assignment.booking.controller;

import com.assignment.booking.DTO.BookingDTO;

import com.assignment.booking.repository.RoomRepository;
import com.assignment.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final RoomRepository roomRepository;

    @Autowired
    public BookingController(BookingService bookingService , RoomRepository roomRepository) {
        this.bookingService = bookingService;
        this.roomRepository = roomRepository;
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO booking , @PathVariable Integer roomId) throws
            ParseException {

        return bookingService.makeBooking(booking,roomId);
    }

    @GetMapping("/getCapacity/{date}")
    public int getAllFreeCapacity(@PathVariable String date) throws ParseException {
        return bookingService.getCapacityFreeWorkingPlace(date);
    }



}
