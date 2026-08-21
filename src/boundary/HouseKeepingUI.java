package boundary;

/*
 * @author Chia Kah Shun
 */

import adt.ListInterface;
import control.BookingControl;
import control.HousekeepingControl;
import entity.Booking;
import entity.Room;
import entity.StatusEntry;
import java.util.Iterator;
import utility.InputUtility;
import utility.Navigation;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import utility.Utility;

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
            "3. Guest Check-Out",
            "4. Cleaning Queue (Dirty + In Progress)",
            "5. Report: Room Status History",
            "6. Report: Room Status Summary",
            "7. Report: Room Demand & Availability",
            "0. Back to Main Menu"
        };

        Runnable[] actions = {
            () -> showRoomStatusMenu(),
            () -> manageRoomStatus(),
            () -> guestCheckOut(),
            () -> showRoomsCleanInProgress(),
            () -> reportStatusHistory(),
            () -> reportStatusSummary(),
            () -> roomDemandAndAvailabilityReportUI(),
            () -> Navigation.stack.pop()
        };

        Utility.customMenu(
            options,
            Utility.HOTEL_NAME + " - HOUSEKEEPING MENU",
            "Enter your choice: ",
            actions
        );
    };

    // UI Formatting Utilities
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

    private static void printBarChart(String title, String[] labels, int[] values) {
        System.out.println();
        System.out.println("  " + title + ":");

        int max = 0;
        for (int value : values) {
            max = Math.max(max, value);
        }
        if (max == 0) {
            max = 1; // avoid divide-by-zero
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

    // 1. Show Room Status
    private static void showRoomStatusMenu() {
        boolean resume;
        do {
            resume = false;
            InputUtility.clearScreen();

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

    private static void showRoomStatus() {
        printHeader("ROOM STATUS OVERVIEW");

        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("  No rooms registered yet.");
            printFooter();
            return;
        }

        printRoomTable(HousekeepingControl.getRoomsSortedByRoomNumber());
        printFooter();
    }

    private static void printRoomTable(ListInterface<Room> rooms) {
        System.out.printf("  %-8s %-14s %-20s%n", "Room", "Type", "Status");
        System.out.printf("  %-8s %-14s %-20s%n", "------", "------------", "------------------");

        if (rooms == null || rooms.isEmpty()) {
            return;
        }

        Iterator<Room> iterator = rooms.getIterator();
        while (iterator.hasNext()) {
            Room room = iterator.next();
            if (room == null) continue;

            StatusEntry current = (room.getStatusHistory() != null) ? room.getStatusHistory().getCurrentData() : null;
            String statusStr = (current != null) ? RoomStatusUtil.statusName(current.getStatusCode()) : "Unknown";

            System.out.printf("  %-8s %-14s %-20s%n",
                    room.getRoomNum(),
                    RoomTypeUtil.roomTypeName(room.getRoomType()),
                    statusStr);
        }
    }

    // Search & Sort UI
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

    // Filter Prompts for Reports
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

    // 2. Manage Room Status
    private static void manageRoomStatus() {
        Room room = selectOrRegisterRoom();
        if (room == null) {
            return;
        }

        boolean continueManaging = true;

        while (continueManaging) {
            InputUtility.clearScreen();
            showCurrentRoomState(room);

            StatusEntry current = (room.getStatusHistory() != null) ? room.getStatusHistory().getCurrentData() : null;
            int currentStatus = (current != null) ? current.getStatusCode() : -1;
            boolean canAssignToBooking = (currentStatus == RoomStatusUtil.Ready_For_CheckIN);

            if (canAssignToBooking) {
                System.out.println("  1. Advance to Next Status");
                System.out.println("  2. Correct Mistake (Rollback)");
                System.out.println("  3. Guest Requests Late Check-Out (Interrupt)");
                System.out.println("  4. Resume Cleaning (After Late Check-Out Resolved)");
                System.out.println("  5. Assign to Waiting Booking (Check-In)");
                System.out.println("  0. Back");
                System.out.println(border());
                System.out.print("Enter your choice: ");

                String choice = InputUtility.getStringInput().trim();
                switch (choice) {
                    case "1": advanceStatus(room); break;
                    case "2": rollbackStatus(room); break;
                    case "3": interruptForLateCheckOut(room); break;
                    case "4": resumeStatus(room); break;
                    case "5": assignRoomToBooking(room); break;
                    case "0": continueManaging = false; break;
                    default:
                        System.out.println("Invalid choice!");
                        InputUtility.pressEnterToContinue();
                }
            } else {
                System.out.println("  1. Advance to Next Status");
                System.out.println("  2. Correct Mistake (Rollback)");
                System.out.println("  3. Guest Requests Late Check-Out (Interrupt)");
                System.out.println("  4. Resume Cleaning (After Late Check-Out Resolved)");
                System.out.println("  0. Back");
                System.out.println(border());
                System.out.print("Enter your choice: ");

                String choice = InputUtility.getStringInput().trim();
                switch (choice) {
                    case "1": advanceStatus(room); break;
                    case "2": rollbackStatus(room); break;
                    case "3": interruptForLateCheckOut(room); break;
                    case "4": resumeStatus(room); break;
                    case "0": continueManaging = false; break;
                    default:
                        System.out.println("Invalid choice!");
                        InputUtility.pressEnterToContinue();
                }
            }
        }
    }

    private static void showCurrentRoomState(Room room) {
        StatusEntry current = (room.getStatusHistory() != null) ? room.getStatusHistory().getCurrentData() : null;
        String statusName = (current != null) ? RoomStatusUtil.statusName(current.getStatusCode()) : "Unknown";

        printHeader("MANAGE ROOM " + room.getRoomNum());
        System.out.println("  Room Type      : " + RoomTypeUtil.roomTypeName(room.getRoomType()));
        System.out.println("  Current Status : " + statusName);
        if (current != null && current.getNote() != null && !current.getNote().isEmpty()) {
            System.out.println("  Status Note    : " + current.getNote());
        }
        System.out.println(border());
    }

    private static Room selectOrRegisterRoom() {
        System.out.print("Enter Room Number: ");
        String roomNum = InputUtility.getStringInput().trim();

        Room room = HousekeepingControl.findRoom(roomNum);
        if (room != null) {
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
                System.out.println("Invalid choice, defaulting to Single Room.");
                return RoomTypeUtil.Single_Room;
        }
    }

    private static void advanceStatus(Room room) {
        System.out.print("Note (optional): ");
        String note = InputUtility.getStringInput().trim();

        int nextStatus = HousekeepingControl.advanceStatus(room, note);
        if (nextStatus == -1) {
            System.out.println("Room " + room.getRoomNum() + " has no automatic next status from here.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " updated to: " + RoomStatusUtil.statusName(nextStatus));
        }
        InputUtility.pressEnterToContinue();
    }

    private static void rollbackStatus(Room room) {
        System.out.print("Note (optional rollback reason): ");
        String note = InputUtility.getStringInput().trim();

        int restoredStatus = HousekeepingControl.rollbackStatus(room, note);
        if (restoredStatus == -1) {
            System.out.println("Room " + room.getRoomNum() + ": already at earliest status, cannot roll back.");
        } else {
            System.out.println("Room " + room.getRoomNum() + " rolled back to: " + RoomStatusUtil.statusName(restoredStatus));
        }
        InputUtility.pressEnterToContinue();
    }

    private static void interruptForLateCheckOut(Room room) {
        String note = "Late check out";
        HousekeepingControl.interruptForLateCheckout(room, note);
        System.out.println("Room " + room.getRoomNum() + " status note updated to: " + note);
        InputUtility.pressEnterToContinue();
    }

    private static void resumeStatus(Room room) {
        int resumedStatus = HousekeepingControl.resumeStatus(room);

        if (resumedStatus == -1) {
            System.out.println("Room " + room.getRoomNum() 
                    + ": Room is not currently on Late Check-Out Hold.");
        } else {
            System.out.println("Room " + room.getRoomNum() 
                    + " resumed to status: " + RoomStatusUtil.statusName(resumedStatus));
            System.out.println("Status Note: Resumed cleaning (After late check out)");
        }

        InputUtility.pressEnterToContinue();
    }

    private static void assignRoomToBooking(Room room) {
        StatusEntry current = (room.getStatusHistory() != null) ? room.getStatusHistory().getCurrentData() : null;
        int currentStatus = (current != null) ? current.getStatusCode() : -1;

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

        InputUtility.pressEnterToContinue();
    }

    // 3. Guest Check-Out
    private static void guestCheckOut() {
        printHeader("GUEST CHECK-OUT PROCESSING");

        System.out.print("Enter Room Number: ");
        String roomNum = InputUtility.getStringInput().trim();

        if (roomNum.isEmpty()) {
            System.out.println("Room number cannot be empty.");
            InputUtility.pressEnterToContinue();
            return;
        }

        Room room = HousekeepingControl.findRoom(roomNum);

        if (room == null) {
            System.out.println("Room " + roomNum + " not found.");
            InputUtility.pressEnterToContinue();
            return;
        }

        boolean housekeepingUpdated = HousekeepingControl.guestCheckOut(room);

        BookingControl bookingControl = new BookingControl();
        Booking closedBooking = bookingControl.checkOutBookingByRoomID(room.getRoomNum());

        if (housekeepingUpdated) {
            System.out.println("✔ Room " + room.getRoomNum() + " status updated to: Dirty (needs cleaning).");

            if (closedBooking != null) {
                System.out.println("✔ Matching Booking " + closedBooking.getBookingID()
                        + " (" + closedBooking.getGuestName() + ") updated to: Checked Out.");
            } else {
                System.out.println("⚠ Housekeeping status updated, but no active 'Served' booking record was linked to Room "
                        + room.getRoomNum() + ".");
            }
        } else {
            System.out.println("✖ Room " + room.getRoomNum() + " is already marked Dirty (needs cleaning).");
            if (closedBooking != null) {
                System.out.println("✔ Associated Booking " + closedBooking.getBookingID() + " updated to: Checked Out.");
            }
        }

        InputUtility.pressEnterToContinue();
    }

    // 4. Cleaning Queue
    private static void showRoomsCleanInProgress() {
        printHeader("HOUSEKEEPING CLEANING QUEUE");

        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("  No rooms registered yet.");
            printFooter();
            InputUtility.pressEnterToContinue();
            return;
        }

        ListInterface<Room> snapshotList = HousekeepingControl.getRoomsSortedByRoomNumber();

        System.out.println("  [Awaiting Cleaning - DIRTY]");
        System.out.printf("  %-8s %-16s %-20s%n", "Room", "Type", "Status");
        System.out.printf("  %-8s %-16s %-20s%n", "------", "----------------", "--------------------");

        boolean foundDirty = false;
        Iterator<Room> dirtyIterator = snapshotList.getIterator();

        while (dirtyIterator.hasNext()) {
            Room room = dirtyIterator.next();
            if (room == null || room.getStatusHistory() == null) continue;

            StatusEntry current = room.getStatusHistory().getCurrentData();
            if (current != null && current.getStatusCode() == RoomStatusUtil.Dirty) {
                System.out.printf("  %-8s %-16s %-20s%n",
                        room.getRoomNum(),
                        RoomTypeUtil.roomTypeName(room.getRoomType()),
                        "Dirty (Needs Cleaning)");
                foundDirty = true;
            }
        }

        if (!foundDirty) {
            System.out.println("  (None - No rooms currently awaiting cleaning)");
        }

        System.out.println();

        System.out.println("  [Currently In Progress - CLEANING]");
        System.out.printf("  %-8s %-16s %-20s%n", "Room", "Type", "Status");
        System.out.printf("  %-8s %-16s %-20s%n", "------", "----------------", "--------------------");

        boolean foundInProgress = false;
        Iterator<Room> inProgressIterator = snapshotList.getIterator();

        while (inProgressIterator.hasNext()) {
            Room room = inProgressIterator.next();
            if (room == null || room.getStatusHistory() == null) continue;

            StatusEntry current = room.getStatusHistory().getCurrentData();
            if (current != null && current.getStatusCode() == RoomStatusUtil.Clean_In_Progress) {
                System.out.printf("  %-8s %-16s %-20s%n",
                        room.getRoomNum(),
                        RoomTypeUtil.roomTypeName(room.getRoomType()),
                        "Cleaning In Progress");
                foundInProgress = true;
            }
        }

        if (!foundInProgress) {
            System.out.println("  (None - No active cleaning tasks in progress)");
        }

        printFooter();
        InputUtility.pressEnterToContinue();
    }

    // 5. Report: Room Status History
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

        if (room.getStatusHistory() != null && !room.getStatusHistory().isEmpty()) {
            StatusEntry current = room.getStatusHistory().getCurrentData();
            Iterator<StatusEntry> iterator = room.getStatusHistory().getIterator();

            int step = 1;
            while (iterator.hasNext()) {
                StatusEntry entry = iterator.next();
                if (entry == null) continue;

                String activeMarker = (entry == current) ? "  <-- [CURRENT STATUS]" : "";
                String noteText = (entry.getNote() != null && !entry.getNote().trim().isEmpty()) 
                        ? " | Note: \"" + entry.getNote() + "\"" 
                        : "";

                System.out.printf("  %2d. %-20s%s%s%n", 
                        step, 
                        RoomStatusUtil.statusName(entry.getStatusCode()), 
                        noteText, 
                        activeMarker);
                step++;
            }
        } else {
            System.out.println("  No status history recorded for this room.");
        }

        BookingControl bookingControl = new BookingControl();
        ListInterface<Booking> waitingMatches = HousekeepingControl.getWaitingBookingsForRoom(room, bookingControl);

        System.out.println();
        if (waitingMatches.isEmpty()) {
            System.out.println("  No waiting bookings currently match this room's type.");
        } else {
            System.out.println("  Waiting bookings for " + RoomTypeUtil.roomTypeName(room.getRoomType()) + ":");
            Iterator<Booking> bookingIterator = waitingMatches.getIterator();
            while (bookingIterator.hasNext()) {
                Booking booking = bookingIterator.next();
                if (booking != null) {
                    System.out.println("    - " + booking.getBookingID() + " (" + booking.getGuestName() + ")");
                }
            }
        }

        printFooter();
        InputUtility.pressEnterToContinue();
    }

    // 6. Report: Room Status Summary
    private static void reportStatusSummary() {
        printHeader("ROOM STATUS SUMMARY REPORT");

        if (HousekeepingControl.roomIsEmpty()) {
            System.out.println("  No rooms registered yet.");
            printFooter();
            InputUtility.pressEnterToContinue();
            return;
        }

        int[] statusCounts = HousekeepingControl.getStatusCounts();
        int[] typeCounts = HousekeepingControl.getTypeCounts();

        System.out.println("  Dirty                  : " + statusCounts[RoomStatusUtil.Dirty]);
        System.out.println("  Cleaning In Progress   : " + statusCounts[RoomStatusUtil.Clean_In_Progress]);
        System.out.println("  Inspected              : " + statusCounts[RoomStatusUtil.Inspected]);
        System.out.println("  Ready for Check-In     : " + statusCounts[RoomStatusUtil.Ready_For_CheckIN]);
        System.out.println("  Late Check-Out Hold    : " + statusCounts[RoomStatusUtil.Late_CheckOut_Hold]);
        System.out.println("  ----------------------------------------");
        System.out.println("  Total Rooms            : " + HousekeepingControl.getRoomCount());

        System.out.println();
        System.out.println("  Rooms by Type:");
        System.out.println("  Single Room            : " + typeCounts[RoomTypeUtil.Single_Room]);
        System.out.println("  Medium Room            : " + typeCounts[RoomTypeUtil.Medium_Room]);
        System.out.println("  Large Room             : " + typeCounts[RoomTypeUtil.Large_Room]);

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

        BookingControl bookingControl = new BookingControl();
        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();
        ListInterface<HousekeepingControl.RoomBookingMatch> readyMatches =
                HousekeepingControl.getReadyRoomsForWaitingGuests(bookingControl);

        System.out.println();
        System.out.println("  Cross-Module (Booking Integration):");
        System.out.println("  Total Waiting Bookings : " + waitingBookings.getSize());
        System.out.println("  Ready Rooms w/ Match   : " + readyMatches.getSize());

        printFooter();
        InputUtility.pressEnterToContinue();
    }

    // 7. Report: Room Demand & Availability Report
    private static void roomDemandAndAvailabilityReportUI() {
        printHeader("ROOM DEMAND & AVAILABILITY REPORT - SETUP");

        Integer typeFilter = promptRoomTypeFilter();
        Integer statusFilter = promptStatusFilter();
        int sortOption = promptSortOption();

        ListInterface<Room> report = HousekeepingControl.generateBusinessCycleReport(typeFilter, statusFilter, sortOption);

        printHeader("ROOM DEMAND & AVAILABILITY REPORT");
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

        int[] statusBreakdown = new int[5];
        int[] typeBreakdown = new int[3];

        Iterator<Room> breakdownIterator = report.getIterator();
        while (breakdownIterator.hasNext()) {
            Room room = breakdownIterator.next();
            if (room != null) {
                if (room.getStatusHistory() != null && room.getStatusHistory().getCurrentData() != null) {
                    int status = room.getStatusHistory().getCurrentData().getStatusCode();
                    if (status >= 0 && status < statusBreakdown.length) {
                        statusBreakdown[status]++;
                    }
                }
                int type = room.getRoomType();
                if (type >= 0 && type < typeBreakdown.length) {
                    typeBreakdown[type]++;
                }
            }
        }

        BookingControl bookingControl = new BookingControl();
        int readyWithMatch = 0;

        Iterator<Room> matchIterator = report.getIterator();
        while (matchIterator.hasNext()) {
            Room room = matchIterator.next();
            if (room != null && room.getStatusHistory() != null && room.getStatusHistory().getCurrentData() != null) {
                int status = room.getStatusHistory().getCurrentData().getStatusCode();
                if (status == RoomStatusUtil.Ready_For_CheckIN) {
                    ListInterface<Booking> matches = HousekeepingControl.getWaitingBookingsForRoom(room, bookingControl);
                    if (!matches.isEmpty()) {
                        readyWithMatch++;
                    }
                }
            }
        }

        printHeader("DEMAND & AVAILABILITY VISUAL ANALYSIS");

        printBarChart("Status Distribution",
                new String[]{"Dirty", "Cleaning", "Inspected", "Ready", "Hold"},
                new int[]{
                    statusBreakdown[RoomStatusUtil.Dirty],
                    statusBreakdown[RoomStatusUtil.Clean_In_Progress],
                    statusBreakdown[RoomStatusUtil.Inspected],
                    statusBreakdown[RoomStatusUtil.Ready_For_CheckIN],
                    statusBreakdown[RoomStatusUtil.Late_CheckOut_Hold]
                });

        printBarChart("Type Composition",
                new String[]{"Single", "Medium", "Large"},
                new int[]{
                    typeBreakdown[RoomTypeUtil.Single_Room],
                    typeBreakdown[RoomTypeUtil.Medium_Room],
                    typeBreakdown[RoomTypeUtil.Large_Room]
                });

        System.out.println();
        System.out.println("  --------------------------------------------------");
        System.out.println("  Filtered Set Size       : " + report.getSize() + " room(s)");
        System.out.println("  Ready Rooms w/ Match    : " + readyWithMatch);
        System.out.println("  --------------------------------------------------");

        printFooter();
        InputUtility.pressEnterToContinue();
    }
}