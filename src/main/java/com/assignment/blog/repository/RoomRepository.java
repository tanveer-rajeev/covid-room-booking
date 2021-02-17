package com.assignment.blog.repository;

import com.assignment.blog.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {

    Room findByRoomName(String name);
}
