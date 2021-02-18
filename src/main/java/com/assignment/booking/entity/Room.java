package com.assignment.booking.entity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String roomName;
    private Integer capacity;


    @OneToMany(mappedBy = "room",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Booking> booking;

    @Override
    public String toString() {
        return "Room{" + "roomName='" + roomName + '\'' + ", capacity=" + capacity + '}';
    }
}
