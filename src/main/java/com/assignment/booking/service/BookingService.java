package com.assignment.booking.service;

import com.assignment.booking.DTO.BookingDTO;
import com.assignment.booking.entity.Booking;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface BookingService {
    Booking getBookingById(Integer id);
    ResponseEntity<?> makeBooking(BookingDTO booking, String roomName) throws ParseException;
    int getCapacityFreeWorkingPlace(String requestedBookingDate) throws ParseException;
}
