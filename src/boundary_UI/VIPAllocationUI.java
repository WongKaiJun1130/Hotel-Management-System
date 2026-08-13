package boundary_UI;

import control.VIPAllocationControl;
import entity.Guest;
import adt.ListInterface;
import utility.InputUtility;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class VIPAllocationUI {

    private VIPAllocationControl allocationControl;

    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String MENU_BORDER = "+----------------------------------------------------------+";
    private static final String DETAIL_BORDER = "+----------------------------------------------------------+";
    private static final String TABLE_BORDER = "+-----+----------+----------------------+---------------+----------+---------------+------------+------------+------------------+";
    private static final String REPORT_BORDER = "+------+-----------------+--------------+-----------------+";

    //==========================================================
    // Default Constructor
    //==========================================================
    public VIPAllocationUI() {
        allocationControl = new VIPAllocationControl();
    }

    //==========================================================
    // Constructor With Control
    //==========================================================
    public VIPAllocationUI(VIPAllocationControl allocationControl) {
        this.allocationControl = allocationControl;
    }

    //==========================================================
    // Main Allocation Menu
    //==========================================================
    public void allocationMenu() {

        int choice;

        do {

            InputUtility.clearScreen();

            System.out.println(MENU_BORDER);
            printBoxTitle("VIP & LOYALTY ROOM ALLOCATION MENU");
            System.out.println(MENU_BORDER);
            printMenuItem(1, "Add Guest To Allocation Queue");
            printMenuItem(2, "Allocate Room");
            printMenuItem(3, "View Next Priority Guest");
            printMenuItem(4, "Display Allocation Queue");
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
                    displayAllocationQueue();
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
                    System.out.println("\nInvalid choice.");
                    InputUtility.pressEnterToContinue();
                    break;
            }

        } while (choice != 0);
    }

    //==========================================================
    // 1. Add Guest To Allocation Queue
    //==========================================================
    private void addGuest() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("ADD GUEST TO ALLOCATION QUEUE");
        System.out.println(MENU_BORDER);

        String guestID = allocationControl.generateGuestID();

        System.out.printf("| %-18s : %-35s |%n", "Guest ID", guestID);
        System.out.println(MENU_BORDER);

        String guestName = inputValidGuestName();
        String phoneNumber = inputValidPhoneNumber();
        String loyaltyTier = inputLoyaltyTier();
        String roomType = inputRoomType();
        String checkInDate = inputCheckInDate();
        String arrivalDateTime = inputArrivalDateTime();

        Guest guest = new Guest(guestID, guestName, phoneNumber, loyaltyTier, roomType, "Waiting", checkInDate, arrivalDateTime);

        System.out.println();
        displayGuestInformation(guest);

        System.out.println();
        System.out.println(MENU_BORDER);
        printMenuItem(1, "Confirm");
        printMenuItem(2, "Cancel");
        System.out.println(MENU_BORDER);

        int confirmation = readChoice(1, 2, "Enter Choice: ");

        if (confirmation == 1) {

            boolean added = allocationControl.addGuestToQueue(guest);

            System.out.println();

            if (added) {
                printMessageBox("Guest added to allocation queue successfully.");
            } else {
                printMessageBox("Unable to add guest. Guest ID may already exist.");
            }

        } else {

            System.out.println();
            printMessageBox("Add guest cancelled.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Validate Guest Name
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
    // Validate Phone Number
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
    // Input Loyalty Tier
    //==========================================================
    private String inputLoyaltyTier() {

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("LOYALTY TIER");
        System.out.println(MENU_BORDER);
        printMenuItem(1, "Elite");
        printMenuItem(2, "Diamond");
        printMenuItem(3, "Platinum");
        printMenuItem(4, "Standard");
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
    // Input Room Type
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
    // Input Check-In Date
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
    // Input New Check-In Date
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

                System.out.println("Invalid date. Example: 20/08/2026");
            }
        }
    }

    //==========================================================
    // Input Arrival Date And Time
    //==========================================================
    private String inputArrivalDateTime() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        String currentDateTime = LocalDateTime.now().format(formatter);

        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle("ARRIVAL DATE AND TIME");
        System.out.println(MENU_BORDER);
        System.out.printf("| %-18s : %-35s |%n", "Current DateTime", currentDateTime);
        printMenuItem(1, "Use Current Date And Time");
        printMenuItem(2, "Enter Arrival Date And Time");
        System.out.println(MENU_BORDER);

        int choice = readChoice(1, 2, "Enter Choice: ");

        if (choice == 1) {
            return currentDateTime;
        }

        return inputCustomArrivalDateTime(formatter);
    }

    //==========================================================
    // Input Custom Arrival Date And Time
    //==========================================================
    private String inputCustomArrivalDateTime(DateTimeFormatter formatter) {

        while (true) {

            System.out.print("Enter Arrival Date & Time (DD/MM/YYYY HH:MM): ");
            String arrivalDateTime = InputUtility.getStringInput();

            if (arrivalDateTime == null || arrivalDateTime.trim().isEmpty()) {
                System.out.println("Arrival date and time cannot be empty.");
                continue;
            }

            try {

                LocalDateTime parsedDateTime = LocalDateTime.parse(arrivalDateTime.trim(), formatter);

                if (parsedDateTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
                    System.out.println("Arrival date and time cannot be in the past.");
                    continue;
                }

                return parsedDateTime.format(formatter);

            } catch (DateTimeParseException exception) {

                System.out.println("Invalid date or time. Example: 29/08/2026 14:30");
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
            printMessageBox("No waiting guest in the allocation queue.");
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
    // 3. View Next Priority Guest
    //==========================================================
    private void viewNextPriorityGuest() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("NEXT PRIORITY GUEST");
        System.out.println(MENU_BORDER);

        Guest guest = allocationControl.getNextPriorityGuest();

        if (guest == null) {
            printMessageBox("No waiting guest in the allocation queue.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println();
        displayGuestInformation(guest);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 4. Display Allocation Queue
    //==========================================================
    private void displayAllocationQueue() {

        InputUtility.clearScreen();

        ListInterface<Guest> guestList = allocationControl.getAllGuestAllocations();

        if (guestList == null || guestList.isEmpty()) {
            printMessageBox("No guest records in the allocation queue.");
            return;
        }

        System.out.println(TABLE_BORDER);
        System.out.printf("| %-127s |%n", centerText("ALLOCATION QUEUE", 127));
        System.out.println(TABLE_BORDER);

        displayGuestTableHeader();

        for (int i = 1; i <= guestList.getSize(); i++) {

            Guest guest = guestList.getEntry(i);

            if (guest != null) {
                displayGuestTableRow(i, guest);
            }
        }

        System.out.println(TABLE_BORDER);
        System.out.printf("| %-18s : %-106d |%n", "Total Guests", guestList.getSize());
        System.out.println(TABLE_BORDER);
    }

    //==========================================================
    // 5. Search Guest Allocation
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
        System.out.printf("| %-127s |%n", centerText("SEARCH RESULT", 127));
        System.out.println(TABLE_BORDER);

        displayGuestTableHeader();

        for (int i = 1; i <= result.getSize(); i++) {

            Guest guest = result.getEntry(i);

            if (guest != null) {
                displayGuestTableRow(i, guest);
            }
        }

        System.out.println(TABLE_BORDER);
        System.out.printf("| %-24s : %-100d |%n", "Total Matching Guests", result.getSize());
        System.out.println(TABLE_BORDER);

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 6. View Available Rooms
    //==========================================================
    private void viewAvailableRooms() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("AVAILABLE ROOMS");
        System.out.println(MENU_BORDER);

        String[][] rooms = allocationControl.getAvailableRooms();

        if (rooms == null || rooms.length == 0) {
            printMessageBox("No room information is available.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println("+------+----------------------+----------------------+");
        System.out.printf("| %-4s | %-20s | %-20s |%n", "No.", "Room Type", "Availability");
        System.out.println("+------+----------------------+----------------------+");

        for (int i = 0; i < rooms.length; i++) {

            String roomType = rooms[i] != null && rooms[i].length > 0 ? rooms[i][0] : "";
            String availability = rooms[i] != null && rooms[i].length > 1 ? rooms[i][1] : "";

            System.out.printf("| %-4d | %-20s | %-20s |%n", i + 1, limitText(roomType, 20), limitText(availability, 20));
        }

        System.out.println("+------+----------------------+----------------------+");

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 7. Remove Guest Allocation
    //==========================================================
    private void removeGuestAllocation() {

        InputUtility.clearScreen();

        System.out.println(MENU_BORDER);
        printBoxTitle("REMOVE GUEST ALLOCATION");
        System.out.println(MENU_BORDER);

        if (allocationControl.isQueueEmpty()) {
            printMessageBox("No guest records in the allocation queue.");
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
    // Validate Guest ID
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
    // 8. Loyalty Tier Summary Report
    //==========================================================
    private void displayLoyaltyTierSummaryReport() {

        InputUtility.clearScreen();

        if (allocationControl.isQueueEmpty()) {
            printMessageBox("No guest records available for loyalty tier report.");
            InputUtility.pressEnterToContinue();
            return;
        }

        int[] loyaltyTierCounts = allocationControl.getLoyaltyTierCounts();
        int totalGuests = allocationControl.getTotalGuests();

        String[] loyaltyLabels = {"Elite", "Diamond", "Platinum", "Standard"};
        String[] loyaltyGraph = buildVerticalBarChart("Guests by Loyalty Tier", loyaltyLabels, loyaltyTierCounts, "Loyalty Tiers");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String generatedDateTime = LocalDateTime.now().format(formatter);

        System.out.println(REPORT_BORDER);
        System.out.printf("| %-55s |%n", centerText("LOYALTY TIER SUMMARY REPORT", 55));
        System.out.println(REPORT_BORDER);
        System.out.printf("| %-15s : %-36s |%n", "Generated At", generatedDateTime);
        System.out.printf("| %-15s : %-36d |%n", "Total Guests", totalGuests);
        System.out.println(REPORT_BORDER);
        System.out.printf("| %-4s | %-15s | %-12s | %-15s |%n", "No.", "Loyalty Tier", "Guest Count", "Percentage");
        System.out.println(REPORT_BORDER);

        for (int i = 0; i < loyaltyLabels.length; i++) {

            double percentage = totalGuests == 0 ? 0.0 : loyaltyTierCounts[i] * 100.0 / totalGuests;

            System.out.printf("| %-4d | %-15s | %-12d | %-14.2f%% |%n", i + 1, loyaltyLabels[i], loyaltyTierCounts[i], percentage);
        }

        System.out.println(REPORT_BORDER);
        System.out.printf("| %-22s | %-12d | %-15s |%n", "Total Guests", totalGuests, "100.00%");
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
    //==========================================================
    private void displayRoomTypeSummaryReport() {

        InputUtility.clearScreen();

        if (allocationControl.isQueueEmpty()) {
            printMessageBox("No guest records available for room type report.");
            InputUtility.pressEnterToContinue();
            return;
        }

        int[] roomTypeCounts = allocationControl.getRoomTypeCounts();
        int totalGuests = allocationControl.getTotalGuests();

        String[] roomLabels = {"Small", "Medium", "Big"};
        String[] roomGraph = buildVerticalBarChart("Guests by Room Type", roomLabels, roomTypeCounts, "Room Types");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String generatedDateTime = LocalDateTime.now().format(formatter);

        System.out.println(REPORT_BORDER);
        System.out.printf("| %-55s |%n", centerText("ROOM TYPE SUMMARY REPORT", 55));
        System.out.println(REPORT_BORDER);
        System.out.printf("| %-15s : %-36s |%n", "Generated At", generatedDateTime);
        System.out.printf("| %-15s : %-36d |%n", "Total Guests", totalGuests);
        System.out.println(REPORT_BORDER);
        System.out.printf("| %-4s | %-15s | %-12s | %-15s |%n", "No.", "Room Type", "Guest Count", "Percentage");
        System.out.println(REPORT_BORDER);

        for (int i = 0; i < roomLabels.length; i++) {

            double percentage = totalGuests == 0 ? 0.0 : roomTypeCounts[i] * 100.0 / totalGuests;

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
    // Build Vertical Bar Chart
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
    // Print Single Graph
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
    // Get Maximum Graph Value
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
    // Read Valid Number Choice
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
    // Display Guest Information
    //==========================================================
    private void displayGuestInformation(Guest guest) {

        if (guest == null) {
            printMessageBox("Guest information is not available.");
            return;
        }

        System.out.println(DETAIL_BORDER);
        printBoxTitle("GUEST INFORMATION");
        System.out.println(DETAIL_BORDER);
        System.out.printf("| %-18s : %-35s |%n", "Guest ID", limitText(guest.getGuestID(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Guest Name", limitText(guest.getGuestName(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Phone Number", limitText(guest.getPhoneNumber(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Loyalty Tier", limitText(guest.getLoyaltyTier(), 35));
        System.out.printf("| %-18s : %-35d |%n", "Priority", guest.getPriority());
        System.out.printf("| %-18s : %-35s |%n", "Room Type", limitText(guest.getRoomType(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Room Status", limitText(guest.getRoomStatus(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Check-In Date", limitText(guest.getCheckInDate(), 35));
        System.out.printf("| %-18s : %-35s |%n", "Arrival DateTime", limitText(guest.getArrivalDateTime(), 35));
        System.out.println(DETAIL_BORDER);
    }

    //==========================================================
    // Display Guest Table Header
    //==========================================================
    private void displayGuestTableHeader() {

        System.out.printf("| %-3s | %-8s | %-20s | %-13s | %-8s | %-13s | %-10s | %-10s | %-16s |%n", "No.", "Guest ID", "Guest Name", "Loyalty Tier", "Priority", "Room Type", "Status", "Check-In", "Arrival DateTime");
        System.out.println(TABLE_BORDER);
    }

    //==========================================================
    // Display Guest Table Row
    //==========================================================
    private void displayGuestTableRow(int number, Guest guest) {

        System.out.printf("| %-3d | %-8s | %-20s | %-13s | %-8d | %-13s | %-10s | %-10s | %-16s |%n", number, limitText(guest.getGuestID(), 8), limitText(guest.getGuestName(), 20), limitText(guest.getLoyaltyTier(), 13), guest.getPriority(), limitText(guest.getRoomType(), 13), limitText(guest.getRoomStatus(), 10), limitText(guest.getCheckInDate(), 10), limitText(guest.getArrivalDateTime(), 16));
    }

    //==========================================================
    // Print Menu Item
    //==========================================================
    private void printMenuItem(int number, String text) {
        System.out.printf("| %-56s |%n", number + ". " + text);
    }

    //==========================================================
    // Print Box Title
    //==========================================================
    private void printBoxTitle(String title) {
        System.out.printf("|%-58s|%n", centerText(title, 58));
    }

    //==========================================================
    // Print Message Box
    //==========================================================
    private void printMessageBox(String message) {

        System.out.println(MENU_BORDER);
        System.out.printf("| %-56s |%n", limitText(message, 56));
        System.out.println(MENU_BORDER);
    }

    //==========================================================
    // Limit Long Text
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
    // Repeat Character
    //==========================================================
    private String repeatCharacter(char character, int total) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < total; i++) {
            result.append(character);
        }

        return result.toString();
    }

    //==========================================================
    // Remove ANSI Colour Codes
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

        System.out.println(totalLoaded + " Guests Loaded!");
    }
}