package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
