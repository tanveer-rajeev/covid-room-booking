package com.assignment.blog.service;

import com.assignment.blog.DTO.BookingDTO;
import com.assignment.blog.entity.Booking;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface BookingService {
    Booking getBookingById(Integer id);
    ResponseEntity<?> makeBooking(BookingDTO booking, Integer roomId) throws ParseException;
}
