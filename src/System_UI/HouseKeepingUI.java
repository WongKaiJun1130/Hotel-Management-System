package System_UI;

import System_Entity.Room;
import System_Entity.StatusEntry;
import System_Utility.Navigation;
import System_Utility.Utility;
import System_Utility.RoomStatusUtil;
import System_Utility.RoomTypeUtil;
import System_Control.HousekeepingControl;

import java.util.Scanner;
import java.util.Iterator;


public class HouseKeepingUI {
    
    private static Scanner input = new Scanner(System.in);
    
    // ==========================
    // Main Housekeeping Menu
    // ==========================
    public static void menu() {
        
        HousekeepingControl.loadRoomDatabase();

        Navigation.stack.push(HouseKeepingMenu);

        boolean exit = false;

        while (!Navigation.stack.isEmpty() && !exit) {

            Runnable currentMenu = Navigation.stack.peek();

            if (currentMenu != null) {
                currentMenu.run();
            } else {
                exit = true;
            }
        }

        System.out.println("Returning to Main Menu...");
    }

    private static final Runnable HouseKeepingMenu = () -> {

    String[] options = {
            "1. Show Room Status",
            "2. Manage Room Status",
            "3. Guest Check-Out",
            "4. Rooms Needing Cleaning",
            "5. Report: Room Status History",
            "6. Report: Room Status Summary",
            "0. Back to Main Menu"
        };

    Runnable[] actions = {
            () -> showRoomStatus(),
            () -> manageRoomStatus(),
            () -> guestCheckOut(),
            () -> showRoomNeedCleaning(),
            () -> reportStatusHistory(),
            () -> reportStatusSummary(),
            () -> Navigation.stack.pop()
        };

    Utility.customMenu(
            options,
            "Housekeeping Menu",
            "Enter your choice: ",
            actions
    );

};
    
    // 1. show room status //
    private static void showRoomStatus(){
        
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              ROOM STATUS OVERVIEW            |");
        System.out.println("+----------------------------------------------+");
        
        if(HousekeepingControl.roomIsEmpty()){
            System.out.println("No room registered yet");
            return;
        }
        
        for(int i = 1; i <= HousekeepingControl.getRoomCount() ; i++){
        
       Room room = HousekeepingControl.getRoomAt(i);
       StatusEntry current = room.getStatusHistory().getCurrentData();
       System.out.print("Room" + room.getRoomNum() + "[" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]" + RoomStatusUtil.statusName(current.getStatusCode()));
        }
    }
    
    // 3. Check-Out
    
    private static void guestCheckOut() {
        System.out.print("Enter Room Number :");
        String roomNum = input.nextLine().trim();
    
        Room room = HousekeepingControl.findRoom(roomNum);
    
        if(roomNum == null){
            System.out.println("Room" + roomNum +" not found");
            return;
        }
    
        boolean success = HousekeepingControl.guestCheckOut(room);
        if(success){
            System.out.println("Room " + room.getRoomNum() + "Checked out.");
        }
        else{
            System.out.println("Room " + room.getRoomNum() + "already marked Dirty");
        }
    }
    
    // 4. need cleaning
    
    private static void showRoomNeedCleaning(){
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|           ROOMS NEEDING CLEANING              |");
        System.out.println("+----------------------------------------------+");
        
        if(HousekeepingControl.roomIsEmpty()){
            System.out.println("No room registered yet");
            return;
        }
        
        boolean found = false;
        for(int i = 1 ; i <= HousekeepingControl.getRoomCount() ; i++) {
            Room room = HousekeepingControl.getRoomAt(i);
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            if(status == RoomStatusUtil.Dirty){
                System.out.println("Room " + room.getRoomNum() + "[" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]");
                found = true;
            }
        }
        
        if (!found){
            System.out.println("No Room Need Cleaning");
        }
    }
    
    // 2. manage room status
    
    private static void manageRoomStatus(){
        
        Room room = selectOrRegisterRoom();
        if(room == null){
            return;
        }
        String[] options = {
            "1. Advance to Next Status",
            "2. Correct Mistake (Rollback)",
            "3. Guest Requests Late Check-Out (Interrupt)",
            "4. Resume Cleaning (After Late Check-Out Resolved)",
            "0. Back"
        };
        
        Runnable[] actions = {
            () -> advanceStatus(room),
            () -> rollbackStatus(room),
            () -> interruptForLateCheckOut(room),
            () -> resumeStatus(room),
            () -> {}
        };
        
        Utility.customMenu(
                options,
                "Manage Room " + room.getRoomNum(),
                "Enter your choice: ",
                actions
        );
    }
    
    private static Room selectOrRegisterRoom(){
        System.out.print("Enter Room Number");
        String roomNum = input.nextLine().trim();
        
        Room room = HousekeepingControl.findRoom(roomNum);
        if( room != null){
            return room;
        }
        
        System.out.println("Room " + roomNum + " not found.");
        System.out.print("Register this room now? (Y/N): ");
        String confirm = input.nextLine().trim();
 
        if (confirm.equalsIgnoreCase("Y")) {
            int roomType = selectRoomType();
            Room newRoom = HousekeepingControl.registerRoom(roomNum, roomType);
            System.out.println("Room " + roomNum + " (" + RoomTypeUtil.roomTypeName(roomType) + ") registered with status: Dirty");
            return newRoom;
        }
 
        return null;
    }
    
