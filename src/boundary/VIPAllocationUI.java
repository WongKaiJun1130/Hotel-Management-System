package boundary;

import adt.DoublyLinkedList;
import adt.ListInterface;
import control.VIPAllocationControl;
import entity.Guest;
import entity.LoyaltyRecord;
import entity.Room;
import utility.InputUtility;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Module: VIP & Loyalty Tier Priority Room Allocation
 *
 * @author Wong Kai Jun
 */
public class VIPAllocationUI {

    private VIPAllocationControl allocationControl;

    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String MENU_BORDER = "+----------------------------------------------------------+";
    private static final String DETAIL_BORDER = "+----------------------------------------------------------+";
    private static final String TABLE_BORDER = "+-----+----------+----------------------+---------------+----------+------------+---------------+------------+------------+------------------+";
    private static final String REPORT_BORDER = "+------+-----------------+--------------+-----------------+";

    //==========================================================
    // Constructor
    //==========================================================
    public VIPAllocationUI() {
        allocationControl = new VIPAllocationControl();
    }

    public VIPAllocationUI(VIPAllocationControl allocationControl) {

        if (allocationControl == null) {
            this.allocationControl = new VIPAllocationControl();
        } else {
            this.allocationControl = allocationControl;
        }
    }

    //==========================================================
    // Main Menu
    //==========================================================
    public void allocationMenu() {

        int choice;

        do {

            InputUtility.clearScreen();

            System.out.println(MENU_BORDER);
            printBoxTitle("VIP & LOYALTY ROOM ALLOCATION MENU");
            System.out.println(MENU_BORDER);

            printMenuItem(1, "Add Guest To Allocation Stack");
            printMenuItem(2, "Allocate Room");
            printMenuItem(3, "View Next Priority Guest");
            printMenuItem(4, "Display Allocation Stack");
            printMenuItem(5, "Search Guest Allocation");
            printMenuItem(6, "View Available Rooms");
            printMenuItem(7, "Remove Guest Allocation");
            printMenuItem(8, "Loyalty Tier Summary Report");
            printMenuItem(9, "Room Type Summary Report");
            printMenuItem(0, "Back");

            System.out.println(MENU_BORDER);

            choice = readChoice(0, 9, "Enter Choice: ");

            switch (choice) {

                case 1:
                    addGuest();
                    break;

                case 2:
                    allocateRoom();
                    break;

                case 3:
                    viewNextPriorityGuest();
                    break;

                case 4:
                    displayAllocationStack();
                    InputUtility.pressEnterToContinue();
                    break;

                case 5:
                    searchGuestAllocation();
                    break;

                case 6:
                    viewAvailableRooms();
                    break;

                case 7:
                    removeGuestAllocation();
                    break;

                case 8:
                    displayLoyaltyTierSummaryReport();
                    break;

                case 9:
                    displayRoomTypeSummaryReport();
                    break;

                case 0:
                    break;

                default:
                    break;
            }

        } while (choice != 0);
    }

    //==========================================================
    // 1. Add Guest
    //==========================================================
    private void addGuest() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("ADD GUEST TO ALLOCATION STACK");
        System.out.println(MENU_BORDER);

        String guestID = allocationControl.generateGuestID();

        System.out.printf("| %-18s : %-35s |%n", "Guest ID", guestID);

        System.out.println(MENU_BORDER);

        String guestName = inputValidGuestName();

        String phoneNumber = inputValidPhoneNumber();

        String loyaltyTier = inputLoyaltyTier();

        int lifetimePoints = getMinimumPointsForTier(loyaltyTier);

        String roomType = inputRoomType();

        String checkInDate = inputCheckInDate();

        //======================================================
        // FIX:
        // Arrival Date Uses Selected Check-In Date
        //======================================================
        String arrivalDateTime = inputArrivalDateTime(checkInDate);

        Guest guest = new Guest(guestID, guestName, phoneNumber, loyaltyTier, roomType, "Waiting", checkInDate, arrivalDateTime);

        System.out.println();

        displayNewGuestInformation(guest, lifetimePoints);

        System.out.println();

        displayPointLevelGuide();

        System.out.println();

        System.out.println(MENU_BORDER);

        printMenuItem(1, "Confirm");
        printMenuItem(2, "Cancel");

        System.out.println(MENU_BORDER);

        int confirmation = readChoice(1, 2, "Enter Choice: ");

