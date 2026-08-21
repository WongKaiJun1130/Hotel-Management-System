package boundary;
/**
 *
 * @author Yeong Wei Kin
 */

import control.BookingControl;
import control.LoyaltyControl;
import entity.LoyaltyRecord;
import entity.Booking;
import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Room;
import entity.StatusEntry;  
import utility.InputUtility;
import utility.Utility;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;
import dao.GuestDao;
import dao.RoomDao;
import entity.Guest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BookingUI {
    private BookingControl bookingControl;
    private GuestDao guestDatabase;
    private LoyaltyControl loyaltyControl;
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_RESET = "\u001B[0m";

    //==========================================================
    // Constructor
    //==========================================================
    public BookingUI() {
        bookingControl = new BookingControl();
        guestDatabase = new GuestDao();
        loyaltyControl = new LoyaltyControl();
    }

    public BookingUI(BookingControl bookingControl) {
        this.bookingControl = bookingControl;
        guestDatabase = new GuestDao();
        loyaltyControl = new LoyaltyControl();
    }

    //==========================================================
    // Booking Menu
    //==========================================================
    public void bookingMenu() {
        String[] options = {
            "1. Add Standard Reservation",
            "2. Process Next Reservation",
            "3. Cancel Booking",
            "4. Search Booking",
            "5. Edit Booking",
            "6. Display Booking Queue",
            "7. Room Availability Schedule",
            "8. Monthly Booking Calendar Report",
            "9. Monthly Room Occupancy Report",
            "0. Back"
        };

        Runnable[] actions = {
            () -> addBooking(),
            () -> processNextReservation(),
            () -> cancelBooking(),
            () -> searchBooking(),
            () -> editBooking(),
            () -> displayBooking(),
            () -> displayRoomSchedule(),
            () -> displayBookingCalendar(),
            () -> displayOccupancy(),
            () -> {}
        };
        Utility.customMenu( options, "STANDARD BOOKING MANAGEMENT", "Enter Choice: ", actions);
    }

    //==========================================================
    // Add Standard Reservation
    // FIFO Queue Enqueue
    //==========================================================
    private void addBooking() {
        InputUtility.clearScreen();
        Utility.printHeader("ADD STANDARD RESERVATION");        
        String bookingID = bookingControl.generateBookingID();
        System.out.println("Booking ID      : " + bookingID);
        System.out.print("Guest Name      : ");
        String guestName = InputUtility.getValidName();
        System.out.print("Phone Number    : ");
        String phoneNumber = InputUtility.getPhoneInput();
       
        Guest existingGuest = guestDatabase.searchGuestByNameAndPhone(guestName, phoneNumber);
        String guestID;
        if (existingGuest != null) {
            guestID = existingGuest.getGuestID();
            System.out.println("Guest ID        : " + guestID);
            System.out.println("Existing guest found.");
        } else {
            guestID = guestDatabase.generateGuestID();
            System.out.println("Guest ID        : " + guestID);
            System.out.println("New guest.");
        }
        System.out.print("Room Type       : ");
        String roomType = InputUtility.getValidRoomType();
        roomType = InputUtility.capitalizeFirstLetter(roomType);
        String roomID = bookingControl.assignRoomID(roomType);
        if (roomID == null) {
            System.out.println("\nNo available " + roomType + " room.");
            InputUtility.pressEnterToContinue();
            return;
        }
        System.out.println("Room ID         : " + roomID);
        System.out.print("Check-In Date   : ");
        String checkInDate = InputUtility.getDateInput();
        String checkOutDate = InputUtility.getCheckOutDate(checkInDate);
        
        //====================================================
        // CREATE NEW GUEST ONLY IF GUEST DOES NOT EXIST
        //====================================================
        if (existingGuest == null) {
            String guestRoomType;
            if (roomType.equalsIgnoreCase("Single")) {
                guestRoomType = "Single Room";
            } else if (roomType.equalsIgnoreCase("Medium")) {
                guestRoomType = "Medium Room";
            } else {
                guestRoomType = "Large Room";
            }
            String guestCheckInDate = checkInDate.replace("-", "/");
            Guest newGuest = new Guest(
                    guestID,
                    guestName,
                    phoneNumber,
                    "Standard",
                    guestRoomType,
                    "Waiting",
                    guestCheckInDate,
                    guestCheckInDate + " 12:00"
            );

            boolean guestAdded = guestDatabase.addGuest(newGuest);

            if (guestAdded) {
                loyaltyControl.createLoyaltyMember(newGuest,0);
            }
        }
        Booking booking = new Booking(
                bookingID,
                guestName,
                phoneNumber,
                guestID,
                roomType,
                roomID,
                checkInDate,
                checkOutDate,
                "Waiting"
        );
        if(bookingControl.addBooking(booking)) {
            Utility.printMessageBox("Reservation added successfully.");
        }
        else {
            Utility.printMessageBox("\nFailed to add reservation.");
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Process Next Reservation
    // FIFO Queue Dequeue
    //==========================================================
    private void processNextReservation() {
        InputUtility.clearScreen();
        System.out.println("========== PROCESS NEXT RESERVATION ==========");
        Booking booking = bookingControl.processNextReservation();
        if(booking == null) {
            System.out.println("\nNo waiting reservation.");
        } 
        else {
            System.out.println("Reservation Processed: " + "");
            System.out.println();
            displayTableHeader();
            displayBookingRow(booking);
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            System.out.println("\nReservation processed successfully.");
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Cancel Booking
    //==========================================================
    private void cancelBooking() {
        InputUtility.clearScreen();
        System.out.println("========== CANCEL BOOKING ==========");

        while(true) {
            System.out.print("Enter Booking ID : ");
            String bookingID = InputUtility.getStringInput();
            Booking booking = bookingControl.getBookingByID(bookingID);

            if(booking == null) {
                System.out.println("\nBooking ID does not exist. Please enter again.");
                continue;  // return to the start of while loop
            }
            System.out.println("\n========== BOOKING DETAILS ==========");
            displayBookingInformation(booking);
            System.out.print("\nProceed with cancellation? (Y/N): ");
            String choice = InputUtility.getYOrNInput();

            if(choice.equalsIgnoreCase("Y")) {
                if(bookingControl.cancelBooking(bookingID)) {
                    System.out.println("\nBooking cancelled successfully.");
                }
                else {
                    System.out.println("\nFailed to cancel booking.");
                }
            }
            else {
                System.out.println("\nCancellation cancelled.");
            }
            break;
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Search Booking
    // Search by Booking ID / Guest Name / Phone Number
    //==========================================================
    private void searchBooking() {
        InputUtility.clearScreen();
        System.out.println("========== SEARCH BOOKING ==========");
        System.out.print("Enter Booking ID / Guest Name / Phone Number : ");
        String keyword = InputUtility.getStringInput();
        ListInterface<Booking> result = bookingControl.searchBooking(keyword);
        if (result.isEmpty()) {
            System.out.println("\nNo booking found.");
        } else {
            System.out.println("\n========== SEARCH RESULT ==========");
            displayTableHeader();
            for (int i = 1; i <= result.getSize(); i++) {
                Booking booking = result.getEntry(i);
                displayBookingRow(booking);
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            System.out.println("Total Found : " + result.getSize());
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Edit Booking
    //==========================================================
    private void editBooking() {
        InputUtility.clearScreen();
        System.out.println("========== UPDATE BOOKING ==========");
        while(true) {
            System.out.print("Enter Booking ID : ");
            String bookingID = InputUtility.getStringInput();
            Booking booking = bookingControl.getBookingByID(bookingID);
                if(booking == null) {
                    System.out.println("\nBooking ID does not exist. Please enter again.");
                    continue;
                }

            int choice;
            do {
                System.out.println("\n========== BOOKING DETAILS ==========");
                displayBookingInformation(booking);

                System.out.println("\n========== EDIT OPTION ==========");
                System.out.println("1. Edit Guest Name");
                System.out.println("2. Edit Phone Number");
                System.out.println("3. Edit Room Type");
                System.out.println("4. Edit Check-In and Check-Out Date");
                System.out.println("5. Edit All Details");
                System.out.println("0. Back");
                System.out.print("\nEnter Choice : ");
                choice = InputUtility.getIntInput();

                switch(choice) {
                    case 1 -> {
                        System.out.print("\nNew Guest Name : ");
                        booking.setGuestName(InputUtility.getValidName());
                        if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nGuest name updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 2 -> {
                        System.out.print("\nNew Phone Number : ");
                        booking.setPhoneNumber(InputUtility.getPhoneInput());
                        if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nPhone number updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 3 -> {
                        System.out.print("\nNew Room Type : ");
                        String newRoomType = InputUtility.getValidRoomType();
                        newRoomType = InputUtility.capitalizeFirstLetter(newRoomType);
                        String oldRoomType = booking.getRoomType();
                        String oldRoomID = booking.getRoomID();
                        // Same room type
                        if (newRoomType.equalsIgnoreCase(oldRoomType)) {
                            System.out.println("\nRoom type unchanged.");
                            System.out.println("Room ID remains : " + oldRoomID);
                        }
                        // Different room type
                        else {
                            String newRoomID = bookingControl.assignRoomID(newRoomType);
                            if (newRoomID == null) {
                                System.out.println("\nNo available " + newRoomType + " room.");
                                System.out.println("Room Type remains : " + oldRoomType);
                                System.out.println("Room ID remains   : " + oldRoomID);
                            } else {
                                booking.setRoomType(newRoomType);
                                booking.setRoomID(newRoomID);
                                if (bookingControl.updateBooking(booking)) {
                                    System.out.println("\nRoom type updated successfully.");
                                    System.out.println("Old Room ID : " + oldRoomID);
                                    System.out.println("New Room ID : " + newRoomID);
                                } else {
                                    // Restore original information
                                    booking.setRoomType(oldRoomType);
                                    booking.setRoomID(oldRoomID);
                                    System.out.println("\nFailed to update booking.");
                                }
                            }
                        }
                    }

                    case 4 -> {
                        System.out.print("\nNew Check-In Date : ");
                        String checkInDate = InputUtility.getDateInput();
                        System.out.print("New");
                        String checkOutDate = InputUtility.getCheckOutDate(checkInDate);
                        booking.setCheckInDate(checkInDate);
                        booking.setCheckOutDate(checkOutDate);
                       if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nCheck-In and Check-Out date updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 5 -> {
                        //==================================================
                        // Edit All Details
                        //==================================================
                        String oldGuestName = booking.getGuestName();
                        String oldPhoneNumber = booking.getPhoneNumber();
                        String oldRoomType = booking.getRoomType();
                        String oldRoomID = booking.getRoomID();
                        String oldCheckInDate = booking.getCheckInDate();
                        String oldCheckOutDate = booking.getCheckOutDate();

                        //==================================================
                        // Enter New Information
                        //==================================================
                        System.out.print("\nNew Guest Name : ");
                        String newGuestName = InputUtility.getValidName();

                        System.out.print("New Phone Number : ");
                        String newPhoneNumber = InputUtility.getPhoneInput();

                        System.out.print("New Room Type  : ");
                        String newRoomType = InputUtility.getValidRoomType();
                        newRoomType = InputUtility.capitalizeFirstLetter(newRoomType);

                        System.out.print("New Check-In Date : ");
                        String newCheckInDate = InputUtility.getDateInput();

                        System.out.print("New");
                        String newCheckOutDate = InputUtility.getCheckOutDate(newCheckInDate);

                        //==================================================
                        // Display Old Information
                        //==================================================

                        System.out.println("\n==============================================");
                        System.out.println("              BOOKING COMPARISON");
                        System.out.println("==============================================");

                        System.out.println("\n       OLD INFORMATION");
                        System.out.println("----------------------------------------------");
                        System.out.println("Guest Name      : " + oldGuestName);
                        System.out.println("Phone Number    : " + oldPhoneNumber);
                        System.out.println("Room Type       : " + oldRoomType);
                        System.out.println("Room ID         : " + oldRoomID);
                        System.out.println("Check-In Date   : " + oldCheckInDate);
                        System.out.println("Check-Out Date  : " + oldCheckOutDate);
                        //==================================================
                        // Find New Room ID
                        //==================================================
                        String newRoomID = oldRoomID;
                        if (!newRoomType.equalsIgnoreCase(oldRoomType)) {
                            newRoomID = bookingControl.assignRoomID(newRoomType);
                            if (newRoomID == null) {
                                System.out.println("\nNo available " + newRoomType + " room.");
                                System.out.println("Edit cancelled.");
                                return;
                            }
                        }
                        //==================================================
                        // Display New Information
                        //==================================================
                        System.out.println("\n       NEW INFORMATION");
                        System.out.println("----------------------------------------------");
                        System.out.println("Guest Name      : " + newGuestName);
                        System.out.println("Phone Number    : " + newPhoneNumber);
                        System.out.println("Room Type       : " + newRoomType);
                        System.out.println("Room ID         : " + newRoomID);
                        System.out.println("Check-In Date   : " + newCheckInDate);
                        System.out.println("Check-Out Date  : " + newCheckOutDate);
                        //==================================================
                        // Show Changes
                        //==================================================
                        System.out.println("\n==============================================");
                        System.out.println("                  CHANGES");
                        System.out.println("==============================================");
                        System.out.println("Guest Name     : " + oldGuestName + "  ->  " + newGuestName);
                        System.out.println("Phone Number   : " + oldPhoneNumber + "  ->  " + newPhoneNumber);
                        System.out.println("Room Type      : " + oldRoomType + "  ->  " + newRoomType);
                        System.out.println("Room ID        : " + oldRoomID + "  ->  " + newRoomID);
                        System.out.println("Check-In Date  : " + oldCheckInDate + "  ->  " + newCheckInDate);
                        System.out.println("Check-Out Date : " + oldCheckOutDate + "  ->  " + newCheckOutDate);
                        //==================================================
                        // Confirm Update
                        //==================================================
                        System.out.print("\nConfirm update? (Y/N): ");
                        String confirmation = InputUtility.getYOrNInput();

                        if (confirmation.equalsIgnoreCase("Y")) {
                            booking.setGuestName(newGuestName);
                            booking.setPhoneNumber(newPhoneNumber);
                            booking.setRoomType(newRoomType);
                            booking.setRoomID(newRoomID);
                            booking.setCheckInDate(newCheckInDate);
                            booking.setCheckOutDate(newCheckOutDate);
                            if (bookingControl.updateBooking(booking)) {
                                System.out.println("\nBooking updated successfully.");
                            } else {
                                System.out.println("\nFailed to update booking.");
                            }
                        } else {
                            System.out.println("\nUpdate cancelled.");
                        }
                    }

                    case 0 -> {}
                    default -> System.out.println("\nInvalid choice.Please enter again.");
                }
            }while(choice != 0);
            InputUtility.pressEnterToContinue();
            break;
        }
    }

    //==========================================================
    // Display Booking Queue
    //==========================================================
    private void displayBooking() {
        InputUtility.clearScreen();
        System.out.println("======================================= STANDARD RESERVATION QUEUE ===========================================\n");

        ListInterface<Booking> bookingList = bookingControl.getAllBooking();

        if (bookingList.isEmpty()) {
            System.out.println("No booking records available.");
        } else {
            bookingControl.sortBookingByID(bookingList);
            displayTableHeader();

            for (int i = 1; i <= bookingList.getSize(); i++) {
                Booking booking = bookingList.getEntry(i);
                displayBookingRow(booking);
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            System.out.println("Total Reservations : " + bookingList.getSize() + "/" + bookingControl.getTotalRoomCount());
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Display Booking Information
    //==========================================================
    private void displayBookingInformation(Booking booking) {
        System.out.println("----------------------------------------");
        System.out.println("Booking ID     : " + booking.getBookingID());
        System.out.println("Guest Name     : " + booking.getGuestName());
        System.out.println("Phone Number   : " + booking.getPhoneNumber());
        System.out.println("Room Type      : " + booking.getRoomType());
        System.out.println("Room ID        : " + booking.getRoomID());
        System.out.println("Check-In Date  : " + booking.getCheckInDate());
        System.out.println("Check-Out Date : " + booking.getCheckOutDate());
        System.out.println("Status         : " + booking.getRoomStatus());
        System.out.println("----------------------------------------");
    }

    //==========================================================
    // Display Room Schedule
    //==========================================================
    private void displayRoomSchedule(
            String roomType,
            String roomPrefix,
            int startRoom,
            int endRoom) {

        for (int i = startRoom; i <= endRoom; i++) {
            String roomID = roomPrefix + String.format("%02d", i);
            Booking roomBooking = null;
            // Find booking for this room
            ListInterface<Booking> list = bookingControl.getBookingSchedule();
            for (int j = 1; j <= list.getSize(); j++) {
                Booking booking = list.getEntry(j);
                if (booking.getRoomID().equalsIgnoreCase(roomID)) {
                    roomBooking = booking;
                    break;
                }
            }
            //==================================================
            // Room is occupied
            //==================================================
            if (roomBooking != null) {
                System.out.printf("%-12s %-10s %-12s %-20s %-15s %-12s %-12s %-12s%n",
                    roomBooking.getBookingID(),
                    roomID,
                    roomType,
                    roomBooking.getGuestName(),
                    roomBooking.getPhoneNumber(),
                    roomBooking.getCheckInDate(),
                    roomBooking.getCheckOutDate(),
                    roomBooking.getRoomStatus()
                );
            }
            //==================================================
            // Room is available
            //==================================================
            else {
                System.out.printf("%-12s %-10s %-12s %-20s %-15s %-12s %-12s %-12s%n",
                    "-",
                    roomID,
                    roomType,
                    "-",
                    "-",
                    "-",
                    "-",
                    "Available"
                );
            }
        }
    }

    //==========================================================
    // Display Booking Schedule / 30 Hotel Rooms
    //==========================================================
    private void displayRoomSchedule() {
        InputUtility.clearScreen();
        System.out.println("========================================== HOTEL ROOM SCHEDULE ===============================================");
        System.out.printf("%-12s %-10s %-12s %-20s %-15s %-12s %-12s %-12s%n",
            "Booking ID",
            "Room ID",
            "Room Type",
            "Guest Name",
            "Phone Number",
            "Check-In",
            "Check-Out",
            "Status"
        );

        System.out.println("--------------------------------------------------------------------------------------------------------------");
        //======================================================
        // Single Rooms S01 - S10
        //======================================================
        displayRoomSchedule("Single", "S", 1, 10);
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        //======================================================
        // Medium Rooms M01 - M10
        //======================================================
        displayRoomSchedule("Medium", "M", 1, 10);
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        //======================================================
        // Large Rooms L01 - L10
        //======================================================
        displayRoomSchedule("Large", "L", 1, 10);
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Rooms : 30");
        InputUtility.pressEnterToContinue();
    }

    private void displayBookingCalendar() {
        InputUtility.clearScreen();
        System.out.println("======== MONTHLY BOOKING CALENDAR ========");
        System.out.print("Enter Year  : ");
        int year = InputUtility.getIntInput();
        int month;
        while (true) {
            System.out.print("Enter Month : ");
            month = InputUtility.getIntInput();
            if (month >= 1 && month <= 12) {
                break;
            }
            System.out.println("Invalid month. Please enter 1 to 12.");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        System.out.println("\n=============== " + yearMonth.getMonth() + " " + year + " ================");
        System.out.println(" Mon   Tue   Wed   Thu   Fri   Sat   Sun");
        System.out.println("------------------------------------------");

        LocalDate firstDay = yearMonth.atDay(1);
        int startDay = firstDay.getDayOfWeek().getValue();
        for (int i = 1; i < startDay; i++) {
            System.out.print("      ");
        }

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            if (bookingControl.hasBookingOnDate(currentDate)) {
                System.out.printf("%3d*  ", day);
            } else {
                System.out.printf("%3d   ", day);
            }

            if (currentDate.getDayOfWeek().getValue() == 7) {
                System.out.println();
            }
        }

        System.out.println();
        System.out.println("\n* = Has Booking");

        ListInterface<Booking> monthlyBookings = bookingControl.getBookingsByMonth(yearMonth);
        System.out.println("Total Bookings This Month : " + monthlyBookings.getSize());

        displayMonthlyBookingDetails(yearMonth);
        displayMonthlyBookingSummary(yearMonth);
        InputUtility.pressEnterToContinue();
    }

    private void displayMonthlyBookingDetails(YearMonth yearMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.println("\n============================================== MONTHLY BOOKING DETAILS ==========================================================");

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            ListInterface<Booking> bookings = bookingControl.getBookingsByDate(currentDate);
            if (!bookings.isEmpty()) {
                System.out.println("\n" + currentDate.format(formatter));
                System.out.println("------------------------------------------------------------------------------------------------------------------------------");

                System.out.printf(
                        "%-10s %-10s %-17s %-12s %-10s %-10s %-12s %-12s %-10s %-12s%n",
                        "Booking",
                        "Guest ID",
                        "Guest Name",
                        "Loyalty",
                        "Room Type",
                        "Room ID",
                        "Check-In",
                        "Check-Out",
                        "Days",
                        "Status"
                );

                System.out.println("------------------------------------------------------------------------------------------------------------------------------");

                for (int i = 1; i <= bookings.getSize(); i++) {
                    Booking booking = bookings.getEntry(i);
                    long stayDays = bookingControl.getBookingStayDays(booking);
                    Guest guest = guestDatabase.searchGuestByID(booking.getGuestID());
                    String loyaltyTier = "N/A";
                    if (guest != null) {
                        loyaltyTier = guest.getLoyaltyTier();
                    }

                    System.out.printf(
                            "%-10s %-10s %-17s %-12s %-10s %-10s %-12s %-12s %-10d %-12s%n",
                            booking.getBookingID(),
                            booking.getGuestID(),
                            booking.getGuestName(),
                            loyaltyTier,
                            booking.getRoomType(),
                            booking.getRoomID(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            stayDays,
                            booking.getRoomStatus()
                    );
                }
            }
        }
    }
    
   private void displayMonthlyBookingSummary(YearMonth yearMonth) {
    ListInterface<Booking> bookingList = bookingControl.getBookingsByMonth(yearMonth);
    int waiting = 0;
    int served = 0;
    int checkedOut = 0;
    long totalStayDays = 0;
    int single = 0;
    int medium = 0;
    int large = 0;

    for (int i = 1; i <= bookingList.getSize(); i++) {
        Booking booking = bookingList.getEntry(i);
        if (booking == null) {
            continue;
        }

        if (booking.getRoomStatus().equalsIgnoreCase("Waiting")) {
            waiting++;
        } else if (booking.getRoomStatus().equalsIgnoreCase("Served")) {
            served++;
        } else if (booking.getRoomStatus().equalsIgnoreCase("Checked Out")) {
            checkedOut++;
        }

        if (booking.getRoomType().equalsIgnoreCase("Single")) {
            single++;
        } else if (booking.getRoomType().equalsIgnoreCase("Medium")) {
            medium++;
        } else if (booking.getRoomType().equalsIgnoreCase("Large")) {
            large++;
        }

        totalStayDays += bookingControl.getBookingStayDaysInMonth(booking, yearMonth);}
        int standard = 0;
        int platinum = 0;
        int diamond = 0;
        int elite = 0;

        int monthlyLoyaltyMembers = 0;

        DoublyLinkedList<String> countedGuestIDs = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking == null || booking.getGuestID() == null) {
                continue;
            }
            String guestID = booking.getGuestID();

            boolean alreadyCounted = false;

            for (int j = 1; j <= countedGuestIDs.getSize(); j++) {
                String countedID = countedGuestIDs.getEntry(j);

                if (countedID != null && countedID.equalsIgnoreCase(guestID)) {
                    alreadyCounted = true;
                    break;
                }
            }
            if (alreadyCounted) {
                continue;
            }
            LoyaltyRecord loyaltyRecord = loyaltyControl.searchGuest(guestID);

            if (loyaltyRecord == null) {
                continue;
            }
            
            countedGuestIDs.add(guestID);
            monthlyLoyaltyMembers++;
            String tier = loyaltyRecord.getLoyaltyTier();

            if (tier.equalsIgnoreCase("Standard")) {
                standard++;
            } else if (tier.equalsIgnoreCase("Platinum")) {
                platinum++;
            } else if (tier.equalsIgnoreCase("Diamond")) {
                diamond++;
            } else if (tier.equalsIgnoreCase("Elite")) {
                elite++;
            }
        }

        System.out.println("\n================ MONTHLY BOOKING SUMMARY ================");
        System.out.println("Month                    : " + yearMonth.getMonth() + " " + yearMonth.getYear());
        System.out.println("Total Bookings           : " + bookingList.getSize());
        System.out.println("Waiting Bookings         : " + waiting);
        System.out.println("Served Bookings          : " + served);
        System.out.println("Checked Out Bookings     : " + checkedOut);
        System.out.println("Total Stay Days          : " + totalStayDays);
        System.out.println( "---------------------------------------------------------");
        System.out.println("Single Room Bookings     : " + single);
        System.out.println("Medium Room Bookings     : " + medium);
        System.out.println("Large Room Bookings      : " + large);

        System.out.println("---------------------------------------------------------");
        System.out.println("LOYALTY TIER BREAKDOWN");
        System.out.println("Standard Members         : " + standard);
        System.out.println("Platinum Members         : " + platinum);
        System.out.println("Diamond Members          : " + diamond);
        System.out.println("Elite Members            : " + elite);
        System.out.println("Total Loyalty Members    : " + monthlyLoyaltyMembers);
        System.out.println("=========================================================");
    }
    
    private void displayOccupancy() {
        InputUtility.clearScreen();
        System.out.println("========== MONTHLY ROOM OCCUPANCY REPORT ==========");

        System.out.print("Enter Year  : ");
        int year = InputUtility.getIntInput();
        int month;
        
        while (true) {
            System.out.print("Enter Month : ");
            month = InputUtility.getIntInput();
            if (month >= 1 && month <= 12) {
                break;
            }
            System.out.println("Invalid month. Please enter 1 to 12.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        double singleRate = bookingControl.getRoomOccupancyRate(yearMonth, "Single");
        double mediumRate = bookingControl.getRoomOccupancyRate(yearMonth, "Medium");
        double largeRate = bookingControl.getRoomOccupancyRate(yearMonth, "Large");
        RoomDao roomDao =
        new RoomDao();

        DoublyLinkedList<Room> rooms = roomDao.retrieveFromFile();

        int singleRooms = 0;
        int mediumRooms = 0;
        int largeRooms = 0;

        int singleReady = 0;
        int mediumReady = 0;
        int largeReady = 0;

        for (int i = 1; i <= rooms.getSize(); i++) {
            Room room = rooms.getEntry(i);
            if (room == null) {
                continue;
            }

            StatusEntry currentStatus = room.getStatusHistory().getCurrentData();
            boolean ready = currentStatus != null && currentStatus.getStatusCode() == RoomStatusUtil.Ready_For_CheckIN;

            switch (room.getRoomType()) {
                case RoomTypeUtil.Single_Room:
                    singleRooms++;
                    if (ready) {
                        singleReady++;
                    }
                    break;

                case RoomTypeUtil.Medium_Room:
                    mediumRooms++;
                    if (ready) {
                        mediumReady++;
                    }
                    break;

                case RoomTypeUtil.Large_Room:
                    largeRooms++;
                    if (ready) {
                        largeReady++;
                    }
                    break;
            }
        }
        
        String generatedAt = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println();
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println("|                               ROOM OCCUPANCY REPORT                               |");
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.printf("| %-18s : %-58s   |%n", "Generated At", generatedAt);
        System.out.printf("| %-18s : %-58s   |%n", "Report Month", yearMonth.getMonth() + " " + yearMonth.getYear());
        System.out.printf("| %-18s : %-58d   |%n", "Total Room Types", bookingControl.getRoomTypeCount());
        System.out.println("+------+---------------+-------------+-------------+------------------+-------------+");
        System.out.printf(
                "| %-4s | %-13s | %-11s | %-11s | %-16s | %-11s |%n",
                "No.",
                "Room Type",
                "Rooms",
                "Ready Rooms",
                "Occupancy Rate",
                "Status"
        );

        System.out.println( "+------+---------------+-------------+-------------+------------------+-------------+");

        String singleRateText = String.format("%.2f%%", singleRate);
        String mediumRateText = String.format("%.2f%%", mediumRate);
        String largeRateText = String.format("%.2f%%", largeRate);
       
        System.out.printf(
                "| %-4d | %-13s | %-11d | %-11d | %-16s | %-11s |%n",
                1,
                "Single",
                singleRooms,
                singleReady,
                singleRateText,
                getOccupancyStatus(singleRate)
        );
       
        System.out.printf(
                "| %-4d | %-13s | %-11d | %-11d | %-16s | %-11s |%n",
                2,
                "Medium",
                mediumRooms,
                mediumReady,
                mediumRateText,
                getOccupancyStatus(mediumRate)
        );

        System.out.printf(
                "| %-4d | %-13s | %-11d | %-11d | %-16s | %-11s |%n",
                3,
                "Large",
                largeRooms,
                largeReady,
                largeRateText,
                getOccupancyStatus(largeRate)
        );
        System.out.println("+------+---------------+-------------+-------------+------------------+-------------+");
        double averageRate = (singleRate + mediumRate + largeRate) / 3.0;
        String averageRateText = String.format("%.2f%%", averageRate);

        System.out.printf(
                "| %-48s | %-16s | %-11s |%n",
                "Average Occupancy Rate",
                averageRateText,
                getOccupancyStatus(averageRate)
        );
        System.out.println("+-----------------------------------------------------------------------------------+");
        System.out.println();
        int[] occupancyValues = {
            (int) Math.round(singleRate * 10),
            (int) Math.round(mediumRate * 10),
            (int) Math.round(largeRate * 10)
        };

        String[] roomLabels = {
            "Single",
            "Medium",
            "Large"
        };

        String[] occupancyGraph = buildVerticalBarChart(
                "Room Occupancy Rate",
                roomLabels,
                occupancyValues,
                "Room Types"
        );

        printSingleGraph(occupancyGraph);
        InputUtility.pressEnterToContinue();
    }
    
    private String getOccupancyStatus(double rate) {
        if (rate >= 75) {
            return "High";
        } else if (rate >= 40) {
            return "Moderate";
        } else {
            return "Low";
        }
    }
    
    //==========================================================
    // Build Vertical Bar Chart
    //==========================================================
    private String[] buildVerticalBarChart(
            String title,
            String[] labels,
            int[] values,
            String xAxisTitle) {

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

        graphLines[lineIndex] =
                "   0  +"
                + repeatCharacter('-', labels.length * 11)
                + "> "
                + xAxisTitle;

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
    
    private String repeatCharacter(char character, int total) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < total; i++) {
            result.append(character);
        }   
        return result.toString();
    }
    
    private String removeAnsiCodes(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll( "\u001B\\[[;\\d]*m", "");
    }
    
    //==========================================================
    // Display Booking Table Header
    //==========================================================
   private void displayTableHeader() {
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-13s %-20s %-15s %-12s %-10s %-12s %-12s %-12s%n",
            "Booking ID",
            "Guest Name",
            "Phone Number",
            "Room Type",
            "Room ID",
            "Check-In",
            "Check-Out",
            "Status"
        );
        System.out.println("--------------------------------------------------------------------------------------------------------------");
    }

    //==========================================================
    // Display One Booking as Row
    //==========================================================
    private void displayBookingRow(Booking booking) {
         System.out.printf("%-13s %-20s %-15s %-12s %-10s %-12s %-12s %-12s%n",
             booking.getBookingID(),
             booking.getGuestName(),
             booking.getPhoneNumber(),
             booking.getRoomType(),
             booking.getRoomID(),
             booking.getCheckInDate(),
             booking.getCheckOutDate(),
             booking.getRoomStatus()
         );
    }
    
   
}
    
