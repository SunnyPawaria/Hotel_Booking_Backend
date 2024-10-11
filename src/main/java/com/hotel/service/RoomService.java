package com.hotel.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.Room;

public interface RoomService {

	Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException, SerialException, SQLException;

	List<String> getAllRoomTypes();

	List<Room> getAllRoom();

	byte[] getRoomPhotoByRoomId(Long roomId) throws Throwable;

	void deleteRoom(Long roomId);

}
