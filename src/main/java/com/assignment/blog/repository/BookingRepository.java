package com.assignment.blog.repository;

import com.assignment.blog.entity.Booking;
import com.assignment.blog.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> {
    Booking findByRoom(Room room);
    Booking findByBookingDate(String bookingDate);
}
