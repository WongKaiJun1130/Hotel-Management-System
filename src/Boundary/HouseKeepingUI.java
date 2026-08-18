package Boundary;
/*
 *
 * @author Kah Shun
 */

import Entity.Room;
import Entity.StatusEntry;
import Entity.Booking;
import Utility.Navigation;
import Utility.Utility;
import Utility.RoomStatusUtil;
import Utility.RoomTypeUtil;
import Control.HousekeepingControl;
import Control.HousekeepingControl.RoomBookingMatch;
import Control.BookingControl;
import Adt.ListInterface;

import java.util.Scanner;
import java.util.Iterator;


public class HouseKeepingUI {
    
    private static Scanner input = new Scanner(System.in);
    private static final int BOX_WIDTH = 52;
    
    // ==========================
    // Main Housekeeping Menu
    // ==========================
    public static void menu() {
        
        HousekeepingControl.loadDummyRooms();

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
            "7. Search Rooms",
            "8. Sort Rooms",
            "0. Back to Main Menu"
        };

    Runnable[] actions = {
            () -> showRoomStatus(),
            () -> manageRoomStatus(),
            () -> guestCheckOut(),
            () -> showRoomNeedCleaning(),
            () -> reportStatusHistory(),
            () -> reportStatusSummary(),
            () -> searchRoomsUI(),
            () -> sortRoomsUI(),
            () -> Navigation.stack.pop()
        };

