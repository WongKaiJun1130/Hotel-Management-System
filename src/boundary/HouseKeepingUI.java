package boundary;
/*
 *
 * @author Kah Shun
 */

import entity.Room;
import entity.StatusEntry;
import entity.Booking;
import utility.Navigation;
import utility.Utility;
import utility.InputUtility;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import control.HousekeepingControl;
import control.HousekeepingControl.RoomBookingMatch;
import control.BookingControl;
import adt.ListInterface;

import java.util.Iterator;


public class HouseKeepingUI {
    
    private static final int BOX_WIDTH = 52;
    
    // ==========================
    // Main Housekeeping Menu
    // ==========================
    public static void menu() {
        
        HousekeepingControl.loadDummyRooms();
        HousekeepingControl.startAutoAdvanceScheduler();

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
            "3. Guest Check-Out ",
            "4. Cleaning Queue (Dirty + In Progress)",
            "5. Report: Room Status History",
            "6. Report: Room Status Summary",
            "7. Filter Rooms (Multi-Criteria)",
            "8. Report: Business Cycle (Filter + Sort)",
            "0. Back to Main Menu"
        };

    Runnable[] actions = {
            () -> showRoomStatusMenu(),
            () -> manageRoomStatus(),
            () -> guestCheckOut(),
            () -> showRoomsCleanInProgress(),
            () -> reportStatusHistory(),
            () -> reportStatusSummary(),
            () -> filterRoomsUI(),
            () -> businessCycleReportUI(),
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
    private static void showRoomStatusMenu() {

        boolean resume;
        do {
            resume = false;
            InputUtility.clearScreen();

            // Room table and the Search/Sort menu are shown together on
            // the same screen - no separate "press enter to continue"
            // step in between.
            showRoomStatus();

            System.out.println(Utility.MENU_BORDER);
            Utility.printBoxTitle("Show Room Status");
            System.out.println(Utility.MENU_BORDER);
            Utility.printMenuItem("1. Search Rooms");
            Utility.printMenuItem("2. Sort Rooms");
            Utility.printMenuItem("0. Back");
            System.out.println(Utility.MENU_BORDER);
            System.out.print("Enter your choice: ");

            String input = InputUtility.getStringInput().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                InputUtility.pressEnterToContinue();
                resume = true;
                continue;
            }

            switch (choice) {
                case 1:
                    searchRoomsUI();
                    resume = true;
                    break;
                case 2:
                    sortRoomsUI();
                    resume = true;
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice!");
                    InputUtility.pressEnterToContinue();
                    resume = true;
            }
        } while (resume);
    }

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
    // Search Rooms (under Show Room Status)
    // ==============================
    private static void searchRoomsUI() {
        System.out.print("Enter search keyword (room number, type, or status): ");
        String keyword = InputUtility.getStringInput().trim();

        ListInterface<Room> results = HousekeepingControl.searchRooms(keyword);

        printHeader("SEARCH RESULTS: \"" + keyword + "\"");

        if (results.isEmpty()) {
            System.out.println("  No rooms matched.");
        } else {
            printRoomTable(results);
        }

        printFooter();
        InputUtility.pressEnterToContinue();
    }

