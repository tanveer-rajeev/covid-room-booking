package com.assignment.blog.ServiceImplementation;

import com.assignment.blog.DTO.BookingDTO;
import com.assignment.blog.Exception.ResourceNotFoundException;
import com.assignment.blog.DTO.BookedInfo;
import com.assignment.blog.entity.Booking;
import com.assignment.blog.entity.Room;
import com.assignment.blog.repository.BookingRepository;
import com.assignment.blog.repository.RoomRepository;
import com.assignment.blog.repository.UserRepository;
import com.assignment.blog.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImplementation implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

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
    public ResponseEntity<?> makeBooking(BookingDTO bookingDTO , Integer roomId) throws ParseException {
        Booking bookingRequest = modelMapper.map(bookingDTO , Booking.class);

        Room requestedRoom = roomRepository
                .findById(roomId)
                .stream()
                .filter(room -> room
                        .getId()
                        .equals(roomId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        //TODO: trying to set automatic in booking name field fill up with current logged in user nam
        // but it  does not save in database.In database save as  null
        // User currentUser = CurrentLoggedInUser.getUserEntity();
        // bookingRequest.setUsername(currentUser.getUserName());

        List<Booking> bookingList = requestedRoom.getBooking();
        bookingRequest.setRoom(requestedRoom);

        // check given user name validation during booking
        if (userRepository.findByUserName(bookingRequest.getUsername()) == null) {
            throw new ResourceNotFoundException("User name not found in the system " + bookingRequest.getUsername());
        }

        // check booking date validation
        if (!checkValidationOfBookingDate(bookingRequest.getBookingDate())) {
            throw new ResourceNotFoundException("Booking date not valid "+bookingRequest.getBookingDate());
        }

        //
        if (isWorkingPlaceAvailable(bookingRequest , bookingList)) {
            Booking bookingConfirmation = bookingRepository.save(bookingRequest);
            return new ResponseEntity<>(bookingConfirmation , HttpStatus.ACCEPTED);
        }

        BookedInfo bookedInfo = getInformedForAvailableRoom(requestedRoom , bookingRequest.getBookingDate());
        return new ResponseEntity<>(bookedInfo , HttpStatus.NOT_FOUND);
    }

    private boolean checkValidationOfBookingDate(String requestBookingDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = sdf.parse(requestBookingDate);
        Date currentDate = sdf.parse(String.valueOf(LocalDate.now()));

        return requestDate.getTime()>=currentDate.getTime();
    }

    // TODO: check every working place into the room has  same booking date or not
    public boolean isWorkingPlaceAvailable(Booking booking , List<Booking> bookingList) {

        if (bookingList.size() == 0) return true;
        int getCapacityOfRoom = booking
                .getRoom()
                .getCapacity();

        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList
                    .get(i)
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

        System.out.println("-------get  all room except the requested room");
        System.out.println(restOfTheRooms);


        // get all available room name with working place
        List<String> getAllAvailableRoomName = getAllRoomWithWorkingPlace(bookingDate , restOfTheRooms);

        // wrap those 2 info--1.Booked persons list 2.Available room name list
        BookedInfo bookedInfo = new BookedInfo();
        bookedInfo.setRoomList(getAllAvailableRoomName);
        bookedInfo.setBookedPersonsList(bookedPersonNames);

        return bookedInfo;
    }

    public List<String> getAllRoomWithWorkingPlace(String requestedBookingDate , List<Room> restRoomList) {

        List<String> allAvailableRoomName = new ArrayList<>();

        for (Room bookedRoom: restRoomList) {

            // get all booking  if a room has multiple capacities of working place
            List<Booking> bookingList = bookedRoom.getBooking();

            // check every room has a same booking date or not
            // then collect those non booked room that do not overlap requested booking date

            if (bookingList.isEmpty()) {
                allAvailableRoomName.add(bookedRoom.getRoomName());
            } else {
                for (Booking booked: bookingList) {
                    if (!booked
                            .getBookingDate()
                            .equals(requestedBookingDate)) {
                        allAvailableRoomName.add(bookedRoom.getRoomName());
                    }
                }
            }

        }
        return allAvailableRoomName;
    }


}
