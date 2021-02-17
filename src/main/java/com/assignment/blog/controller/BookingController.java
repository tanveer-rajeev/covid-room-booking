package com.assignment.blog.controller;

import com.assignment.blog.DTO.BookingDTO;
import com.assignment.blog.entity.Booking;

import com.assignment.blog.repository.RoomRepository;
import com.assignment.blog.service.BookingService;
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



}
