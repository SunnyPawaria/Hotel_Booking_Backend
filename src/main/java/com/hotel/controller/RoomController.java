package com.hotel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.exception.PhotoRetrievalException;
import com.hotel.model.BookedRoom;
import com.hotel.model.Room;
import com.hotel.response.BookingResponse;
import com.hotel.response.RoomResponse;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173") // Allow your frontend's origin
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

	private final RoomService roomService;
	private final BookingService bookingService;

	@PostMapping("/add/new-room")
	public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
			@RequestParam("roomType") String roomType, @RequestParam("roomPrice") BigDecimal roomPrice)
			throws SerialException, IOException, SQLException {

		Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
		RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/room/types")
	public List<String> getRoomTypes() {
		return roomService.getAllRoomTypes();
	}

	@GetMapping("all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws Throwable {
		List<Room> rooms = roomService.getAllRoom();
		List<RoomResponse> roomResponses = new ArrayList<>();
		for (Room room : rooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if (photoBytes != null && photoBytes.length > 0) {
				String base64Photo = Base64.encodeBase64String(photoBytes);
				RoomResponse roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(base64Photo);
				roomResponses.add(roomResponse);
			}
		}
		return ResponseEntity.ok(roomResponses);
	}
	
	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId){
		roomService.deleteRoom(roomId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private RoomResponse getRoomResponse(Room room) {
//		List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//		List<BookingResponse> bookingInfo = bookings.stream().map(booking -> new BookingResponse(booking.getBookingId(),
//				booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();
		byte[] photoBytes = null;
		Blob photoBlob = room.getPhoto();
		if (photoBlob != null) {
			try {
				photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
			} catch (SQLException e) {
				throw new PhotoRetrievalException("Error retreving photo");
			}
		}
		return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes);
	}

	private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
		return bookingService.getAllBookingsByRoomId(roomId);
	}
}
