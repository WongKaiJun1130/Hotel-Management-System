/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.Room;
import entity.StatusEntry;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import adt.ListInterface;
import adt.DoublyLinkedList;

/**
 *
 * @author Kah Shun
 */
public class RoomDao {
    
    private static DoublyLinkedList<Room> roomData = new DoublyLinkedList<>();

    // Builds and returns the 20 dummy rooms directly in memory - no file
    // storage involved.
    public static ListInterface<Room> createRoomData() {

        DoublyLinkedList<Room> rooms = new DoublyLinkedList<>();
        
        // --- Rooms 101-105: Normal, just registered (Dirty) ---
        rooms.add(newRoom("101", RoomTypeUtil.Single_Room));
        rooms.add(newRoom("102", RoomTypeUtil.Single_Room));
        rooms.add(newRoom("103", RoomTypeUtil.Single_Room));
        rooms.add(newRoom("104", RoomTypeUtil.Single_Room));
        rooms.add(newRoom("105", RoomTypeUtil.Single_Room));

        // --- Rooms 106-110: Deluxe, cleaning in progress ---
        rooms.add(advanceTo("106", RoomTypeUtil.Medium_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Maria"));
        rooms.add(advanceTo("107", RoomTypeUtil.Medium_Room, RoomStatusUtil.Clean_In_Progress, "Staff: John"));
        rooms.add(advanceTo("108", RoomTypeUtil.Medium_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Amy"));
        rooms.add(advanceTo("109", RoomTypeUtil.Medium_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Ben"));
        rooms.add(advanceTo("110", RoomTypeUtil.Medium_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Cara"));

        // --- Rooms 111-114: VIP, inspected ---
        rooms.add(advanceTo("111", RoomTypeUtil.Large_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("112", RoomTypeUtil.Large_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("113", RoomTypeUtil.Large_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("114", RoomTypeUtil.Large_Room, RoomStatusUtil.Inspected, "Passed inspection"));

        // --- Rooms 115-118: Ready for Check-In ---
        rooms.add(advanceTo("115", RoomTypeUtil.Single_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("116", RoomTypeUtil.Medium_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("117", RoomTypeUtil.Medium_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("118", RoomTypeUtil.Large_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));

        // --- Rooms 119-120: VIP, mid-clean guest requested late check-out ---
        // shows the rollback + interrupt (splice) behaviour: room was inspected,
        // supervisor rolled it back to cleaning, then guest asked to hold the room.
        rooms.add(lateCheckoutRoom("119", RoomTypeUtil.Large_Room));
        rooms.add(lateCheckoutRoom("120", RoomTypeUtil.Large_Room));
        
        //================================================
        // Save Into Shared Memory
        //================================================
        roomData = rooms;

        System.out.println(
                roomData.getSize()
                + " Rooms Created In Memory!"
        );

        return rooms;
    }

    // Fresh room, only the initial "Dirty" status entry
    private static Room newRoom(String roomNum, int roomType) {
        Room room = new Room(roomNum, roomType);
        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Room registered"));
        return room;
    }

    // Room advanced forward from Dirty up to the given status
    private static Room advanceTo(String roomNum, int roomType, int targetStatus, String note) {
        Room room = newRoom(roomNum, roomType);

        int status = RoomStatusUtil.Dirty;
        while (status != targetStatus) {
            int next = RoomStatusUtil.nextStatusAfter(status);
            if (next == -1) {
                break;
            }
            String entryNote = (next == targetStatus) ? note : "";
            room.getStatusHistory().insertAndAdvance(new StatusEntry(next, entryNote));
            status = next;
        }

        return room;
    }

    // Room mid-clean, interrupted by a guest's late check-out request
    private static Room lateCheckoutRoom(String roomNum, int roomType) {
        Room room = advanceTo(roomNum, roomType, RoomStatusUtil.Inspected, "Passed inspection");

        // supervisor logged it too early, rolls back to Cleaning In Progress
        room.getStatusHistory().rollback();

        // guest requests late check-out mid-clean - splice in a hold entry
        room.getStatusHistory().spliceAfterCurrent(
                new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold, "Guest requested late check-out")
        );

        return room;
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
    // Save Room Data In Memory
    //====================================================
    // Overwrites the in-memory store with whatever list is handed in.
    // Used by HousekeepingControl to push its live roomList back here
    // after every mutation, so this DAO's copy never goes stale.
    public void saveToFile(ListInterface<Room> roomList) {

        DoublyLinkedList<Room> updated = new DoublyLinkedList<>();

        if (roomList != null) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room != null) {
                    updated.add(room);
                }
            }
        }

        roomData = updated;
    }
}