    // ==============================
    // Sort Rooms (under Show Room Status)
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
        InputUtility.pressEnterToContinue();
    }


    // ==============================
    // Shared filter-criteria prompts
    // (used by both Filter Rooms and the Business Cycle Report)
    // ==============================

    // Returns null for "any type" (no filter on this field).
    private static Integer promptRoomTypeFilter() {
        System.out.println("Filter by Room Type:");
        System.out.println("  1. Single Room");
        System.out.println("  2. Medium Room");
        System.out.println("  3. Large Room");
        System.out.println("  0. Any (no filter)");
        System.out.print("Enter choice: ");
        String choice = InputUtility.getStringInput().trim();

        switch (choice) {
            case "1": return RoomTypeUtil.Single_Room;
            case "2": return RoomTypeUtil.Medium_Room;
            case "3": return RoomTypeUtil.Large_Room;
            default: return null;
        }
    }

    // Returns null for "any status" (no filter on this field).
    private static Integer promptStatusFilter() {
        System.out.println("Filter by Status:");
        System.out.println("  1. Dirty");
        System.out.println("  2. Cleaning In Progress");
        System.out.println("  3. Inspected");
        System.out.println("  4. Ready For Check-In");
        System.out.println("  5. Late Check-Out Hold");
        System.out.println("  0. Any (no filter)");
        System.out.print("Enter choice: ");
        String choice = InputUtility.getStringInput().trim();

        switch (choice) {
            case "1": return RoomStatusUtil.Dirty;
            case "2": return RoomStatusUtil.Clean_In_Progress;
            case "3": return RoomStatusUtil.Inspected;
            case "4": return RoomStatusUtil.Ready_For_CheckIN;
            case "5": return RoomStatusUtil.Late_CheckOut_Hold;
            default: return null;
        }
    }

    // 1 = by room number, 2 = by status, 3 = by room type
    private static int promptSortOption() {
        System.out.println("Sort By:");
        System.out.println("  1. Room Number");
        System.out.println("  2. Status");
        System.out.println("  3. Room Type");
        System.out.print("Enter choice: ");
        String choice = InputUtility.getStringInput().trim();

        switch (choice) {
            case "2": return 2;
            case "3": return 3;
            default: return 1;
        }
    }

    private static String sortLabel(int sortOption) {
        switch (sortOption) {
            case 2: return "Status";
            case 3: return "Room Type";
            default: return "Room Number";
        }
    }


    // ==============================
    // 7. Filter Rooms (Multi-Criteria)
    // ==============================
    // Structured filter, distinct from keyword Search: lets the user
    // narrow rooms by room type AND/OR status at the same time, e.g.
    // "all Dirty VIP rooms" - a single keyword search can't express an
    // exact combined condition like that.
    private static void filterRoomsUI() {
        printHeader("FILTER ROOMS");

        Integer typeFilter = promptRoomTypeFilter();
        Integer statusFilter = promptStatusFilter();

        ListInterface<Room> results = HousekeepingControl.filterRooms(typeFilter, statusFilter);

        System.out.println();
        System.out.println("  Type Filter   : " + (typeFilter == null ? "Any" : RoomTypeUtil.roomTypeName(typeFilter)));
        System.out.println("  Status Filter : " + (statusFilter == null ? "Any" : RoomStatusUtil.statusName(statusFilter)));
        System.out.println();

        if (results.isEmpty()) {
            System.out.println("  No rooms matched the selected filters.");
        } else {
            printRoomTable(results);
            System.out.println();
            System.out.println("  " + results.getSize() + " room(s) matched.");
        }

        printFooter();
        InputUtility.pressEnterToContinue();
    }


    // ==============================
    // 8. Report: Business Cycle (Filter + Sort combined)
    // ==============================
    // The end-of-cycle operational report: filters the room registry by
    // whatever criteria the user picks, sorts that filtered subset, and
    // renders it as a structured table with a breakdown summary and a
    // cross-module (Room <-> Booking) figure underneath - filter, sort,
    // and reporting all chained into one pipeline via
    // HousekeepingControl.generateBusinessCycleReport().
    private static void businessCycleReportUI() {
        printHeader("BUSINESS CYCLE REPORT - SETUP");

        Integer typeFilter = promptRoomTypeFilter();
        Integer statusFilter = promptStatusFilter();
        int sortOption = promptSortOption();

        ListInterface<Room> report = HousekeepingControl.generateBusinessCycleReport(typeFilter, statusFilter, sortOption);

        printHeader("BUSINESS CYCLE REPORT");
        System.out.println("  Type Filter   : " + (typeFilter == null ? "Any" : RoomTypeUtil.roomTypeName(typeFilter)));
        System.out.println("  Status Filter : " + (statusFilter == null ? "Any" : RoomStatusUtil.statusName(statusFilter)));
        System.out.println("  Sorted By     : " + sortLabel(sortOption));
        System.out.println();

        if (report.isEmpty()) {
            System.out.println("  No rooms matched the selected filters.");
            printFooter();
            InputUtility.pressEnterToContinue();
            return;
        }

        printRoomTable(report);

        // Breakdown of the filtered + sorted set
        int[] statusBreakdown = new int[5];
        int[] typeBreakdown = new int[3];

        Iterator<Room> breakdownIterator = report.getIterator();
        while (breakdownIterator.hasNext()) {
            Room room = breakdownIterator.next();
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            statusBreakdown[status]++;
            typeBreakdown[room.getRoomType()]++;
        }

        System.out.println();
        System.out.println("  Summary of " + report.getSize() + " room(s):");
        System.out.println("    Status -> Dirty: " + statusBreakdown[RoomStatusUtil.Dirty]
                + "  Cleaning: " + statusBreakdown[RoomStatusUtil.Clean_In_Progress]
                + "  Inspected: " + statusBreakdown[RoomStatusUtil.Inspected]
                + "  Ready: " + statusBreakdown[RoomStatusUtil.Ready_For_CheckIN]
                + "  Hold: " + statusBreakdown[RoomStatusUtil.Late_CheckOut_Hold]);
        System.out.println("    Type   -> Single: " + typeBreakdown[RoomTypeUtil.Single_Room]
                + "  Medium: " + typeBreakdown[RoomTypeUtil.Medium_Room]
                + "  Large: " + typeBreakdown[RoomTypeUtil.Large_Room]);

        // Cross-module (Room <-> Booking): within this filtered/sorted
        // set, how many Ready rooms already have a matching waiting booking.
        BookingControl bookingControl = new BookingControl();
        int readyWithMatch = 0;

        Iterator<Room> matchIterator = report.getIterator();
        while (matchIterator.hasNext()) {
            Room room = matchIterator.next();
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            if (status == RoomStatusUtil.Ready_For_CheckIN) {
                ListInterface<Booking> matches = HousekeepingControl.getWaitingBookingsForRoom(room, bookingControl);
                if (!matches.isEmpty()) {
                    readyWithMatch++;
                }
            }
        }

        System.out.println();
        System.out.println("  Ready rooms in this set with a waiting-booking match: " + readyWithMatch);

        printFooter();
        InputUtility.pressEnterToContinue();
    }
    
    
    // 3. Check-Out
    private static void guestCheckOut() {
        System.out.print("Enter Room Number: ");
        String roomNum = InputUtility.getStringInput().trim();
    
        Room room = HousekeepingControl.findRoom(roomNum);
    
        if(room == null){
            System.out.println("Room " + roomNum + " not found.");
            InputUtility.pressEnterToContinue();
            return;
        }
    
        boolean success = HousekeepingControl.guestCheckOut(room);
        if(success){
            System.out.println("Room " + room.getRoomNum() + " checked out. Status updated to: Dirty (needs cleaning).");

            // Close out the matching Booking record too, so it stops
            // showing as "Served" once the guest has actually left.
            BookingControl bookingControl = new BookingControl();
            Booking closedBooking = bookingControl.checkOutBookingByRoomID(room.getRoomNum());
            if (closedBooking != null) {
                System.out.println("Booking " + closedBooking.getBookingID() + " (" + closedBooking.getGuestName()
                        + ") marked as Checked Out.");
            }
        }
        else{
            System.out.println("Room " + room.getRoomNum() + " is already marked Dirty (needs cleaning).");
        }
        InputUtility.pressEnterToContinue();
    }
    
    // 4. need 
    
    // 4. Rooms Clean In Progress
    private static void showRoomsCleanInProgress(){
        
         printHeader("HOUSEKEEPING CLEANING QUEUE");
        
        if(HousekeepingControl.roomIsEmpty()){
            System.out.println("  No rooms registered yet.");
            printFooter();
            InputUtility.pressEnterToContinue();
            return;
        }
        
        System.out.println("  Awaiting Cleaning (Dirty)");
        System.out.printf("  %-8s %-14s%n", "Room", "Type");
        System.out.printf("  %-8s %-14s%n", "------", "------------");
        boolean foundDirty = false;
        for(int i = 1 ; i <= HousekeepingControl.getRoomCount() ; i++) {
            Room room = HousekeepingControl.getRoomAt(i);
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            if(status == RoomStatusUtil.Dirty){
                System.out.println("  Room " + room.getRoomNum() + " [" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]");
                foundDirty = true;
            }
        }
        if (!foundDirty){
            System.out.println("  None.");
        }

        System.out.println();
        System.out.println("  Currently Clean In Progress");
        System.out.printf("  %-8s %-14s%n", "Room", "Type");
        System.out.printf("  %-8s %-14s%n", "------", "------------");
        boolean foundInProgress = false;
        for(int i = 1 ; i <= HousekeepingControl.getRoomCount() ; i++) {
            Room room = HousekeepingControl.getRoomAt(i);
            int status = room.getStatusHistory().getCurrentData().getStatusCode();
            if(status == RoomStatusUtil.Clean_In_Progress){
                System.out.println("  Room " + room.getRoomNum() + " [" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]");
                foundInProgress = true;
            }
        }
        if (!foundInProgress){
            System.out.println("  None.");
        }
        
        printFooter();
        InputUtility.pressEnterToContinue();
    }
    
    
    // 2. manage room status
    private static void manageRoomStatus(){
        
        Room room = selectOrRegisterRoom();
        if(room == null){
            return;
        }
        showCurrentRoomState(room);
        InputUtility.pressEnterToContinue();

        // Only offered while the room is actually Ready For Check-In -
        // there's nothing to assign otherwise.
        int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();
        boolean canAssignToBooking = (currentStatus == RoomStatusUtil.Ready_For_CheckIN);

        String[] options;
        Runnable[] actions;

        if (canAssignToBooking) {
            options = new String[]{
                "1. Advance to Next Status",
                "2. Correct Mistake (Rollback)",
                "3. Guest Requests Late Check-Out (Interrupt)",
                "4. Resume Cleaning (After Late Check-Out Resolved)",
                "5. Assign to Waiting Booking (Check-In)",
                "0. Back"
            };
            actions = new Runnable[]{
                () -> advanceStatus(room),
                () -> rollbackStatus(room),
                () -> interruptForLateCheckOut(room),
                () -> resumeStatus(room),
                () -> assignRoomToBooking(room),
                () -> {}
            };
        } else {
            options = new String[]{
                "1. Advance to Next Status",
                "2. Correct Mistake (Rollback)",
                "3. Guest Requests Late Check-Out (Interrupt)",
                "4. Resume Cleaning (After Late Check-Out Resolved)",
                "0. Back"
            };
            actions = new Runnable[]{
                () -> advanceStatus(room),
                () -> rollbackStatus(room),
                () -> interruptForLateCheckOut(room),
                () -> resumeStatus(room),
                () -> {}
            };
        }
        
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
        String roomNum = InputUtility.getStringInput().trim();
        
        Room room = HousekeepingControl.findRoom(roomNum);
        if( room != null){
            return room;
        }
        
        System.out.println("Room " + roomNum + " not found.");
        System.out.print("Register this room now? (Y/N): ");
        String confirm = InputUtility.getStringInput().trim();
 
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
        System.out.println("1. Single Room");
        System.out.println("2. Medium Room");
        System.out.println("3. Large Room");
        System.out.print("Enter choice: ");
        String choice = InputUtility.getStringInput().trim();
 
        switch (choice) {
            case "1": return RoomTypeUtil.Single_Room;
            case "2": return RoomTypeUtil.Medium_Room;
            case "3": return RoomTypeUtil.Large_Room;
            default:
                System.out.println("Invalid choice, defaulting to Normal Room.");
                return RoomTypeUtil.Single_Room;
        }
    }
    
    
    // advance status foward 
    private static void advanceStatus(Room room){
        System.out.print("Note (optional): ");
        String note = InputUtility.getStringInput().trim();
        
        int nextStatus = HousekeepingControl.advanceStatus(room , note);
        if(nextStatus == -1){
            System.out.println("Room " + room.getRoomNum() + " has no automatic next status from here.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " updated to: " + RoomStatusUtil.statusName(nextStatus));
        }
        showCurrentRoomState(room);
        InputUtility.pressEnterToContinue();
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
        InputUtility.pressEnterToContinue();
    }
    
    // ------------------------------
    // Interrupt: guest requests late check-out mid-cleaning
    // ------------------------------
    private static void interruptForLateCheckOut(Room room) {
        System.out.print("Note (e.g. requested check-out time): ");
        String note = InputUtility.getStringInput().trim();
 
        HousekeepingControl.interruptForLateCheckout(room, note);
        System.out.println("Room " + room.getRoomNum() + " placed on hold: " + note);
        showCurrentRoomState(room);
        InputUtility.pressEnterToContinue();
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
        InputUtility.pressEnterToContinue();
    }

    // ------------------------------
    // Assign to Waiting Booking: manually put a Ready-For-Check-In room
    // back into service by linking it to a guest still waiting for a
    // room of that type (Room <-> Booking dependency). Only valid while
    // the room is Ready For Check-In.
    // ------------------------------
    private static void assignRoomToBooking(Room room) {
        int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();

        if (currentStatus != RoomStatusUtil.Ready_For_CheckIN) {
            System.out.println("Room " + room.getRoomNum() + " must be Ready For Check-In to assign it to a booking. "
                    + "Current status: " + RoomStatusUtil.statusName(currentStatus));
            InputUtility.pressEnterToContinue();
            return;
        }

        BookingControl bookingControl = new BookingControl();
        ListInterface<Booking> matches = HousekeepingControl.getWaitingBookingsForRoom(room, bookingControl);

        if (matches.isEmpty()) {
            System.out.println("No waiting bookings currently match Room " + room.getRoomNum()
                    + "'s type (" + RoomTypeUtil.roomTypeName(room.getRoomType()) + ").");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println();
        System.out.println("  Waiting bookings for " + RoomTypeUtil.roomTypeName(room.getRoomType()) + ":");
        int index = 1;
        Iterator<Booking> iterator = matches.getIterator();
        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            System.out.println("    " + index + ". " + booking.getBookingID() + " - " + booking.getGuestName()
                    + " (wants " + booking.getRoomType() + ")");
            index++;
        }
        System.out.println("    0. Cancel");

        System.out.print("Select a booking to check in (number): ");
        int choice = InputUtility.getIntInput();

        if (choice <= 0 || choice > matches.getSize()) {
            System.out.println("Cancelled - no booking assigned.");
            InputUtility.pressEnterToContinue();
            return;
        }

        Booking chosen = matches.getEntry(choice);
        boolean success = bookingControl.checkInBooking(chosen, room.getRoomNum());

        if (success) {
            System.out.println();
            System.out.println("Room " + room.getRoomNum() + " assigned to booking " + chosen.getBookingID()
                    + " (" + chosen.getGuestName() + "). Guest checked in.");
        } else {
            System.out.println("Could not assign this booking - it may have already been processed. Please try again.");
        }

        showCurrentRoomState(room);
        InputUtility.pressEnterToContinue();
    }
 
    // ==============================
    // 5. Report: Full Status History for a Room
    // ==============================
    private static void reportStatusHistory() {
        System.out.print("Enter Room Number: ");
        String roomNum = InputUtility.getStringInput().trim();
 
        Room room = HousekeepingControl.findRoom(roomNum);
        if (room == null) {
            System.out.println("Room " + roomNum + " not found.");
            InputUtility.pressEnterToContinue();
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
        InputUtility.pressEnterToContinue();
    }
    
    // ==============================
    // 6. Report: Status Summary Across All Rooms
    // ==============================
    private static void reportStatusSummary() {
        
        printHeader("ROOM STATUS SUMMARY REPORT");
 
        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("No rooms registered yet.");
            printFooter();
            InputUtility.pressEnterToContinue();
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
        System.out.println("Single Room            : " + typeCounts[RoomTypeUtil.Single_Room]);
        System.out.println("Medium Room            : " + typeCounts[RoomTypeUtil.Medium_Room]);
        System.out.println("Large Room               : " + typeCounts[RoomTypeUtil.Large_Room]);

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
                new String[]{"Single", "Medium", "Large"},
                new int[]{
                    typeCounts[RoomTypeUtil.Single_Room],
                    typeCounts[RoomTypeUtil.Medium_Room],
                    typeCounts[RoomTypeUtil.Large_Room]
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
        InputUtility.pressEnterToContinue();
    }

}