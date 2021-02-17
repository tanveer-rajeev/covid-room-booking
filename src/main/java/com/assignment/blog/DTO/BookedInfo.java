package com.assignment.blog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class BookedInfo {

    private List<String> bookedPersonsList;
    private List<String> roomList;


}
