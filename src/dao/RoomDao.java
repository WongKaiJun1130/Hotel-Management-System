package dao;

import entity.Room;
import entity.StatusEntry;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import adt.DoublyLinkedList;
import adt.ListInterface;

public class RoomDao {

    //====================================================
    // In-Memory Room Data
    //====================================================
    private static DoublyLinkedList<Room> roomData = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Room Data
    //====================================================
    public static void createRoomData() {

        DoublyLinkedList<Room> rooms = new DoublyLinkedList<>();

        //================================================
        // Rooms 101-105: Normal Room, Dirty
        //================================================
        rooms.add(newRoom("101", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("102", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("103", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("104", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("105", RoomTypeUtil.Normal_Room));

        //================================================
        // Rooms 106-110: Deluxe, Cleaning In Progress
        //================================================
        rooms.add(advanceTo("106", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Maria"));
        rooms.add(advanceTo("107", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: John"));
        rooms.add(advanceTo("108", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Amy"));
        rooms.add(advanceTo("109", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Ben"));
        rooms.add(advanceTo("110", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Cara"));

        //================================================
        // Rooms 111-114: VIP, Inspected
        //================================================
        rooms.add(advanceTo("111", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("112", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("113", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("114", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));

        //================================================
        // Rooms 115-118: Ready For Check-In
        //================================================
        rooms.add(advanceTo("115", RoomTypeUtil.Normal_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("116", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("117", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("118", RoomTypeUtil.VIP_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));

        //================================================
        // Rooms 119-120: Late Check-Out Hold
        //================================================
        rooms.add(lateCheckoutRoom("119", RoomTypeUtil.VIP_Room));
        rooms.add(lateCheckoutRoom("120", RoomTypeUtil.VIP_Room));

        RoomDao roomDao = new RoomDao();

        roomDao.saveToFile(rooms);

        System.out.println(rooms.getSize() + " Rooms Created In Memory!");
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

        System.out.println("Room Database Updated In Memory!");
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
}