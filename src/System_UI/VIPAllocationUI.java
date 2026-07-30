package System_UI;

import System_Control.VIPAllocationControl;
import System_Entity.Guest;
import System_adt.ListInterface;
import System_Utility.InputUtility;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VIPAllocationUI {

    private VIPAllocationControl allocationControl;

    // Green background colour for graph bars
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_RESET = "\u001B[0m";

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

            System.out.println("+----------------------------------------------------------+");
            System.out.println("|        VIP & LOYALTY ROOM ALLOCATION MENU               |");
            System.out.println("+----------------------------------------------------------+");
            System.out.println("| 1. Add Guest To Allocation Queue                        |");
            System.out.println("| 2. Allocate Room                                        |");
            System.out.println("| 3. View Next Priority Guest                             |");
            System.out.println("| 4. Display Allocation Queue                             |");
            System.out.println("| 5. Search Guest Allocation                              |");
            System.out.println("| 6. View Available Rooms                                 |");
            System.out.println("| 7. Summary Report                                       |");
            System.out.println("| 0. Back                                                 |");
            System.out.println("+----------------------------------------------------------+");
            System.out.print("Enter Choice: ");

            choice = InputUtility.getIntInput();

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
                    displaySummaryReport();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("\nInvalid Choice.");
                    InputUtility.pressEnterToContinue();
            }

        } while (choice != 0);
    }

    //==========================================================
    // 1. Add Guest To Allocation Queue
    //==========================================================
    private void addGuest() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------+");
        System.out.println("|           ADD GUEST TO ALLOCATION QUEUE                 |");
        System.out.println("+----------------------------------------------------------+");

        String guestID = allocationControl.generateGuestID();

        System.out.println("Guest ID        : " + guestID);

        System.out.print("Guest Name      : ");
        String guestName = InputUtility.getStringInput();

        System.out.print("Phone Number    : ");
        String phoneNumber = InputUtility.getStringInput();

        String loyaltyTier = inputLoyaltyTier();
        String roomType = inputRoomType();
        String checkInDate = inputCheckInDate();
        String arrivalDateTime = inputArrivalDateTime();

        Guest guest = new Guest(guestID, guestName, phoneNumber, loyaltyTier, roomType, "Waiting", checkInDate, arrivalDateTime);

        System.out.println();
        System.out.println("Guest Information");
        displayGuestInformation(guest);

        System.out.println();
        System.out.println("1. Confirm");
        System.out.println("2. Cancel");
        System.out.print("Choose: ");

        int confirmation = InputUtility.getIntInput();

        switch (confirmation) {

            case 1:

                boolean added = allocationControl.addGuestToQueue(guest);

                if (added) {
                    System.out.println("\nGuest added to allocation queue successfully.");
                } else {
                    System.out.println("\nUnable to add guest.");
                    System.out.println("Guest ID may already exist.");
                }

                break;

            case 2:
                System.out.println("\nAdd guest cancelled.");
                break;

            default:
                System.out.println("\nInvalid choice.");
                System.out.println("Guest was not added.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Input Loyalty Tier
    //==========================================================
    private String inputLoyaltyTier() {

        while (true) {

            System.out.println();
            System.out.println("+--------------------------------------+");
            System.out.println("|            LOYALTY TIER              |");
            System.out.println("+--------------------------------------+");
            System.out.println("| 1. Elite                             |");
            System.out.println("| 2. Diamond                           |");
            System.out.println("| 3. Platinum                          |");
            System.out.println("| 4. Standard                          |");
            System.out.println("+--------------------------------------+");
            System.out.print("Choose Loyalty Tier: ");

            int choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    return "Elite";

                case 2:
                    return "Diamond";

                case 3:
                    return "Platinum";

                case 4:
                    return "Standard";

                default:
                    System.out.println("\nInvalid choice.");
                    System.out.println("Please select 1 to 4.");
            }
        }
    }

    //==========================================================
    // Input Room Type
    //==========================================================
    private String inputRoomType() {

        while (true) {

            System.out.println();
            System.out.println("+--------------------------------------+");
            System.out.println("|              ROOM TYPE               |");
            System.out.println("+--------------------------------------+");
            System.out.println("| 1. Small Room                        |");
            System.out.println("| 2. Medium Room                       |");
            System.out.println("| 3. Big Room                          |");
            System.out.println("+--------------------------------------+");
            System.out.print("Choose Room Type: ");

            int choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    return "Small Room";

                case 2:
                    return "Medium Room";

                case 3:
                    return "Big Room";

                default:
                    System.out.println("\nInvalid choice.");
                    System.out.println("Please select 1 to 3.");
            }
        }
    }

    //==========================================================
    // Input Check-In Date
    //==========================================================
    private String inputCheckInDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String defaultDate = LocalDate.now().format(formatter);

        while (true) {

            System.out.println();
            System.out.println("+--------------------------------------+");
            System.out.println("|            CHECK-IN DATE             |");
            System.out.println("+--------------------------------------+");
            System.out.println("Default Check-In Date: " + defaultDate);
            System.out.println("1. Use Default Date");
            System.out.println("2. Change Date");
            System.out.print("Choose: ");

            int choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    return defaultDate;

                case 2:
                    return inputNewCheckInDate(formatter);

                default:
                    System.out.println("\nInvalid choice.");
                    System.out.println("Please select 1 or 2.");
            }
        }
    }

    //==========================================================
    // Input New Check-In Date
    //==========================================================
    private String inputNewCheckInDate(DateTimeFormatter formatter) {

        while (true) {

            System.out.print("Enter Check-In Date (DD/MM/YYYY): ");
            String newDate = InputUtility.getStringInput();

            try {

                LocalDate parsedDate = LocalDate.parse(newDate, formatter);

                return parsedDate.format(formatter);

            } catch (DateTimeParseException exception) {

                System.out.println("\nInvalid date.");
                System.out.println("Example: 20/07/2026");
            }
        }
    }

    //==========================================================
    // Input Arrival Date And Time
    //==========================================================
    private String inputArrivalDateTime() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        while (true) {

            String currentDateTime = LocalDateTime.now().format(formatter);

            System.out.println();
            System.out.println("+---------------------------------------------+");
            System.out.println("|          ARRIVAL DATE AND TIME              |");
            System.out.println("+---------------------------------------------+");
            System.out.println("Current Date & Time: " + currentDateTime);
            System.out.println("1. Use Current Date & Time");
            System.out.println("2. Enter Arrival Date & Time");
            System.out.print("Choose: ");

            int choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    return currentDateTime;

                case 2:
                    return inputCustomArrivalDateTime(formatter);

                default:
                    System.out.println("\nInvalid choice.");
                    System.out.println("Please select 1 or 2.");
            }
        }
    }

    //==========================================================
    // Input Custom Arrival Date And Time
    //==========================================================
    private String inputCustomArrivalDateTime(DateTimeFormatter formatter) {

        while (true) {

            System.out.print("Enter Arrival Date & Time (DD/MM/YYYY HH:MM): ");
            String arrivalDateTime = InputUtility.getStringInput();

            try {

                LocalDateTime parsedDateTime = LocalDateTime.parse(arrivalDateTime, formatter);

                return parsedDateTime.format(formatter);

            } catch (DateTimeParseException exception) {

                System.out.println("\nInvalid arrival date or time.");
                System.out.println("Example: 29/07/2026 14:30");
            }
        }
    }

    //==========================================================
    // 2. Allocate Room
    //==========================================================
    private void allocateRoom() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------+");
        System.out.println("|                    ALLOCATE ROOM                         |");
        System.out.println("+----------------------------------------------------------+");

        Guest guest = allocationControl.allocateRoom();

        if (guest == null) {
            System.out.println("\nNo waiting guest in the allocation queue.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println("\nRoom allocated to:");
        System.out.println();

        displayGuestInformation(guest);

        System.out.println("\nRoom allocated successfully.");

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 3. View Next Priority Guest
    //==========================================================
    private void viewNextPriorityGuest() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------+");
        System.out.println("|                 NEXT PRIORITY GUEST                      |");
        System.out.println("+----------------------------------------------------------+");

        Guest guest = allocationControl.getNextPriorityGuest();

        if (guest == null) {
            System.out.println("\nNo waiting guest in the allocation queue.");
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

        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                                   ALLOCATION QUEUE                                                                             |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------------------------------+");

        ListInterface<Guest> guestList = allocationControl.getAllGuestAllocations();

        if (guestList.isEmpty()) {
            System.out.println("\nNo guest records in the allocation queue.");
            return;
        }

        System.out.println();
        displayGuestTableHeader();

        for (int i = 1; i <= guestList.getSize(); i++) {

            Guest guest = guestList.getEntry(i);

            displayGuestTableRow(i, guest);
        }

        System.out.println("================================================================================================================================================================");
        System.out.println("Total Guests: " + guestList.getSize());
    }

    //==========================================================
    // 5. Search Guest Allocation
    //==========================================================
    private void searchGuestAllocation() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------+");
        System.out.println("|                SEARCH GUEST ALLOCATION                   |");
        System.out.println("+----------------------------------------------------------+");
        System.out.print("Enter Search Keyword: ");

        String keyword = InputUtility.getStringInput();

        ListInterface<Guest> result = allocationControl.searchGuest(keyword);

        if (result.isEmpty()) {
            System.out.println("\nGuest allocation record not found.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println();
        System.out.println("Search Result");
        System.out.println();

        displayGuestTableHeader();

        for (int i = 1; i <= result.getSize(); i++) {

            Guest guest = result.getEntry(i);

            displayGuestTableRow(i, guest);
        }

        System.out.println("================================================================================================================================================================");
        System.out.println("Total Matching Guests: " + result.getSize());

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 6. View Available Rooms
    //==========================================================
    private void viewAvailableRooms() {

        InputUtility.clearScreen();

        System.out.println("+----------------------------------------------------------+");
        System.out.println("|                   AVAILABLE ROOMS                        |");
        System.out.println("+----------------------------------------------------------+");

        String[][] rooms = allocationControl.getAvailableRooms();

        System.out.println();
        System.out.printf("%-5s %-20s %-15s%n", "No.", "Room Type", "Availability");
        System.out.println("---------------------------------------------");

        for (int i = 0; i < rooms.length; i++) {
            System.out.printf("%-5d %-20s %-15s%n", i + 1, rooms[i][0], rooms[i][1]);
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // 7. Display Summary Report
    //==========================================================
    private void displaySummaryReport() {

        InputUtility.clearScreen();

        if (allocationControl.isQueueEmpty()) {
            System.out.println("\nNo guest records available for summary report.");
            InputUtility.pressEnterToContinue();
            return;
        }

        int[] loyaltyTierCounts = allocationControl.getLoyaltyTierCounts();
        int[] roomTypeCounts = allocationControl.getRoomTypeCounts();

        String[] loyaltyLabels = {"Elite", "Diamond", "Platinum", "Standard"};
        String[] roomLabels = {"Small", "Medium", "Big"};

        String[] loyaltyGraph = buildVerticalBarChart("Guests by Loyalty Tier", loyaltyLabels, loyaltyTierCounts, "Loyalty Tiers");
        String[] roomGraph = buildVerticalBarChart("Guests by Room Type", roomLabels, roomTypeCounts, "Room Types");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String generatedDateTime = LocalDateTime.now().format(formatter);

        System.out.println("========================================================================================================================");
        System.out.println("                                             VIP ROOM ALLOCATION SUBSYSTEM");
        System.out.println();
        System.out.println("                                              SUMMARY OF ALLOCATION REPORT");
        System.out.println("                                    ------------------------------------------");
        System.out.println();
        System.out.println("Generated at: " + generatedDateTime);
        System.out.println("========================================================================================================================");
        System.out.printf("%-10s | %-20s | %-15s | %-10s | %-15s | %-18s%n", "Guest ID", "Guest Name", "Loyalty Tier", "Priority", "Room Type", "Arrival DateTime");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        ListInterface<Guest> guestList = allocationControl.getAllGuestAllocations();

        for (int i = 1; i <= guestList.getSize(); i++) {

            Guest guest = guestList.getEntry(i);

            System.out.printf("%-10s | %-20s | %-15s | %-10d | %-15s | %-18s%n", guest.getGuestID(), guest.getGuestName(), guest.getLoyaltyTier(), guest.getPriority(), guest.getRoomType(), guest.getArrivalDateTime());
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Number of Guests: " + allocationControl.getTotalGuests());
        System.out.println();
        System.out.println("                                      GRAPHICAL REPRESENTATION OF VIP ALLOCATION MODULE");
        System.out.println();

        printSideBySideGraphs(loyaltyGraph, roomGraph);

        System.out.println();
        System.out.println("Loyalty Tier Summary");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| Elite      : " + loyaltyTierCounts[0]);
        System.out.println("| Diamond    : " + loyaltyTierCounts[1]);
        System.out.println("| Platinum   : " + loyaltyTierCounts[2]);
        System.out.println("| Standard   : " + loyaltyTierCounts[3]);
        System.out.println("+----------------------------------------------------------+");

        System.out.println();
        System.out.println("Room Type Summary");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| Small Room  : " + roomTypeCounts[0]);
        System.out.println("| Medium Room : " + roomTypeCounts[1]);
        System.out.println("| Big Room    : " + roomTypeCounts[2]);
        System.out.println("+----------------------------------------------------------+");

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("                                                  END OF REPORT");
        System.out.println("========================================================================================================================");

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

        for (int i = 0; i < labels.length; i++) {
            labelLine += centerText(labels[i], 11);
        }

        graphLines[lineIndex] = labelLine;

        return graphLines;
    }

    //==========================================================
    // Print Two Graphs Side By Side
    //==========================================================
    private void printSideBySideGraphs(String[] leftGraph, String[] rightGraph) {

        int maximumLines = Math.max(leftGraph.length, rightGraph.length);

        for (int i = 0; i < maximumLines; i++) {

            String leftLine = "";
            String rightLine = "";

            if (i < leftGraph.length && leftGraph[i] != null) {
                leftLine = leftGraph[i];
            }

            if (i < rightGraph.length && rightGraph[i] != null) {
                rightLine = rightGraph[i];
            }

            System.out.println(padVisibleRight(leftLine, 68) + " | " + rightLine);
        }
    }

    //==========================================================
    // Get Maximum Graph Value
    //==========================================================
    private int getMaximumValue(int[] values) {

        int maximumValue = 1;

        for (int value : values) {

            if (value > maximumValue) {
                maximumValue = value;
            }
        }

        return maximumValue;
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
    // Pad Visible Text To Right
    //==========================================================
    private String padVisibleRight(String text, int width) {

        if (text == null) {
            text = "";
        }

        int visibleLength = removeAnsiCodes(text).length();

        if (visibleLength >= width) {
            return text;
        }

        return text + repeatCharacter(' ', width - visibleLength);
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
    // Display Guest Information
    //==========================================================
    private void displayGuestInformation(Guest guest) {

        System.out.println("-----------------------------------------------");
        System.out.println("Guest ID         : " + guest.getGuestID());
        System.out.println("Guest Name       : " + guest.getGuestName());
        System.out.println("Phone Number     : " + guest.getPhoneNumber());
        System.out.println("Loyalty Tier     : " + guest.getLoyaltyTier());
        System.out.println("Priority         : " + guest.getPriority());
        System.out.println("Room Type        : " + guest.getRoomType());
        System.out.println("Room Status      : " + guest.getRoomStatus());
        System.out.println("Check-In Date    : " + guest.getCheckInDate());
        System.out.println("Arrival DateTime : " + guest.getArrivalDateTime());
        System.out.println("-----------------------------------------------");
    }

    //==========================================================
    // Display Guest Table Header
    //==========================================================
    private void displayGuestTableHeader() {

        System.out.println("================================================================================================================================================================");
        System.out.printf("%-4s %-10s %-20s %-15s %-9s %-15s %-12s %-12s %-18s%n", "No.", "Guest ID", "Guest Name", "Loyalty Tier", "Priority", "Room Type", "Status", "Check-In", "Arrival DateTime");
        System.out.println("================================================================================================================================================================");
    }

    //==========================================================
    // Display Guest Table Row
    //==========================================================
    private void displayGuestTableRow(int number, Guest guest) {

        System.out.printf("%-4d %-10s %-20s %-15s %-9d %-15s %-12s %-12s %-18s%n", number, guest.getGuestID(), guest.getGuestName(), guest.getLoyaltyTier(), guest.getPriority(), guest.getRoomType(), guest.getRoomStatus(), guest.getCheckInDate(), guest.getArrivalDateTime());
    }

    //==========================================================
    // Load Guest Database
    //==========================================================
    public void loadGuestDatabase() {

        int totalLoaded = allocationControl.loadGuestDatabase();

        System.out.println(totalLoaded + " Guests Loaded!");
    }
}