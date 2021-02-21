package com.assignment.booking.ServiceImplementation;

import com.assignment.booking.DTO.BookingDTO;
import com.assignment.booking.Exception.BookedNotificationHandler;
import com.assignment.booking.Exception.ResourceNotFoundException;
import com.assignment.booking.DTO.BookedInfo;
import com.assignment.booking.entity.Booking;
import com.assignment.booking.entity.Room;
import com.assignment.booking.repository.BookingRepository;
import com.assignment.booking.repository.RoomRepository;
import com.assignment.booking.repository.UserRepository;
import com.assignment.booking.response.RoomResponse;
import com.assignment.booking.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImplementation implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImplementation(BookingRepository bookingRepository , RoomRepository roomRepository ,
                                        ModelMapper modelMapper , UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository    = roomRepository;
        this.modelMapper       = modelMapper;
        this.userRepository    = userRepository;
    }

    @Override
    public Booking getBookingById(Integer id) {
        return bookingRepository
                .findById(id)
                .stream()
                .filter(booking -> booking
                        .getId()
                        .equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }


    // TODO: Make Booking
    @Override
    public ResponseEntity<?> makeBooking(BookingDTO bookingDTO , String roomName) throws ParseException {
        Booking bookingRequest = modelMapper.map(bookingDTO , Booking.class);

        // check given user name validation during booking
        if (userRepository.findByUsername(bookingRequest.getUsername()) == null) {
            throw new ResourceNotFoundException("User name not found in the system " + bookingRequest.getUsername());
        }

        // check booking date validation
        if (!checkValidationOfBookingDate(bookingRequest.getBookingDate())) {
            throw new ResourceNotFoundException("Booking date not valid " + bookingRequest.getBookingDate());
        }

        Room requestedRoom = roomRepository.findByRoomName(roomName);

        List<Booking> bookingList = requestedRoom.getBooking();
        bookingRequest.setRoom(requestedRoom);

        if (isWorkingPlaceAvailable(bookingRequest , bookingList)) {

            bookingRequest.setUsername(bookingDTO.getUsername());
            Booking bookingConfirmation = bookingRepository.save(bookingRequest);
            return new ResponseEntity<>(bookingConfirmation , HttpStatus.ACCEPTED);
        }

        BookedInfo bookedInfo = getInformedForAvailableRoom(requestedRoom , bookingRequest.getBookingDate());

        //return new ResponseEntity<>(bookedInfo , HttpStatus.ACCEPTED);

        // Documentation is written  into "GlobalExceptionHandler"
        throw new BookedNotificationHandler("Already booked name list",bookedInfo );
    }

    // TODO: Get Capacity
    public int getCapacityFreeWorkingPlace(String requestedBookingDate) throws ParseException {

        if (!checkValidationOfBookingDate(requestedBookingDate)) {
            throw new ResourceNotFoundException("Booking date not valid " + requestedBookingDate);
        }

        List<Room> roomList = roomRepository.findAll();
        double numberOfWorkingPlace = 0;
        double currentRoomCapacity = 0;

        for (Room bookedRoom: roomList) {

            List<Booking> bookingList = bookedRoom.getBooking();

            numberOfWorkingPlace += bookedRoom.getCapacity();


            for (Booking booked: bookingList) {
                if (booked
                        .getBookingDate()
                        .equals(requestedBookingDate)) {
                    currentRoomCapacity++;
                }
            }
        }
        double result = (numberOfWorkingPlace - currentRoomCapacity) / numberOfWorkingPlace;

        return (int) Math.round(result * 100);
    }

    // TODO: check every working place into the room has  same booking date or not
    public boolean isWorkingPlaceAvailable(Booking booking , List<Booking> bookingList) {

        if (bookingList.size() == 0) return true;

        int getCapacityOfRoom = booking
                .getRoom()
                .getCapacity();

        for (Booking value: bookingList) {
            if (value
                    .getBookingDate()
                    .equals(booking.getBookingDate())) {
                getCapacityOfRoom--;
            }
        }

        return getCapacityOfRoom != 0;
    }

    // TODO: User get informed available room with working place and also booked user name
    public BookedInfo getInformedForAvailableRoom(Room room , String bookingDate) {

        // Room is already booked out by [NAME_1], [NAME_2], [NAME_n] on this day

        List<String> bookedPersonNames = room
                .getBooking()
                .stream()
                .filter(booking -> booking
                        .getBookingDate()
                        .equals(bookingDate))
                .map(Booking::getUsername)
                .collect(Collectors.toList());


        // --------Try available room with open working place--------

        // get all room except the requested room
        List<Room> restOfTheRooms = roomRepository
                .findAll()
                .stream()
                .filter(room1 -> !room1
                        .getId()
                        .equals(room.getId()))
                .collect(Collectors.toList());


        // get all available room name with working place
        Set<RoomResponse> getAllAvailableRoomName = getAllRoomWithFreeWorkingPlace(bookingDate , restOfTheRooms);

        // wrap those 2 info--1.Booked persons list 2.Available room name list
        BookedInfo bookedInfo = new BookedInfo();
        bookedInfo.setRoomList(getAllAvailableRoomName);
        bookedInfo.setBookedPersonsList(bookedPersonNames);

        return bookedInfo;
    }

    public Set<RoomResponse> getAllRoomWithFreeWorkingPlace(String requestedBookingDate , List<Room> restRoomList) {

        Set<RoomResponse> allAvailableRoomName = new HashSet<>();
        boolean alreadyBooked = false;

        for (Room bookedRoom: restRoomList) {
            int currentRoomCapacity = 0;

            // get all booking  if a room has multiple capacities of working place
            List<Booking> bookingList = bookedRoom.getBooking();

            // check every room has a same booking date or not
            // then collect those non booked room that do not overlap requested booking date
            RoomResponse roomResponse = modelMapper.map(bookedRoom , RoomResponse.class);
            Integer totalRoomCapacity = roomResponse.getCapacity();

            if (bookingList.isEmpty()) {
                allAvailableRoomName.add(roomResponse);
            } else {
                for (Booking booked: bookingList) {
                    if (booked
                            .getBookingDate()
                            .equals(requestedBookingDate)) {
                        totalRoomCapacity--;
                    }
                }
                roomResponse.setCapacity(totalRoomCapacity);
                if (totalRoomCapacity != 0) {
                    allAvailableRoomName.add(roomResponse);
                }

            }

        }
        return allAvailableRoomName;
    }


    private boolean checkValidationOfBookingDate(String requestBookingDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = sdf.parse(requestBookingDate);
        Date currentDate = sdf.parse(String.valueOf(LocalDate.now()));

        return requestDate.getTime() >= currentDate.getTime();
    }


}
