
package dao;

import entity.Room;
import entity.StatusEntry;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import adt.ListInterface;
import adt.DoublyLinkedList;

/**
 *
 * @author CHia Kah Shun
 */
public class RoomDao {

  
    private static DoublyLinkedList<Room> roomData = new DoublyLinkedList<>();
    
    

    
    public static ListInterface<Room> createRoomData() {

    DoublyLinkedList<Room> rooms = new DoublyLinkedList<>();

    
        for (int i = 1; i <= 5; i++) {
            rooms.add(
                    newRoom(
                            "S" + String.format("%02d", i),
                            RoomTypeUtil.Single_Room
                    )
            );
        }

        // S06-S10 Ready
        for (int i = 6; i <= 10; i++) {
            rooms.add( advanceTo("S" + String.format("%02d", i),RoomTypeUtil.Single_Room,RoomStatusUtil.Ready_For_CheckIN,"Ready")
            );
        }

        
        String[] staff = {
            "Maria", "John", "Amy", "Ben", "Cara"
        };
        for (int i = 1; i <= 5; i++) {
            rooms.add(
                    advanceTo(
                            "M" + String.format("%02d", i),
                            RoomTypeUtil.Medium_Room,
                            RoomStatusUtil.Clean_In_Progress,
                            "Staff: " + staff[i - 1]
                    )
            );
        }

        
        for (int i = 6; i <= 10; i++) {
            rooms.add(
                    advanceTo(
                            "M" + String.format("%02d", i),
                            RoomTypeUtil.Medium_Room,
                            RoomStatusUtil.Ready_For_CheckIN,
                            "Ready"
                    )
            );
        }

        
        // L01-L04 Inspected
        for (int i = 1; i <= 4; i++) {

            rooms.add(
                    advanceTo(
                            "L" + String.format("%02d", i),
                            RoomTypeUtil.Large_Room,
                            RoomStatusUtil.Inspected,
                            "Passed inspection"
                    )
            );
        }

        // L05 Ready
        rooms.add(
                advanceTo(
                        "L05",
                        RoomTypeUtil.Large_Room,
                        RoomStatusUtil.Ready_For_CheckIN,
                        "Ready"
                )
        );

        
        rooms.add(
                lateCheckoutRoom(
                        "L06",
                        RoomTypeUtil.Large_Room
                )
        );

        rooms.add(
                lateCheckoutRoom(
                        "L07",
                        RoomTypeUtil.Large_Room
                )
        );

        // L08-L10 Ready
        for (int i = 8; i <= 10; i++) {
            rooms.add(
                    advanceTo(
                            "L" + String.format("%02d", i),
                            RoomTypeUtil.Large_Room,
                            RoomStatusUtil.Ready_For_CheckIN,
                            "Ready"
                    )
            );
        }


        RoomDao roomDao = new RoomDao();

        roomDao.saveToFile(rooms);

        System.out.println(
                rooms.getSize()
                + " Rooms Created In Memory!"
        );
        return rooms;
    }

    
    private static Room newRoom(String roomNumber, int roomType) {

        Room room = new Room(roomNumber, roomType);

        room.getStatusHistory().addAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Room registered!"));

        return room;
    }

    
    private static Room advanceTo(String roomNumber, int roomType, int targetStatus, String note) {

        Room room = newRoom(roomNumber, roomType);

        int currentStatus = RoomStatusUtil.Dirty;

        while (currentStatus != targetStatus) {

            int nextStatus = RoomStatusUtil.nextStatusAfter(currentStatus);

            if (nextStatus == -1) {
                break;
            }

            String statusNote;

            if (nextStatus == targetStatus) {
                statusNote = note;
            } else {
                statusNote = "";
            }

            room.getStatusHistory().addAndAdvance(new StatusEntry(nextStatus, statusNote));

            currentStatus = nextStatus;
        }

        return room;
    }

    
    private static Room lateCheckoutRoom(String roomNumber, int roomType) {

        Room room = advanceTo(roomNumber, roomType, RoomStatusUtil.Inspected, "Passed inspection");

        room.getStatusHistory().rollback();

        room.getStatusHistory().insertAfterCurrent(new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold, "Guest requested late check-out"));

        return room;
    }

    
    public void saveToFile(ListInterface<Room> roomList) {

        roomData = new DoublyLinkedList<>();

        if (roomList == null) {
            return;
        }

        for (int i = 1; i <= roomList.getSize(); i++) {

            Room room = roomList.getEntry(i);

            if (room != null) {
                roomData.add(room);
            }
        }
    }

    
    public DoublyLinkedList<Room> retrieveFromFile() {

        DoublyLinkedList<Room> copiedRoomList = new DoublyLinkedList<>();

        if (roomData == null || roomData.isEmpty()) {
            createRoomData();
        }

        for (int i = 1; i <= roomData.getSize(); i++) {

            Room room = roomData.getEntry(i);

            if (room != null) {
                copiedRoomList.add(room);
            }
        }

        return copiedRoomList;
    }

    
    public boolean addRoom(Room room) {

        if (room == null) {
            return false;
        }

        if (room.getRoomNum() == null || room.getRoomNum().trim().isEmpty()) {
            return false;
        }

        if (searchRoomByNumber(room.getRoomNum()) != null) {
            return false;
        }

        roomData.add(room);

        return true;
    }

    
    public Room getRoom(int position) {

        if (position < 1 || position > roomData.getSize()) {
            return null;
        }

        return roomData.getEntry(position);
    }

    
    public Room removeRoom(int position) {

        if (position < 1 || position > roomData.getSize()) {
            return null;
        }

        return roomData.remove(position);
    }

    
    public Room removeRoomByNumber(String roomNumber) {

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= roomData.getSize(); i++) {

            Room room = roomData.getEntry(i);

            if (room != null
                    && room.getRoomNum() != null
                    && room.getRoomNum().equalsIgnoreCase(roomNumber.trim())) {

                return roomData.remove(i);
            }
        }

        return null;
    }

    
    public Room searchRoomByNumber(String roomNumber) {

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= roomData.getSize(); i++) {

            Room room = roomData.getEntry(i);

            if (room == null || room.getRoomNum() == null) {
                continue;
            }

            if (room.getRoomNum().equalsIgnoreCase(roomNumber.trim())) {
                return room;
            }
        }

        return null;
    }

    
    public int getTotalRooms() {
        return roomData.getSize();
    }

    
    public boolean isRoomDataEmpty() {
        return roomData == null || roomData.isEmpty();
    }

    
    public DoublyLinkedList<Room> getAllRooms() {
        return roomData;
    }
}