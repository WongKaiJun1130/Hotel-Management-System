/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Control;

import System_adt.DoublyLinkedList;
import System_Entity.StatusEntry;
import System_Utility.RoomStatusUtil;
import System_Entity.Room;

/**
 *
 * @author USER
 */
public class HousekeepingControl {
    
    public static DoublyLinkedList.ArrayList<Room> rooms = new DoublyLinkedList.ArrayList<>();
    
     // Room register
    
    public static Room registerRoom(String roomNum,  int roomType){
        Room room = new Room(roomNum , roomType);
        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty,"Room Registered"));
        rooms.add(room);
        return room;
    }
    
    public static Room findRoom(String roomNum){
        for(int i = 1 ; i <= rooms.getNumberOfEntries() ; i++){
            Room room = rooms.getEntry(i);
            if(room.getRoomNum().equalsIgnoreCase(roomNum)){
                return room;
            }
        }
        return null;
    }
    
    public static boolean roomIsEmpty(){
        return rooms.isEmpty();
    }
    
    public static int getRoomCount(){
        return rooms.getNumberOfEntries();
    }
    
    public static Room getRoomAt(int index){
        return rooms.getEntry(index);
    }
    
    
    // Status transitions
    public static int advanceStatus(Room room , String node){
        StatusEntry current = room.getStatusHistory().getCurrentData();
        int nextStatus = RoomStatusUtil.nextStatusAfter(current.getStatusCode());
        
        if(nextStatus == -1){
            return -1;
        }
        
        room.getStatusHistory().insertAndAdvance(new StatusEntry(nextStatus , node));
        return nextStatus;
    }
    
    // Supervisor logged the wrong status. Returns the restored status
    // code, or -1 if already at the earliest entry
    
    public static int rollbackStatus(Room room){
        StatusEntry restored = room.getStatusHistory().rollback();
        return (restored == null) ?-1 : restored.getStatusCode();
    }
    
    // Guest requests late check-out mid-cleaning: splice a hold entry in
    // without discarding whatever cleaning step was queued next
    public static void interruptForLateCheckout(Room room , String note){
        room.getStatusHistory().spliceAfterCurrent(new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold,note));
    }
    
    // Continue the cleaning flow after the interruption is resolved
    // Returns the resumed status code, or -1 if nothing was queued
    public static int resumeStatus(Room room){
        StatusEntry resumed = room.getStatusHistory().redo();
        return (resumed == null) ?-1 : resumed.getStatusCode();
    }
    
    // Guest checks out: room goes back to Dirty, ready to be re-cleaned.
    // Returns false if the room was already Dirty
    public static boolean guestCheckOut(Room room){
        int currentStatuc = room.getStatusHistory().getCurrentData().getStatusCode();
        if(currentStatuc == RoomStatusUtil.Dirty){
            return false;
        }
        
        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Guest checked out - needs cleaning"));
        return true;
    }
    
    
    // Queries for reports
    
    // Index matches RoomStatusUtil status codes: Dirty, Clean_In_Progress,
    // Inspected, Ready_For_CheckIN, Late_CheckOut_Hold
    
    public static int[] getStatusCounts(){
        int[] counts = new int[5];
        for(int i = 1 ; i <= rooms.getNumberOfEntries() ; i++){
            int status = rooms.getEntry(i).getStatusHistory().getCurrentData().getStatusCode();
            if(status >= 0 && status < counts.length){
                counts[status]++;
            }
        }
        return counts;
    }
    
    // Index matches RoomTypeUtil type codes: Normal_Room, Deluxe_Room, VIP_Room
    
    public static int[] getTypeCounts(){
        int[] counts = new int[3];
        for (int i = 1; i <= rooms.getNumberOfEntries(); i++) {
        int type = rooms.getEntry(i).getRoomType();
        if (type >= 0 && type <= counts.length){
            counts[type]++;
                }
        }
        return counts;
    }
}
