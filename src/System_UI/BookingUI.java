package System_UI;

import System_Control.BookingControl;
import System_Entity.Booking;
import System_adt.ListInterface;
import System_Utility.InputUtility;

public class BookingUI {

    private BookingControl bookingControl;

    public BookingUI() {
        bookingControl = new BookingControl();
    }

    public BookingUI(BookingControl bookingControl) {
        this.bookingControl = bookingControl;
    }

    public void bookingMenu() {

        int choice;

        do {

            InputUtility.clearScreen();

            System.out.println("==================================");
            System.out.println("      BOOKING MANAGEMENT");
            System.out.println("==================================");
            System.out.println("1. Add Booking");
            System.out.println("2. Cancel Booking");
            System.out.println("3. Search Booking");
            System.out.println("4. Display All Booking");
            System.out.println("5. Update Booking Status");
            System.out.println("0. Back");
            System.out.print("\nEnter Choice : ");

            choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    addBooking();
                    break;

                case 2:
                    cancelBooking();
                    break;

                case 3:
                    searchBooking();
                    break;

                case 4:
                    displayBooking();
                    InputUtility.pressEnterToContinue();
                    break;

                case 5:
                    updateBookingStatus();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Invalid Choice.");
                    InputUtility.pressEnterToContinue();
            }

        } while (choice != 0);
    }

    //==========================================================
    // Add Booking
    //==========================================================
    private void addBooking() {

        InputUtility.clearScreen();

        System.out.println("========== ADD BOOKING ==========");

        System.out.print("Booking ID      : ");
        String bookingID = InputUtility.getStringInput();

        System.out.print("Guest Name      : ");
        String guestName = InputUtility.getStringInput();

        System.out.print("Room Type       : ");
        String roomType = InputUtility.getStringInput();

        System.out.print("Check-In Date   : ");
        String checkIn = InputUtility.getStringInput();

        System.out.print("Check-Out Date  : ");
        String checkOut = InputUtility.getStringInput();

        Booking booking = new Booking(
                bookingID,
                guestName,
                roomType,
                checkIn,
                checkOut,
                "Booked"
        );

        if (bookingControl.addBooking(booking)) {
            System.out.println("\nBooking added successfully.");
        } else {
            System.out.println("\nBooking ID already exists.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Cancel Booking
    //==========================================================
    private void cancelBooking() {

        InputUtility.clearScreen();

        System.out.println("========== CANCEL BOOKING ==========");

        System.out.print("Enter Booking ID : ");
        String bookingID = InputUtility.getStringInput();

        if (bookingControl.cancelBooking(bookingID)) {
            System.out.println("\nBooking cancelled successfully.");
        } else {
            System.out.println("\nBooking not found.");
        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Search Booking
    //==========================================================
    private void searchBooking() {

        InputUtility.clearScreen();

        System.out.println("========== SEARCH BOOKING ==========");

        System.out.print("Enter Booking ID : ");
        String bookingID = InputUtility.getStringInput();

        Booking booking = bookingControl.searchBooking(bookingID);

        if (booking != null) {

            System.out.println("\nBooking Found");
            System.out.println("--------------------------");
            System.out.println(booking);

        } else {

            System.out.println("\nBooking not found.");

        }

        InputUtility.pressEnterToContinue();
    }

    //==========================================================
    // Display Booking
    //==========================================================
    private void displayBooking() {

        InputUtility.clearScreen();

        System.out.println("========== BOOKING LIST ==========");

        ListInterface<Booking> list = bookingControl.getAllBooking();

        if (list.isEmpty()) {

            System.out.println("No booking records.");

            return;
        }

        for (int i = 1; i <= list.getSize(); i++) {

            System.out.println(list.getEntry(i));
            System.out.println("----------------------------------");

        }

    }

    //==========================================================
    // Update Booking Status
    //==========================================================
    private void updateBookingStatus() {

        InputUtility.clearScreen();

        System.out.println("====== UPDATE BOOKING STATUS ======");

        System.out.print("Enter Booking ID : ");
        String bookingID = InputUtility.getStringInput();

        Booking booking = bookingControl.searchBooking(bookingID);

        if (booking == null) {

            System.out.println("\nBooking not found.");
            InputUtility.pressEnterToContinue();
            return;

        }

        System.out.println();
        System.out.println(booking);

        System.out.println("\n1. Confirmed");
        System.out.println("2. Cancelled");
        System.out.print("Choose : ");

        int choice = InputUtility.getIntInput();

        switch (choice) {

            case 1:
                booking.confirm();
                System.out.println("\nBooking confirmed.");
                break;

            case 2:
                booking.cancel();
                System.out.println("\nBooking cancelled.");
                break;

            default:
                System.out.println("\nInvalid choice.");
        }

        InputUtility.pressEnterToContinue();
    }

}