package com.assignment.booking.DTO;

import com.assignment.booking.response.RoomResponse;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BookedInfo {

    private List<String> bookedPersonsList;
    private Set<RoomResponse> roomList;


}