    private static int selectRoomType() {
        System.out.println("Select Room Type:");
        System.out.println("1. Normal Room");
        System.out.println("2. Deluxe Room");
        System.out.println("3. VIP Room");
        System.out.print("Enter choice: ");
        String choice = input.nextLine().trim();
 
        switch (choice) {
            case "1": return RoomTypeUtil.Normal_Room;
            case "2": return RoomTypeUtil.Deluxe_Room;
            case "3": return RoomTypeUtil.VIP_Room;
            default:
                System.out.println("Invalid choice, defaulting to Normal Room.");
                return RoomTypeUtil.Normal_Room;
        }
    }
    
    // advance status foward
    
    private static void advanceStatus(Room room){
        System.out.print("Note (optional)");
        String note = input.nextLine().trim();
        
        int nextStatus = HousekeepingControl.advanceStatus(room , note);
        if(nextStatus == -1){
            System.out.println("Room" + room.getRoomNum() + "has no automatic next status from here");
        } else {
            System.out.println("Room" + room.getRoomNum() + "rolled back to: " + RoomStatusUtil.statusName(nextStatus));
        }
    }
    
    
    // Rollback: supervisor logged the wrong status
    
    private static void rollbackStatus(Room room) {
        int restoredStatus = HousekeepingControl.rollbackStatus(room);
        if (restoredStatus == -1) {
            System.out.println("Room " + room.getRoomNum() + ": already at earliest status, cannot roll back.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " rolled back to: " + RoomStatusUtil.statusName(restoredStatus));
        }
    }
    
    // ------------------------------
    // Interrupt: guest requests late check-out mid-cleaning
    // ------------------------------
    private static void interruptForLateCheckOut(Room room) {
        System.out.print("Note (e.g. requested check-out time): ");
        String note = input.nextLine().trim();
 
        HousekeepingControl.interruptForLateCheckout(room, note);
        System.out.println("Room " + room.getRoomNum() + " placed on hold: " + note);
    }
    
    // ------------------------------
    // Resume: continue cleaning after the late check-out is resolved
    // ------------------------------
    private static void resumeStatus(Room room) {
        int resumedStatus = HousekeepingControl.resumeStatus(room);
        if (resumedStatus == -1) {
            System.out.println("Room " + room.getRoomNum() + ": no queued status to resume.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " resumed to: " + RoomStatusUtil.statusName(resumedStatus));
        }
    }
 
    // ==============================
    // 5. Report: Full Status History for a Room
    // ==============================
    private static void reportStatusHistory() {
        System.out.print("Enter Room Number: ");
        String roomNum = input.nextLine().trim();
 
        Room room = HousekeepingControl.findRoom(roomNum);
        if (room == null) {
            System.out.println("Room " + roomNum + " not found.");
            return;
        }
 
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|   STATUS HISTORY - ROOM " + room.getRoomNum()
                + " (" + RoomTypeUtil.roomTypeName(room.getRoomType()) + ")");
        System.out.println("+----------------------------------------------+");
 
        StatusEntry current = room.getStatusHistory().getCurrentData();
        Iterator<StatusEntry> iterator = room.getStatusHistory().getIterator();
 
        while (iterator.hasNext()) {
            StatusEntry entry = iterator.next();
            String marker = (entry == current) ? "  <-- CURRENT" : "";
            String note = entry.getNote().isEmpty() ? "" : " (" + entry.getNote() + ")";
            System.out.println(RoomStatusUtil.statusName(entry.getStatusCode()) + note + marker);
        }
    }
    
    // ==============================
    // 6. Report: Status Summary Across All Rooms
    // ==============================
    private static void reportStatusSummary() {
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|          ROOM STATUS SUMMARY REPORT           |");
        System.out.println("+----------------------------------------------+");
 
        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("No rooms registered yet.");
            return;
        }
 
        int[] statusCounts = HousekeepingControl.getStatusCounts();
        int[] typeCounts = HousekeepingControl.getTypeCounts();
 
        System.out.println("Dirty                 : " + statusCounts[RoomStatusUtil.Dirty]);
        System.out.println("Cleaning In Progress   : " + statusCounts[RoomStatusUtil.Clean_In_Progress]);
        System.out.println("Inspected              : " + statusCounts[RoomStatusUtil.Inspected]);
        System.out.println("Ready for Check-In     : " + statusCounts[RoomStatusUtil.Ready_For_CheckIN]);
        System.out.println("Late Check-Out Hold    : " + statusCounts[RoomStatusUtil.Late_CheckOut_Hold]);
        System.out.println("Total Rooms            : " + HousekeepingControl.getRoomCount());
 
        System.out.println();
        System.out.println("Rooms by Type:");
        System.out.println("Normal Room            : " + typeCounts[RoomTypeUtil.Normal_Room]);
        System.out.println("Deluxe Room            : " + typeCounts[RoomTypeUtil.Deluxe_Room]);
        System.out.println("VIP Room               : " + typeCounts[RoomTypeUtil.VIP_Room]);
    }
    
}
    
