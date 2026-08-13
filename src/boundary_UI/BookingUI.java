package boundary_UI;

import control.BookingControl;
import entity.Booking;
import adt.ListInterface;
import utility.InputUtility;

public class BookingUI {
    private BookingControl bookingControl;
    
    //==========================================================
    // Constructor
    //==========================================================
    public BookingUI() {
        bookingControl = new BookingControl();
    }

    public BookingUI(BookingControl bookingControl) {
        this.bookingControl = bookingControl;
    }

    //==========================================================
    // Booking Menu
    //==========================================================
    public void bookingMenu() {
        int choice;
        do {
            InputUtility.clearScreen();
            System.out.println("==========================================");
            System.out.println("        STANDARD BOOKING MANAGEMENT       ");
            System.out.println("==========================================");
            System.out.println("1. Add Standard Reservation");
            System.out.println("2. Process Next Reservation");
            System.out.println("3. View Next Waiting Reservation");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Search Booking");
            System.out.println("6. Edit Booking");
            System.out.println("7. Display Booking Queue");
            System.out.println("8. Display Waiting Booking");
            System.out.println("9. Display Completed Booking");
            System.out.println("10. Booking Schedule");
            System.out.println("11. Room Occupancy");
            System.out.println("12. Booking History");
            System.out.println("0. Back");
            System.out.print("\nEnter Choice : ");
            choice = InputUtility.getIntInput();

            switch(choice) {
                case 1 -> addBooking();

                case 2 -> processNextReservation();

                case 3 -> viewNextReservation();

                case 4 -> cancelBooking();

                case 5 -> searchBooking();

                case 6 -> editBooking();

                case 7 -> displayBooking();

                case 8 -> displayWaitingBooking();

                case 9 -> displayCompletedBooking();
                
                case 10 -> displayBookingSchedule();

                case 11 -> displayOccupancy();

                case 12 -> displayBookingHistory();
                
                case 0 -> {}

                default -> {
                    System.out.println("\nInvalid Choice.Plaese enter again.");
                    InputUtility.pressEnterToContinue();
                }
            }
        }while(choice != 0);
    }

    //==========================================================
    // Add Standard Reservation
    // FIFO Queue Enqueue
    //==========================================================
    private void addBooking() {
        InputUtility.clearScreen();
        System.out.println("========== ADD STANDARD RESERVATION ==========");
        String bookingID;
        // Check duplicate booking ID
        while(true) {
            System.out.print("Booking ID      : ");
            bookingID = InputUtility.getStringInput();
            Booking existingBooking = bookingControl.getBookingByID(bookingID);
            if(existingBooking != null) {
                System.out.println("\nBooking ID already exists.");
                System.out.println("Please enter another Booking ID.\n");
            }
            else {
                break;
            }
        }
        System.out.print("Guest Name      : ");
        String guestName = InputUtility.getValidName();
        System.out.print("Room Type       : ");
        String roomType = InputUtility.getValidRoomType();
        System.out.print("Check-In Date   : ");
        String checkInDate = InputUtility.getDateInput();
        System.out.print("Check-Out Date  : ");
        String checkOutDate = InputUtility.getDateInput();

        Booking booking = new Booking(
                bookingID,
                guestName,
                roomType,
                checkInDate,
                checkOutDate,
                "Waiting"
        );


        if(bookingControl.addBooking(booking)) {
            System.out.println("\nReservation added into queue successfully.");
        }
        else {
            System.out.println("\nFailed to add reservation.");
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
            System.out.println("\nRoom assigned to:");
            System.out.println("--------------------------------");
            System.out.println("Booking ID     : " + booking.getBookingID());
            System.out.println("Guest Name     : " + booking.getGuestName());
            System.out.println("Room Type      : " + booking.getRoomType());
            System.out.println("Check-In Date  : " + booking.getCheckInDate());
            System.out.println("Check-Out Date : " + booking.getCheckOutDate());
            System.out.println("Status         : " + booking.getRoomStatus());
            System.out.println("--------------------------------");
            System.out.println("\nReservation processed successfully.");
        }
        InputUtility.pressEnterToContinue();
    }
    
