
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

    //====================================================
    // In-Memory Room Data
    //====================================================
    private static DoublyLinkedList<Room> roomData = new DoublyLinkedList<>();
    
    

    //====================================================
    // Create Initial Room Data
    //====================================================
    public static ListInterface<Room> createRoomData() {

    DoublyLinkedList<Room> rooms = new DoublyLinkedList<>();

    //================================================
    // SINGLE ROOMS S01 - S10
    //================================================
    // S01-S05 Dirty
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

        //================================================
        // MEDIUM ROOMS M01 - M10
        //================================================
        // M01-M05 Cleaning
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

        // M06-M10 Ready
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

        //================================================
        // LARGE ROOMS L01 - L10
        //================================================
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

        // L06-L07 Late Checkout Hold
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

    //====================================================
    // Create New Room
    //====================================================
    private static Room newRoom(String roomNumber, int roomType) {

        Room room = new Room(roomNumber, roomType);

        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Room registered!"));

        return room;
    }

    //====================================================
    // Advance Room To Target Status
    //====================================================
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

            room.getStatusHistory().insertAndAdvance(new StatusEntry(nextStatus, statusNote));

            currentStatus = nextStatus;
        }

        return room;
    }

    //====================================================
    // Create Late Check-Out Room
    //====================================================
    private static Room lateCheckoutRoom(String roomNumber, int roomType) {

        Room room = advanceTo(roomNumber, roomType, RoomStatusUtil.Inspected, "Passed inspection");

        room.getStatusHistory().rollback();

        room.getStatusHistory().spliceAfterCurrent(new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold, "Guest requested late check-out"));

        return room;
    }

    //====================================================
    // Save Room Data In Memory
    //====================================================
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

    //====================================================
    // Retrieve Room Data From Memory
    //====================================================
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

    //====================================================
    // Add Room
    //====================================================
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

    //====================================================
    // Get Room By Position
    //====================================================
    public Room getRoom(int position) {

        if (position < 1 || position > roomData.getSize()) {
            return null;
        }

        return roomData.getEntry(position);
    }

    //====================================================
    // Remove Room By Position
    //====================================================
    public Room removeRoom(int position) {

        if (position < 1 || position > roomData.getSize()) {
            return null;
        }

        return roomData.remove(position);
    }

    //====================================================
    // Remove Room By Room Number
    //====================================================
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

    //====================================================
    // Search Room By Room Number
    //====================================================
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

    //====================================================
    // Get Total Rooms
    //====================================================
    public int getTotalRooms() {
        return roomData.getSize();
    }

    //====================================================
    // Check Room Data Is Empty
    //====================================================
    public boolean isRoomDataEmpty() {
        return roomData == null || roomData.isEmpty();
    }

    //====================================================
    // Get All Room Data
    //====================================================
    public DoublyLinkedList<Room> getAllRooms() {
        return roomData;
    }
    
    public int getTotalRoomByType(
        int roomType) {

        int count = 0;

        for (int i = 1;
             i <= roomData.getSize();
             i++) {

            Room room =
                    roomData.getEntry(i);

            if (room != null
                    && room.getRoomType()
                    == roomType) {

                count++;
            }
        }
        return count;
    }
    
    public int getReadyRoomByType(int roomType) {

        int count = 0;

        for (int i = 1;
             i <= roomData.getSize();
             i++) {

            Room room =
                    roomData.getEntry(i);

            if (room == null
                    || room.getRoomType()
                    != roomType) {

                continue;
            }

            StatusEntry currentStatus =
                    room.getStatusHistory()
                            .getCurrentData();

            if (currentStatus != null
                    && currentStatus.getStatusCode()
                    == RoomStatusUtil.Ready_For_CheckIN) {

                count++;
            }
        }
        return count;
    }
}