    Utility.customMenu(
            options,
            "Housekeeping Menu",
            "Enter your choice: ",
            actions
    );

};
    // design 
    private static String border() {
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < BOX_WIDTH - 2; i++) {
            sb.append("-");
        }
        sb.append("+");
        return sb.toString();
    }
    
    private static String centered(String text) {
        int innerWidth = BOX_WIDTH - 2;
        int totalPadding = innerWidth - text.length();
        int left = Math.max(totalPadding / 2, 0);
        int right = Math.max(totalPadding - left, 0);
 
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < left; i++) {
            sb.append(" ");
        }
        sb.append(text);
        for (int i = 0; i < right; i++) {
            sb.append(" ");
        }
        sb.append("|");
        return sb.toString();
    }
 
    private static void printHeader(String title) {
        System.out.println();
        System.out.println(border());
        System.out.println(centered(title));
        System.out.println(border());
    }
 
    private static void printFooter() {
        System.out.println(border());
    }

    // Prints a simple proportional ASCII bar chart - a console app
    // can't render an actual image, but a bar chart made of repeated
    // characters, scaled against the largest value, gives the same
    // at-a-glance comparison a real bar chart would.
    private static void printBarChart(String title, String[] labels, int[] values) {
        System.out.println();
        System.out.println("  " + title + ":");

        int max = 0;
        for (int value : values) {
            max = Math.max(max, value);
        }
        if (max == 0) {
            max = 1; // avoid divide-by-zero when every count is 0
        }

        int labelWidth = 0;
        for (String label : labels) {
            labelWidth = Math.max(labelWidth, label.length());
        }

        final int maxBarWidth = 30;

        for (int i = 0; i < labels.length; i++) {
            int barLength = (int) Math.round((values[i] / (double) max) * maxBarWidth);

            StringBuilder bar = new StringBuilder();
            for (int b = 0; b < barLength; b++) {
                bar.append('#');
            }

            System.out.printf("  %-" + labelWidth + "s | %-" + maxBarWidth + "s %d%n",
                    labels[i], bar.toString(), values[i]);
        }
    }
    
    // 1. show room status //
    private static void showRoomStatus(){
        
        printHeader("ROOM STATUS OVERVIEW");
 
        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("  No rooms registered yet.");
            printFooter();
            return;
        }
        
        printRoomTable(HousekeepingControl.roomList);
 
        printFooter();
    }

    // Shared table printer - Room / Type / Status columns, reused by
    // Show Room Status, Search Rooms, and Sort Rooms so they all look
    // and behave consistently.
    private static void printRoomTable(ListInterface<Room> rooms) {
        System.out.printf("  %-8s %-14s %-20s%n", "Room", "Type", "Status");
        System.out.printf("  %-8s %-14s %-20s%n", "------", "------------", "------------------");

        Iterator<Room> iterator = rooms.getIterator();
        while (iterator.hasNext()) {
            Room room = iterator.next();
            StatusEntry current = room.getStatusHistory().getCurrentData();
            System.out.printf("  %-8s %-14s %-20s%n",
                    room.getRoomNum(),
                    RoomTypeUtil.roomTypeName(room.getRoomType()),
                    RoomStatusUtil.statusName(current.getStatusCode()));
        }
    }

    // ==============================
    // 7. Search Rooms
    // ==============================
    private static void searchRoomsUI() {
        System.out.print("Enter search keyword (room number, type, or status): ");
        String keyword = input.nextLine().trim();

        ListInterface<Room> results = HousekeepingControl.searchRooms(keyword);

        printHeader("SEARCH RESULTS: \"" + keyword + "\"");

        if (results.isEmpty()) {
            System.out.println("  No rooms matched.");
        } else {
            printRoomTable(results);
        }

        printFooter();
    }

    // ==============================
    // 8. Sort Rooms
    // ==============================
    private static void sortRoomsUI() {
        String[] options = {
            "1. By Room Number",
            "2. By Status",
            "3. By Room Type",
            "0. Back"
        };

        Runnable[] actions = {
            () -> displaySortedRooms(HousekeepingControl.getRoomsSortedByRoomNumber(), "SORTED BY ROOM NUMBER"),
            () -> displaySortedRooms(HousekeepingControl.getRoomsSortedByStatus(), "SORTED BY STATUS"),
            () -> displaySortedRooms(HousekeepingControl.getRoomsSortedByType(), "SORTED BY ROOM TYPE"),
            () -> {}
        };

        Utility.customMenu(
                options,
                "Sort Rooms",
                "Enter your choice: ",
                actions
        );
    }

    private static void displaySortedRooms(ListInterface<Room> sortedRooms, String title) {
        printHeader(title);

        if (sortedRooms.isEmpty()) {
            System.out.println("  No rooms registered yet.");
        } else {
            printRoomTable(sortedRooms);
        }

        printFooter();
    }
    
    
    // 3. Check-Out
    private static void guestCheckOut() {
        System.out.print("Enter Room Number: ");
        String roomNum = input.nextLine().trim();
    
        Room room = HousekeepingControl.findRoom(roomNum);
    
        if(room == null){
            System.out.println("Room " + roomNum + " not found.");
            return;
        }
    
        boolean success = HousekeepingControl.guestCheckOut(room);
        if(success){
            System.out.println("Room " + room.getRoomNum() + " checked out. Status updated to: Dirty (needs cleaning).");
        }
        else{
            System.out.println("Room " + room.getRoomNum() + " is already marked Dirty (needs cleaning).");
        }
    }
    
    // 4. need cleaning
    
    private static void showRoomNeedCleaning(){
        
         printHeader("ROOMS NEEDING CLEANING");
        
        if(HousekeepingControl.roomIsEmpty()){
            System.out.println("  No rooms registered yet.");
            printFooter();
            return;
        }
        
        boolean found = false;
        System.out.printf("  %-8s %-14s%n", "Room", "Type");
        System.out.printf("  %-8s %-14s%n", "------", "------------");
        
        for(int i = 1 ; i <= HousekeepingControl.getRoomCount() ; i++) {
            Room room = HousekeepingControl.getRoomAt(i);
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            if(status == RoomStatusUtil.Dirty){
                System.out.println("Room " + room.getRoomNum() + " [" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]");
                found = true;
            }
        }
        
        if (!found){
            System.out.println("  No rooms currently need cleaning.");
        }
        
        printFooter();
    }
    
    
    // 2. manage room status
    private static void manageRoomStatus(){
        
        Room room = selectOrRegisterRoom();
        if(room == null){
            return;
        }
        showCurrentRoomState(room);
        
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
    
    private static void showCurrentRoomState(Room room) {
        StatusEntry current = room.getStatusHistory().getCurrentData();
        System.out.println();
        System.out.println("  Room " + room.getRoomNum()
                + " [" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]"
                + "  |  Current Status: " + RoomStatusUtil.statusName(current.getStatusCode()));
    }
    
    private static Room selectOrRegisterRoom(){
        System.out.print("Enter Room Number: ");
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
        System.out.print("Note (optional): ");
        String note = input.nextLine().trim();
        
        int nextStatus = HousekeepingControl.advanceStatus(room , note);
        if(nextStatus == -1){
            System.out.println("Room " + room.getRoomNum() + " has no automatic next status from here.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " updated to: " + RoomStatusUtil.statusName(nextStatus));
        }
        showCurrentRoomState(room);
    }
    
    
    // Rollback: supervisor logged the wrong status
    
    private static void rollbackStatus(Room room) {
        int restoredStatus = HousekeepingControl.rollbackStatus(room);
        if (restoredStatus == -1) {
            System.out.println("Room " + room.getRoomNum() + ": already at earliest status, cannot roll back.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " rolled back to: " + RoomStatusUtil.statusName(restoredStatus));
        }
        showCurrentRoomState(room);
    }
    
    // ------------------------------
    // Interrupt: guest requests late check-out mid-cleaning
    // ------------------------------
    private static void interruptForLateCheckOut(Room room) {
        System.out.print("Note (e.g. requested check-out time): ");
        String note = input.nextLine().trim();
 
        HousekeepingControl.interruptForLateCheckout(room, note);
        System.out.println("Room " + room.getRoomNum() + " placed on hold: " + note);
        showCurrentRoomState(room);
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
        showCurrentRoomState(room);
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
 
        printHeader("HISTORY - ROOM " + room.getRoomNum() + " (" + RoomTypeUtil.roomTypeName(room.getRoomType()) + ")");
        
        StatusEntry current = room.getStatusHistory().getCurrentData();
        Iterator<StatusEntry> iterator = room.getStatusHistory().getIterator();
 
        int step = 1 ;
        while (iterator.hasNext()) {
            StatusEntry entry = iterator.next();
            String marker = (entry == current) ? "  <-- CURRENT" : "";
            String note = entry.getNote().isEmpty() ? "" : " (" + entry.getNote() + ")";
            System.out.printf("  %d. %-20s%s%s%n", step, RoomStatusUtil.statusName(entry.getStatusCode()), note, marker);
            step++;
        }

        // Cross-module: show waiting bookings (Booking module) that
        // could be assigned to this room, based on matching room type.
        BookingControl bookingControl = new BookingControl();
        ListInterface<Booking> waitingMatches = HousekeepingControl.getWaitingBookingsForRoom(room, bookingControl);

        System.out.println();
        if (waitingMatches.isEmpty()) {
            System.out.println("  No waiting bookings currently match this room's type.");
        } else {
            System.out.println("  Waiting bookings for this room type:");
            Iterator<Booking> bookingIterator = waitingMatches.getIterator();
            while (bookingIterator.hasNext()) {
                Booking booking = bookingIterator.next();
                System.out.println("    " + booking.getBookingID() + " - " + booking.getGuestName()
                        + " (wants " + booking.getRoomType() + ")");
            }
        }

        printFooter();
    }
    
    // ==============================
    // 6. Report: Status Summary Across All Rooms
    // ==============================
    private static void reportStatusSummary() {
        
        printHeader("ROOM STATUS SUMMARY REPORT");
 
        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("No rooms registered yet.");
            printFooter();
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

        printBarChart("Status Chart",
                new String[]{"Dirty", "Cleaning", "Inspected", "Ready", "Hold"},
                new int[]{
                    statusCounts[RoomStatusUtil.Dirty],
                    statusCounts[RoomStatusUtil.Clean_In_Progress],
                    statusCounts[RoomStatusUtil.Inspected],
                    statusCounts[RoomStatusUtil.Ready_For_CheckIN],
                    statusCounts[RoomStatusUtil.Late_CheckOut_Hold]
                });

        printBarChart("Type Chart",
                new String[]{"Normal", "Deluxe", "VIP"},
                new int[]{
                    typeCounts[RoomTypeUtil.Normal_Room],
                    typeCounts[RoomTypeUtil.Deluxe_Room],
                    typeCounts[RoomTypeUtil.VIP_Room]
                });

        // Cross-module: pull waiting-booking stats from the Booking
        // module and show how many ready rooms actually have a match.
        BookingControl bookingControl = new BookingControl();
        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();
        ListInterface<HousekeepingControl.RoomBookingMatch> readyMatches =
                HousekeepingControl.getReadyRoomsForWaitingGuests(bookingControl);

        System.out.println();
        System.out.println("Cross-Module (Booking):");
        System.out.println("Total Waiting Bookings : " + waitingBookings.getSize());
        System.out.println("Ready Rooms w/ a Match : " + readyMatches.getSize());
        
        printFooter();
    }

}