    //==========================================================
    // View Next Waiting Reservation
    // FIFO Queue Peek
    //==========================================================
    private void viewNextReservation() {
        InputUtility.clearScreen();
        System.out.println("========== NEXT WAITING RESERVATION ==========");
        Booking booking = bookingControl.getNextWaitingBooking();
        
        if(booking == null) {
            System.out.println("\nNo waiting reservation.");
        }
        else {
            System.out.println();
            System.out.println("Booking ID     : " + booking.getBookingID());
            System.out.println("Guest Name     : " + booking.getGuestName());
            System.out.println("Room Type      : " + booking.getRoomType());
            System.out.println("Check-In Date  : " + booking.getCheckInDate());
            System.out.println("Check-Out Date : " + booking.getCheckOutDate());
            System.out.println("Status         : " + booking.getRoomStatus());
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
    //==========================================================
    private void searchBooking() {
        InputUtility.clearScreen();
        System.out.println("========== SEARCH BOOKING ==========");

        while(true) {
            System.out.print("Enter Booking ID : ");
            String bookingID = InputUtility.getStringInput();
            Booking booking = bookingControl.getBookingByID(bookingID);

            if(booking == null) {
                System.out.println("\nBooking ID does not exist. Please enter again.");
                continue;
            }
            System.out.println("\n========== BOOKING DETAILS ==========");
            displayBookingInformation(booking);
            break;
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
                System.out.println("2. Edit Room Type");
                System.out.println("3. Edit Check-In Date");
                System.out.println("4. Edit Check-Out Date");
                System.out.println("5. Edit All Details");
                System.out.println("6. Back");
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
                        System.out.print("\nNew Room Type : ");
                        booking.setRoomType(InputUtility.getValidRoomType());
                       if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nRoom type updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 3 -> {
                        System.out.print("\nNew Check-In Date : ");
                        booking.setCheckInDate(InputUtility.getDateInput());
                       if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nCheck-In date updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 4 -> {
                        System.out.print("\nNew Check-Out Date : ");
                        booking.setCheckOutDate(InputUtility.getDateInput()); 
                       if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nCheck-Out date updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 5 -> {
                        System.out.print("\nNew Guest Name : ");
                        booking.setGuestName(InputUtility.getValidName());                   
                        
                        System.out.print("New Room Type : ");
                        booking.setRoomType(InputUtility.getValidRoomType());             

                        System.out.print("New Check-In Date : ");
                        booking.setCheckInDate(InputUtility.getDateInput());

                        System.out.print("New Check-Out Date : ");
                        booking.setCheckOutDate(InputUtility.getDateInput());
                        if (bookingControl.updateBooking(booking)) {
                            System.out.println("\nAll details updated.");
                        } else {
                            System.out.println("\nFailed to update booking.");
                        }
                    }

                    case 6 -> {}

                    default -> System.out.println("\nInvalid choice.Please enter again.");
                }
            }while(choice != 6);
            InputUtility.pressEnterToContinue();
            break;
        }
    }

    //==========================================================
    // Display Booking Queue
    //==========================================================
    private void displayBooking() {
        InputUtility.clearScreen();
        System.out.println("========== STANDARD RESERVATION QUEUE ==========");
        ListInterface<Booking> list = bookingControl.getAllBooking();
        
        if(list.isEmpty()) {
            System.out.println("\nNo booking records available.");
        }
        else {
            System.out.println("\nQueue Order:");
            for(int i = 1; i <= list.getSize(); i++) {
                Booking booking = list.getEntry(i);
                System.out.println("\n========== POSITION " + i + " ==========");
                displayBookingInformation(booking);
            }
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Display Booking Information
    //==========================================================
    private void displayBookingInformation(Booking booking) {
        System.out.println("--------------------------------");
        System.out.println("Booking ID     : " + booking.getBookingID());
        System.out.println("Guest Name     : " + booking.getGuestName());
        System.out.println("Room Type      : " + booking.getRoomType());
        System.out.println("Check-In Date  : " + booking.getCheckInDate());
        System.out.println("Check-Out Date : " + booking.getCheckOutDate());
        System.out.println("Status         : " + booking.getRoomStatus());
        System.out.println("--------------------------------");
    }
    
    //==========================================================
    // Display Waiting Booking
    //==========================================================
    private void displayWaitingBooking() {
        InputUtility.clearScreen();
        System.out.println("========== WAITING BOOKING ==========");
        ListInterface<Booking> list = bookingControl.getWaitingBookings();
        if(list.isEmpty()) {
            System.out.println("\nNo waiting booking available.");
        }
        else {
            for(int i = 1; i <= list.getSize(); i++) {
                Booking booking = list.getEntry(i);
                System.out.println("\n========== BOOKING " + i + " ==========");
                displayBookingInformation(booking);
            }
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Display Completed Booking
    //==========================================================
    private void displayCompletedBooking() {
        InputUtility.clearScreen();
        System.out.println("========== COMPLETED BOOKING ==========");
        ListInterface<Booking> list = bookingControl.getCompletedBookings();
        if(list.isEmpty()) {
            System.out.println("\nNo completed booking available.");
        }
        else {
            for(int i = 1; i <= list.getSize(); i++) {
                Booking booking = list.getEntry(i);
                System.out.println("\n========== BOOKING " + i + " ==========");
                displayBookingInformation(booking);
            }
        }
        InputUtility.pressEnterToContinue();
    }
    
    
    //==========================================================
    // Booking Schedule
    //==========================================================
    private void displayBookingSchedule() {
        InputUtility.clearScreen();
        System.out.println("========== BOOKING SCHEDULE ==========");
        ListInterface<Booking> list = bookingControl.getBookingSchedule();
            if (list.isEmpty()) {
                System.out.println("\nNo scheduled bookings available.");
            } else {
                for (int i = 1; i <= list.getSize(); i++) {
                    Booking booking = list.getEntry(i);
                    System.out.println("\n========== SCHEDULE " + i + " ==========");
                    System.out.println("Booking ID     : " + booking.getBookingID());
                    System.out.println("Guest Name     : " + booking.getGuestName());
                    System.out.println("Room Type      : " + booking.getRoomType());
                    System.out.println("Check-In Date  : " + booking.getCheckInDate());
                    System.out.println("Check-Out Date : " + booking.getCheckOutDate());
                    System.out.println("Status         : " + booking.getRoomStatus());
                    System.out.println("--------------------------------");
                }
            }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Room Occupancy
    //==========================================================
    private void displayOccupancy() {
        InputUtility.clearScreen();
        System.out.println("========== ROOM OCCUPANCY ==========");
        ListInterface<Booking> list = bookingControl.getOccupiedRooms();

        if (list.isEmpty()) {
            System.out.println("\nNo occupied rooms.");
        } else {
            for (int i = 1; i <= list.getSize(); i++) {
                Booking booking = list.getEntry(i);
                System.out.println("\n========== OCCUPIED ROOM " + i + " ==========");
                System.out.println("Booking ID     : " + booking.getBookingID());
                System.out.println("Guest Name     : " + booking.getGuestName());
                System.out.println("Room Type      : " + booking.getRoomType());
                System.out.println("Check-In Date  : "+ booking.getCheckInDate());
                System.out.println("Check-Out Date : " + booking.getCheckOutDate());
                System.out.println("Status         : " + booking.getRoomStatus());
                System.out.println("--------------------------------");
            }
        }
        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Booking History
    //==========================================================
    private void displayBookingHistory() {
        InputUtility.clearScreen();
        System.out.println("========== BOOKING HISTORY ==========");

        ListInterface<Booking> list = bookingControl.getBookingHistory();

        if (list.isEmpty()) {
            System.out.println("\nNo booking history available.");
        } else {
            for (int i = 1; i <= list.getSize(); i++) {
                Booking booking = list.getEntry(i);
                System.out.println("\n========== HISTORY " + i + " ==========");
                displayBookingInformation(booking);
            }
        }
        System.out.println("\nTotal Completed Bookings : " + bookingControl.getCompletedBookingCount());
        InputUtility.pressEnterToContinue();
    }
}
