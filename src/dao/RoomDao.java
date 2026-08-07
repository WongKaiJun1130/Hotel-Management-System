/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import Entity.Room;
import Entity.StatusEntry;
import Utility.RoomStatusUtil;
import Utility.RoomTypeUtil;
import Adt.DoublyLinkedList;
import java.io.*;

/**
 *
 * @author USER
 */
public class RoomDao {
    
    private String fileName = "RoomDatabase.dat";
    
    public static void createRoomData(){
        
        DoublyLinkedList.ArrayList<Room> rooms = new DoublyLinkedList.ArrayList<> ();
        
        // --- Rooms 101-105: Normal, just registered (Dirty) ---
        rooms.add(newRoom("101", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("102", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("103", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("104", RoomTypeUtil.Normal_Room));
        rooms.add(newRoom("105", RoomTypeUtil.Normal_Room));
        
        // --- Rooms 106-110: Deluxe, cleaning in progress ---
        rooms.add(advanceTo("106", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Maria"));
        rooms.add(advanceTo("107", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: John"));
        rooms.add(advanceTo("108", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Amy"));
        rooms.add(advanceTo("109", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Ben"));
        rooms.add(advanceTo("110", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Clean_In_Progress, "Staff: Cara"));
        
        // --- Rooms 111-114: VIP, inspected ---
        rooms.add(advanceTo("111", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("112", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("113", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        rooms.add(advanceTo("114", RoomTypeUtil.VIP_Room, RoomStatusUtil.Inspected, "Passed inspection"));
        
        // --- Rooms 115-118: Ready for Check-In ---
        rooms.add(advanceTo("115", RoomTypeUtil.Normal_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("116", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("117", RoomTypeUtil.Deluxe_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        rooms.add(advanceTo("118", RoomTypeUtil.VIP_Room, RoomStatusUtil.Ready_For_CheckIN, "Ready"));
        
        // --- Rooms 119-120: VIP, mid-clean guest requested late check-out ---
        // shows the rollback + interrupt (splice) behaviour: room was inspected,
        // supervisor rolled it back to cleaning, then guest asked to hold the room.
        rooms.add(lateCheckoutRoom("119", RoomTypeUtil.VIP_Room));
        rooms.add(lateCheckoutRoom("120", RoomTypeUtil.VIP_Room));
        
        RoomDao dao = new RoomDao();
        dao.saveToFile(rooms);
        
        System.out.println(rooms.getNumberOfEntries() + "Room saved!");
    }
    
    
    // Fresh room, only the initial "Dirty" status entry
    private static Room newRoom (String roomNum, int roomType){
        Room room = new Room(roomNum , roomType);
        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty,"Room registered!"));
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
    
    // Room advanced forward from Dirty up to the given status
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
    // Save Room Data
    //====================================================
    public void saveToFile(DoublyLinkedList.ArrayList<Room> roomList) {
 
        File file = new File(fileName);
 
        try {
 
            ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));
 
            ooStream.writeObject(roomList);
 
            ooStream.close();
 
            System.out.println("\nRoom Database Saved Successfully!");
 
        } catch (FileNotFoundException ex) {
 
            System.out.println("\nFile not found.");
 
        } catch (IOException ex) {
 
            System.out.println("\nCannot save room database.");
        }
    }
 
    //====================================================
    // Load Room Data
    //====================================================
    public DoublyLinkedList.ArrayList<Room> retrieveFromFile() {
 
        File file = new File(fileName);
 
        DoublyLinkedList.ArrayList<Room> roomList = new DoublyLinkedList.ArrayList<>();
 
        try {
 
            ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));
 
            roomList = (DoublyLinkedList.ArrayList<Room>) oiStream.readObject();
 
            oiStream.close();
 
        } catch (FileNotFoundException ex) {
 
            System.out.println("\nNo Room Database Found.");
 
        } catch (IOException ex) {
 
            System.out.println("\nCannot read room database.");
 
        } catch (ClassNotFoundException ex) {
 
            System.out.println("\nClass not found.");
        }
 
        return roomList;
    }
}