        if (confirmation == 1) {

            boolean added = allocationControl.addGuestToStack(guest, lifetimePoints);

            System.out.println();

            if (added) {
                printMessageBox("Guest added to allocation stack successfully.");
            } else {
                printMessageBox("Unable to add guest.");
            }

        } else {

            System.out.println();

            printMessageBox("Add guest cancelled.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Loyalty Tier
    //==========================================================
    private String inputLoyaltyTier() {

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("LOYALTY TIER");
        System.out.println(MENU_BORDER);

        System.out.printf("| %-56s |%n", "1. Elite      - Minimum 6000 Points");
        System.out.printf("| %-56s |%n", "2. Diamond    - Minimum 4000 Points");
        System.out.printf("| %-56s |%n", "3. Platinum   - Minimum 2000 Points");
        System.out.printf("| %-56s |%n", "4. Standard   - Minimum 0 Points");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 4, "Choose Loyalty Tier: ");

        switch (choice) {

            case 1:
                return "Elite";

            case 2:
                return "Diamond";

            case 3:
                return "Platinum";

            default:
                return "Standard";
        }
    }

    //==========================================================
    // Minimum Points
    //==========================================================
    private int getMinimumPointsForTier(String loyaltyTier) {

        if (loyaltyTier == null) {
            return 0;
        }

        if (loyaltyTier.equalsIgnoreCase("Elite")) {
            return 6000;
        }

        if (loyaltyTier.equalsIgnoreCase("Diamond")) {
            return 4000;
        }

        if (loyaltyTier.equalsIgnoreCase("Platinum")) {
            return 2000;
        }

        return 0;
    }

    //==========================================================
    // Point Level
    //==========================================================
    private void displayPointLevelGuide() {

        System.out.println(MENU_BORDER);

        printBoxTitle("POINT LEVEL");

        System.out.println(MENU_BORDER);

        System.out.printf("| %-56s |%n", "Elite      = 6000 Points And Above");
        System.out.printf("| %-56s |%n", "Diamond    = 4000 - 5999 Points");
        System.out.printf("| %-56s |%n", "Platinum   = 2000 - 3999 Points");
        System.out.printf("| %-56s |%n", "Standard   = 0 - 1999 Points");

        System.out.println(MENU_BORDER);
    }

    //==========================================================
    // Guest Information
    //==========================================================
    private void displayNewGuestInformation(Guest guest, int lifetimePoints) {

        if (guest == null) {
            return;
        }

        System.out.println(DETAIL_BORDER);
        printBoxTitle("GUEST INFORMATION");
        System.out.println(DETAIL_BORDER);

        System.out.printf("| %-18s : %-35s |%n", "Guest ID", limitText(guest.getGuestID(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Guest Name", limitText(guest.getGuestName(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Phone Number", limitText(guest.getPhoneNumber(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Loyalty Tier", limitText(guest.getLoyaltyTier(), 35));
        System.out.printf("| %-18s : %-35d |%n", "Lifetime Points", lifetimePoints);
        System.out.printf("| %-18s : %-35d |%n", "Priority", guest.getPriority());
        System.out.printf("| %-18s : %-35s |%n", "Room Type", limitText(guest.getRoomType(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Room Status", limitText(guest.getRoomStatus(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Check-In Date", limitText(guest.getCheckInDate(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Arrival DateTime", limitText(guest.getArrivalDateTime(), 35));

        System.out.println(DETAIL_BORDER);
    }

    //==========================================================
    // Guest Name
    //==========================================================
    private String inputValidGuestName() {

        while (true) {

            System.out.print("Guest Name      : ");

            String guestName = InputUtility.getStringInput();

            if (guestName == null || guestName.trim().isEmpty()) {
                System.out.println("Guest name cannot be empty.");
                continue;
            }

            guestName = guestName.trim().replaceAll("\\s+", " ");

            if (guestName.length() < 2 || guestName.length() > 30) {
                System.out.println("Guest name must contain 2 to 30 characters.");
                continue;
            }

            if (!guestName.matches("[A-Za-z][A-Za-z .'-]*")) {
                System.out.println("Guest name can only contain letters, spaces, apostrophes, dots and hyphens.");
                continue;
            }

            return guestName;
        }
    }

    //==========================================================
    // Phone Number
    //==========================================================
    private String inputValidPhoneNumber() {

        while (true) {

            System.out.print("Phone Number    : ");

            String phoneNumber = InputUtility.getStringInput();

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                System.out.println("Phone number cannot be empty.");
                continue;
            }

            phoneNumber = phoneNumber.replaceAll("[\\s-]", "");

            if (!phoneNumber.matches("01\\d{8,9}")) {
                System.out.println("Invalid phone number. Example: 0123456789");
                continue;
            }

            return phoneNumber;
        }
    }

    //==========================================================
    // Room Type
    //==========================================================
    private String inputRoomType() {

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("ROOM TYPE");
        System.out.println(MENU_BORDER);

        printMenuItem(1, "Small Room");
        printMenuItem(2, "Medium Room");
        printMenuItem(3, "Big Room");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 3, "Choose Room Type: ");

        switch (choice) {

            case 1:
                return "Small Room";

            case 2:
                return "Medium Room";

            default:
                return "Big Room";
        }
    }

    //==========================================================
    // Check-In Date
    //==========================================================
    private String inputCheckInDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        String defaultDate = LocalDate.now().format(formatter);

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("CHECK-IN DATE");
        System.out.println(MENU_BORDER);

        System.out.printf("| %-18s : %-35s |%n", "Default Date", defaultDate);

        printMenuItem(1, "Use Default Date");
        printMenuItem(2, "Change Date");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 2, "Enter Choice: ");

        if (choice == 1) {
            return defaultDate;
        }

        return inputNewCheckInDate(formatter);
    }

    //==========================================================
    // Change Check-In Date
    //==========================================================
    private String inputNewCheckInDate(DateTimeFormatter formatter) {

        while (true) {

            System.out.print("Enter Check-In Date (DD/MM/YYYY): ");

            String newDate = InputUtility.getStringInput();

            if (newDate == null || newDate.trim().isEmpty()) {
                System.out.println("Check-in date cannot be empty.");
                continue;
            }

            try {

                LocalDate parsedDate = LocalDate.parse(newDate.trim(), formatter);

                if (parsedDate.isBefore(LocalDate.now())) {
                    System.out.println("Check-in date cannot be before today.");
                    continue;
                }

                return parsedDate.format(formatter);

            } catch (DateTimeParseException exception) {

                System.out.println("Invalid date. Example: 25/08/2026");
            }
        }
    }

    //==========================================================
    // Arrival DateTime
    //
    // FIXED:
    // Date Always Follows Selected Check-In Date
    //==========================================================
    private String inputArrivalDateTime(String checkInDate) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

        LocalDate selectedDate;

        try {
            selectedDate = LocalDate.parse(checkInDate, dateFormatter);
        } catch (DateTimeParseException exception) {
            selectedDate = LocalDate.now();
        }

        LocalTime defaultTime = LocalTime.now().withSecond(0).withNano(0);

        LocalDateTime defaultArrival = LocalDateTime.of(selectedDate, defaultTime);

        String defaultArrivalDateTime = defaultArrival.format(dateTimeFormatter);

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("ARRIVAL DATE AND TIME");
        System.out.println(MENU_BORDER);

        System.out.printf("| %-18s : %-35s |%n", "Check-In Date", checkInDate);
        System.out.printf("| %-18s : %-35s |%n", "Default DateTime", defaultArrivalDateTime);

        printMenuItem(1, "Use Default Date And Time");
        printMenuItem(2, "Change Arrival Time");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 2, "Enter Choice: ");

        if (choice == 1) {
            return defaultArrivalDateTime;
        }

        return inputCustomArrivalTime(selectedDate);
    }

    //==========================================================
    // Custom Arrival Time
    //
    // Only Change Time
    // Date Remains Check-In Date
    //==========================================================
    private String inputCustomArrivalTime(LocalDate selectedDate) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

        while (true) {

            System.out.print("Enter Arrival Time (HH:MM): ");

            String arrivalTime = InputUtility.getStringInput();

            if (arrivalTime == null || arrivalTime.trim().isEmpty()) {
                System.out.println("Arrival time cannot be empty.");
                continue;
            }

            try {

                LocalTime parsedTime = LocalTime.parse(arrivalTime.trim(), timeFormatter);

                LocalDateTime arrivalDateTime = LocalDateTime.of(selectedDate, parsedTime);

                if (arrivalDateTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
                    System.out.println("Arrival date and time cannot be in the past.");
                    continue;
                }

                return arrivalDateTime.format(dateTimeFormatter);

            } catch (DateTimeParseException exception) {

                System.out.println("Invalid time. Example: 14:30");
            }
        }
    }

    //==========================================================
    // 2. Allocate Room
    //==========================================================
    private void allocateRoom() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("ALLOCATE ROOM");
        System.out.println(MENU_BORDER);

        Guest guest = allocationControl.allocateRoom();

        if (guest == null) {

            printMessageBox("No waiting guest in the allocation stack.");

            InputUtility.pressEnterToContinue();

            return;
        }

        System.out.println();

        displayGuestInformation(guest);

        System.out.println();

        printMessageBox("Room allocated successfully.");

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 3. Next Priority Guest
    //==========================================================
    private void viewNextPriorityGuest() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("NEXT PRIORITY GUEST");
        System.out.println(MENU_BORDER);

        Guest guest = allocationControl.getNextPriorityGuest();

        if (guest == null) {

            printMessageBox("No waiting guest in the allocation stack.");

            InputUtility.pressEnterToContinue();

            return;
        }

        System.out.println();

        displayGuestInformation(guest);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 4. Display Allocation Stack
    //
    // Also Gets Waiting Guest From Booking Module
    //==========================================================
    private void displayAllocationStack() {

        InputUtility.clearScreen();

        ListInterface<Guest> guestList = allocationControl.getAllGuestAllocations();

        if (guestList == null || guestList.isEmpty()) {

            printMessageBox("No guest records in the allocation stack.");

            return;
        }

        displayPointLevelGuide();

        System.out.println();

        System.out.println(TABLE_BORDER);

        System.out.printf("| %-138s |%n", centerText("ALLOCATION STACK", 138));

        System.out.println(TABLE_BORDER);

        displayGuestTableHeader();

        for (int i = 1; i <= guestList.getSize(); i++) {

            Guest guest = guestList.getEntry(i);

            if (guest != null) {
                displayGuestTableRow(i, guest);
            }
        }

        System.out.println(TABLE_BORDER);

        System.out.printf("| %-20s : %-115d |%n", "Total Waiting Guests", guestList.getSize());

        System.out.println(TABLE_BORDER);
    }

    //==========================================================
    // 5. Search
    //==========================================================
    private void searchGuestAllocation() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("SEARCH GUEST ALLOCATION");
        System.out.println(MENU_BORDER);

        String keyword;

        while (true) {

            System.out.print("Enter Search Keyword: ");

            keyword = InputUtility.getStringInput();

            if (keyword != null && !keyword.trim().isEmpty()) {
                keyword = keyword.trim();
                break;
            }

            System.out.println("Search keyword cannot be empty.");
        }

        ListInterface<Guest> result = allocationControl.searchGuest(keyword);

        if (result == null || result.isEmpty()) {

            System.out.println();

            printMessageBox("Guest allocation record not found.");

            InputUtility.pressEnterToContinue();

            return;
        }

        System.out.println();
        System.out.println(TABLE_BORDER);

        System.out.printf("| %-138s |%n", centerText("SEARCH RESULT", 138));

        System.out.println(TABLE_BORDER);

        displayGuestTableHeader();

        for (int i = 1; i <= result.getSize(); i++) {

            Guest guest = result.getEntry(i);

            if (guest != null) {
                displayGuestTableRow(i, guest);
            }
        }

        System.out.println(TABLE_BORDER);

        System.out.printf("| %-24s : %-111d |%n", "Total Matching Guests", result.getSize());

        System.out.println(TABLE_BORDER);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 6. Available Rooms
    //
    // Room Class From Housekeeping Module
    //==========================================================
    private void viewAvailableRooms() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------------+");
        System.out.println("|                       AVAILABLE ROOMS                          |");
        System.out.println("+----------------------------------------------------------------+");

        int totalHotelRooms = allocationControl.getTotalRoomsFromRoomModule();

        DoublyLinkedList<Room> availableRooms = allocationControl.getAvailableRoomsFromRoomModule();

        System.out.printf("| %-24s : %-35d |%n", "Total Hotel Rooms", totalHotelRooms);
        System.out.printf("| %-24s : %-35d |%n", "Total Available Rooms", availableRooms.getSize());

        System.out.println("+----------------------------------------------------------------+");

        if (availableRooms.isEmpty()) {

            System.out.println("| No rooms are currently ready for check-in.                     |");
            System.out.println("+----------------------------------------------------------------+");

            InputUtility.pressEnterToContinue();

            return;
        }

        System.out.println();

        System.out.println("+------+------------+----------------------+----------------------+");

        System.out.printf("| %-4s | %-10s | %-20s | %-20s |%n", "No.", "Room No.", "Room Type", "Current Status");

        System.out.println("+------+------------+----------------------+----------------------+");

        for (int i = 1; i <= availableRooms.getSize(); i++) {

            Room room = availableRooms.getEntry(i);

            String roomType = allocationControl.getRoomTypeName(room);

            String roomStatus = allocationControl.getRoomStatusName(room);

            System.out.printf("| %-4d | %-10s | %-20s | %-20s |%n", i, room.getRoomNum(), limitText(roomType, 20), limitText(roomStatus, 20));
        }

        System.out.println("+------+------------+----------------------+----------------------+");

        System.out.printf("| %-39s : %-21d |%n", "Total Available Rooms", availableRooms.getSize());

        System.out.println("+----------------------------------------------------------------+");

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 7. Remove Guest
    //==========================================================
    private void removeGuestAllocation() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("REMOVE GUEST ALLOCATION");
        System.out.println(MENU_BORDER);

        if (allocationControl.isStackEmpty()) {

            printMessageBox("No guest records in the allocation stack.");

            InputUtility.pressEnterToContinue();

            return;
        }

        String guestID = inputValidGuestID("Enter Guest ID To Remove: ");

        Guest guest = allocationControl.searchGuestByID(guestID);

        if (guest == null) {

            System.out.println();

            printMessageBox("Guest allocation record not found.");

            InputUtility.pressEnterToContinue();

            return;
        }

        System.out.println();

        displayGuestInformation(guest);

        System.out.println();

        System.out.println(MENU_BORDER);

        printMenuItem(1, "Confirm Remove");
        printMenuItem(2, "Cancel");

        System.out.println(MENU_BORDER);

        int confirmation = readChoice(1, 2, "Enter Choice: ");

        if (confirmation == 1) {

            Guest removedGuest = allocationControl.removeGuestByID(guestID);

            System.out.println();

            if (removedGuest != null) {

                System.out.println(MENU_BORDER);
                printBoxTitle("GUEST REMOVED SUCCESSFULLY");
                System.out.println(MENU_BORDER);

                System.out.printf("| %-18s : %-35s |%n", "Guest ID", removedGuest.getGuestID());
                System.out.printf("| %-18s : %-35s |%n", "Guest Name", limitText(removedGuest.getGuestName(), 35));

                System.out.println(MENU_BORDER);

            } else {

                printMessageBox("Unable to remove guest allocation.");
            }

        } else {

            System.out.println();

            printMessageBox("Remove guest allocation cancelled.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Guest ID
    //==========================================================
    private String inputValidGuestID(String prompt) {

        while (true) {

            System.out.print(prompt);

            String guestID = InputUtility.getStringInput();

            if (guestID == null || guestID.trim().isEmpty()) {
                System.out.println("Guest ID cannot be empty.");
                continue;
            }

            guestID = guestID.trim().toUpperCase();

            if (!guestID.matches("R\\d{4}")) {
                System.out.println("Invalid Guest ID format. Example: R0001");
                continue;
            }

            return guestID;
        }
    }

    //==========================================================
    // Report Year
    //==========================================================
    private int inputReportYear() {

        while (true) {

            System.out.print("Enter Report Year (YYYY): ");

            int year = InputUtility.getIntInput();

            if (year >= 2000 && year <= 2100) {
                return year;
            }

            System.out.println("Invalid year. Please enter year between 2000 and 2100.");
        }
    }

    //==========================================================
    // Report Month
    //==========================================================
    private int inputReportMonth() {

        while (true) {

            System.out.print("Enter Report Month (1-12): ");

            int month = InputUtility.getIntInput();

            if (month >= 1 && month <= 12) {
                return month;
            }

            System.out.println("Invalid month. Please enter 1 to 12.");
        }
    }

    //==========================================================
    // Loyalty Tier Filter
    //==========================================================
    private String inputLoyaltyTierFilter() {

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("LOYALTY TIER FILTER");
        System.out.println(MENU_BORDER);

        printMenuItem(1, "All Loyalty Tiers");
        printMenuItem(2, "Elite");
        printMenuItem(3, "Diamond");
        printMenuItem(4, "Platinum");
        printMenuItem(5, "Standard");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 5, "Choose Filter: ");

        switch (choice) {

            case 2:
                return "Elite";

            case 3:
                return "Diamond";

            case 4:
                return "Platinum";

            case 5:
                return "Standard";

            default:
                return "All";
        }
    }

    //==========================================================
    // Room Type Filter
    //==========================================================
    private String inputRoomTypeFilter() {

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("ROOM TYPE FILTER");
        System.out.println(MENU_BORDER);

        printMenuItem(1, "All Room Types");
        printMenuItem(2, "Small Room");
        printMenuItem(3, "Medium Room");
        printMenuItem(4, "Big Room");

        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 4, "Choose Filter: ");

        switch (choice) {

            case 2:
                return "Small Room";

            case 3:
                return "Medium Room";

            case 4:
                return "Big Room";

            default:
                return "All";
        }
    }

    //==========================================================
    // Month Name
    //==========================================================
    private String getMonthName(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    //==========================================================
    // 8. Loyalty Tier Summary Report
    // With Filtering
    //==========================================================
    private void displayLoyaltyTierSummaryReport() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("LOYALTY TIER SUMMARY REPORT");
        System.out.println(MENU_BORDER);

        int year = inputReportYear();
        int month = inputReportMonth();

        String loyaltyFilter = inputLoyaltyTierFilter();
        String roomTypeFilter = inputRoomTypeFilter();

        String reportPeriod = getMonthName(month).toUpperCase() + " " + year;

        int[] loyaltyTierCounts = allocationControl.getLoyaltyTierCountsFromLoyaltyModule(year, month, loyaltyFilter, roomTypeFilter);

        int totalGuests = allocationControl.getTotalLoyaltyMembersFromLoyaltyModule(year, month, loyaltyFilter, roomTypeFilter);

        InputUtility.clearScreen();

        if (totalGuests == 0) {

            System.out.println(MENU_BORDER);
            printBoxTitle("LOYALTY TIER SUMMARY REPORT");
            System.out.println(MENU_BORDER);

            System.out.printf("| %-18s : %-35s |%n", "Report Period", reportPeriod);
            System.out.printf("| %-18s : %-35s |%n", "Tier Filter", loyaltyFilter);
            System.out.printf("| %-18s : %-35s |%n", "Room Filter", roomTypeFilter);

            System.out.println(MENU_BORDER);

            System.out.printf("| %-56s |%n", "No matching loyalty records found.");

            System.out.println(MENU_BORDER);

            InputUtility.pressEnterToContinue();

            return;
        }

        String[] loyaltyLabels = {"Elite", "Diamond", "Platinum", "Standard"};

        String[] loyaltyGraph = buildVerticalBarChart("Loyalty Tier Report - " + reportPeriod, loyaltyLabels, loyaltyTierCounts, "Loyalty Tiers");

        String generatedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-55s |%n", centerText("LOYALTY TIER SUMMARY REPORT", 55));

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-15s : %-36s |%n", "Report Period", reportPeriod);
        System.out.printf("| %-15s : %-36s |%n", "Tier Filter", loyaltyFilter);
        System.out.printf("| %-15s : %-36s |%n", "Room Filter", roomTypeFilter);
        System.out.printf("| %-15s : %-36s |%n", "Generated At", generatedDateTime);
        System.out.printf("| %-15s : %-36d |%n", "Total Members", totalGuests);

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-4s | %-15s | %-12s | %-15s |%n", "No.", "Loyalty Tier", "Guest Count", "Percentage");

        System.out.println(REPORT_BORDER);

        for (int i = 0; i < loyaltyLabels.length; i++) {

            double percentage = loyaltyTierCounts[i] * 100.0 / totalGuests;

            System.out.printf("| %-4d | %-15s | %-12d | %-14.2f%% |%n", i + 1, loyaltyLabels[i], loyaltyTierCounts[i], percentage);
        }

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-22s | %-12d | %-15s |%n", "Total Members", totalGuests, "100.00%");

        System.out.println(REPORT_BORDER);

        System.out.println();

        printSingleGraph(loyaltyGraph);

        System.out.println();

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-55s |%n", centerText("END OF REPORT", 55));

        System.out.println(REPORT_BORDER);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 9. Room Type Summary Report
    // With Filtering
    //==========================================================
    private void displayRoomTypeSummaryReport() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("ROOM TYPE SUMMARY REPORT");
        System.out.println(MENU_BORDER);

        int year = inputReportYear();
        int month = inputReportMonth();

        String loyaltyFilter = inputLoyaltyTierFilter();
        String roomTypeFilter = inputRoomTypeFilter();

        String reportPeriod = getMonthName(month).toUpperCase() + " " + year;

        int[] roomTypeCounts = allocationControl.getRoomTypeCounts(year, month, loyaltyFilter, roomTypeFilter);

        int totalGuests = allocationControl.getTotalGuests(year, month, loyaltyFilter, roomTypeFilter);

        InputUtility.clearScreen();

        if (totalGuests == 0) {

            System.out.println(MENU_BORDER);
            printBoxTitle("ROOM TYPE SUMMARY REPORT");
            System.out.println(MENU_BORDER);

            System.out.printf("| %-18s : %-35s |%n", "Report Period", reportPeriod);
            System.out.printf("| %-18s : %-35s |%n", "Tier Filter", loyaltyFilter);
            System.out.printf("| %-18s : %-35s |%n", "Room Filter", roomTypeFilter);

            System.out.println(MENU_BORDER);

            System.out.printf("| %-56s |%n", "No matching guest records found.");

            System.out.println(MENU_BORDER);

            InputUtility.pressEnterToContinue();

            return;
        }

        String[] roomLabels = {"Small", "Medium", "Big"};

        String[] roomGraph = buildVerticalBarChart("Room Type Report - " + reportPeriod, roomLabels, roomTypeCounts, "Room Types");

        String generatedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-55s |%n", centerText("ROOM TYPE SUMMARY REPORT", 55));

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-15s : %-36s |%n", "Report Period", reportPeriod);
        System.out.printf("| %-15s : %-36s |%n", "Tier Filter", loyaltyFilter);
        System.out.printf("| %-15s : %-36s |%n", "Room Filter", roomTypeFilter);
        System.out.printf("| %-15s : %-36s |%n", "Generated At", generatedDateTime);
        System.out.printf("| %-15s : %-36d |%n", "Total Guests", totalGuests);

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-4s | %-15s | %-12s | %-15s |%n", "No.", "Room Type", "Guest Count", "Percentage");

        System.out.println(REPORT_BORDER);

        for (int i = 0; i < roomLabels.length; i++) {

            double percentage = roomTypeCounts[i] * 100.0 / totalGuests;

            System.out.printf("| %-4d | %-15s | %-12d | %-14.2f%% |%n", i + 1, roomLabels[i] + " Room", roomTypeCounts[i], percentage);
        }

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-22s | %-12d | %-15s |%n", "Total Guests", totalGuests, "100.00%");

        System.out.println(REPORT_BORDER);

        System.out.println();

        printSingleGraph(roomGraph);

        System.out.println();

        System.out.println(REPORT_BORDER);

        System.out.printf("| %-55s |%n", centerText("END OF REPORT", 55));

        System.out.println(REPORT_BORDER);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Graph
    //==========================================================
    private String[] buildVerticalBarChart(String title, String[] labels, int[] values, String xAxisTitle) {

        int maximumValue = getMaximumValue(values);

        int lineCount = maximumValue + 4;

        String[] graphLines = new String[lineCount];

        graphLines[0] = title;
        graphLines[1] = "     ^";

        int lineIndex = 2;

        for (int level = maximumValue; level >= 1; level--) {

            String graphLine = String.format("%3d  |", level);

            for (int i = 0; i < values.length; i++) {

                if (values[i] >= level) {
                    graphLine += centerText(ANSI_GREEN_BACKGROUND + "     " + ANSI_RESET, 11);
                } else {
                    graphLine += centerText("", 11);
                }
            }

            graphLines[lineIndex] = graphLine;

            lineIndex++;
        }

        graphLines[lineIndex] = "   0  +" + repeatCharacter('-', labels.length * 11) + "> " + xAxisTitle;

        lineIndex++;

        String labelLine = "      ";

        for (String label : labels) {
            labelLine += centerText(label, 11);
        }

        graphLines[lineIndex] = labelLine;

        return graphLines;
    }

    //==========================================================
    // Print Graph
    //==========================================================
    private void printSingleGraph(String[] graph) {

        if (graph == null) {
            return;
        }

        for (String line : graph) {

            if (line != null) {
                System.out.println(line);
            }
        }
    }

    //==========================================================
    // Maximum
    //==========================================================
    private int getMaximumValue(int[] values) {

        int maximumValue = 1;

        if (values == null) {
            return maximumValue;
        }

        for (int value : values) {

            if (value > maximumValue) {
                maximumValue = value;
            }
        }

        return maximumValue;
    }

    //==========================================================
    // Read Choice
    //==========================================================
    private int readChoice(int minimum, int maximum, String prompt) {

        while (true) {

            System.out.print(prompt);

            int choice = InputUtility.getIntInput();

            if (choice >= minimum && choice <= maximum) {
                return choice;
            }

            System.out.printf("Invalid choice. Please enter %d to %d.%n", minimum, maximum);
        }
    }

    //==========================================================
    // Existing Guest Information
    //==========================================================
    private void displayGuestInformation(Guest guest) {

        if (guest == null) {

            printMessageBox("Guest information is not available.");

            return;
        }

        LoyaltyRecord loyaltyRecord = allocationControl.getLoyaltyRecordFromLoyaltyModule(guest.getGuestID());

        System.out.println(DETAIL_BORDER);

        printBoxTitle("GUEST INFORMATION");

        System.out.println(DETAIL_BORDER);

        System.out.printf("| %-18s : %-35s |%n", "Guest ID", limitText(guest.getGuestID(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Guest Name", limitText(guest.getGuestName(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Phone Number", limitText(guest.getPhoneNumber(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Loyalty Tier", limitText(guest.getLoyaltyTier(), 35));
        System.out.printf("| %-18s : %-35d |%n", "Priority", guest.getPriority());

        if (loyaltyRecord != null) {
            System.out.printf("| %-18s : %-35d |%n", "Lifetime Points", loyaltyRecord.getLifetimePoints());
        } else {
            System.out.printf("| %-18s : %-35s |%n", "Lifetime Points", "No Loyalty Record");
        }

        System.out.printf("| %-18s : %-35s |%n", "Room Type", limitText(guest.getRoomType(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Room Status", limitText(guest.getRoomStatus(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Check-In Date", limitText(guest.getCheckInDate(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Arrival DateTime", limitText(guest.getArrivalDateTime(), 35));

        System.out.println(DETAIL_BORDER);
    }

    //==========================================================
    // Table Header
    //==========================================================
    private void displayGuestTableHeader() {

        System.out.printf("| %-3s | %-8s | %-20s | %-13s | %-8s | %-10s | %-13s | %-10s | %-10s | %-16s |%n", "No.", "Guest ID", "Guest Name", "Loyalty Tier", "Priority", "Points", "Room Type", "Status", "Check-In", "Arrival DateTime");

        System.out.println(TABLE_BORDER);
    }

    //==========================================================
    // Table Row
    //==========================================================
    private void displayGuestTableRow(int number, Guest guest) {

        int lifetimePoints = allocationControl.getLifetimePointsFromLoyaltyModule(guest.getGuestID());

        String pointsDisplay;

        if (lifetimePoints < 0) {
            pointsDisplay = "N/A";
        } else {
            pointsDisplay = String.valueOf(lifetimePoints);
        }

        System.out.printf("| %-3d | %-8s | %-20s | %-13s | %-8d | %-10s | %-13s | %-10s | %-10s | %-16s |%n", number, limitText(guest.getGuestID(), 8), limitText(guest.getGuestName(), 20), limitText(guest.getLoyaltyTier(), 13), guest.getPriority(), pointsDisplay, limitText(guest.getRoomType(), 13), limitText(guest.getRoomStatus(), 10), limitText(guest.getCheckInDate(), 10), limitText(guest.getArrivalDateTime(), 16));
    }

    //==========================================================
    // Menu Item
    //==========================================================
    private void printMenuItem(int number, String text) {
        System.out.printf("| %-56s |%n", number + ". " + text);
    }

    //==========================================================
    // Box Title
    //==========================================================
    private void printBoxTitle(String title) {
        System.out.printf("|%-58s|%n", centerText(title, 58));
    }

    //==========================================================
    // Message
    //==========================================================
    private void printMessageBox(String message) {

        System.out.println(MENU_BORDER);

        System.out.printf("| %-56s |%n", limitText(message, 56));

        System.out.println(MENU_BORDER);
    }

    //==========================================================
    // Limit Text
    //==========================================================
    private String limitText(String text, int width) {

        if (text == null) {
            return "";
        }

        text = text.trim();

        if (text.length() <= width) {
            return text;
        }

        if (width <= 3) {
            return text.substring(0, width);
        }

        return text.substring(0, width - 3) + "...";
    }

    //==========================================================
    // Center Text
    //==========================================================
    private String centerText(String text, int width) {

        if (text == null) {
            text = "";
        }

        int visibleLength = removeAnsiCodes(text).length();

        if (visibleLength >= width) {
            return text;
        }

        int totalPadding = width - visibleLength;
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;

        return repeatCharacter(' ', leftPadding) + text + repeatCharacter(' ', rightPadding);
    }

    //==========================================================
    // Repeat
    //==========================================================
    private String repeatCharacter(char character, int total) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < total; i++) {
            result.append(character);
        }

        return result.toString();
    }

    //==========================================================
    // Remove ANSI
    //==========================================================
    private String removeAnsiCodes(String text) {

        if (text == null) {
            return "";
        }

        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    //==========================================================
    // Load Guest Database
    //==========================================================
    public void loadGuestDatabase() {

        int totalLoaded = allocationControl.loadGuestDatabase();

        System.out.println(totalLoaded + " Waiting Guests Loaded Into VIP Stack!");
    }
}