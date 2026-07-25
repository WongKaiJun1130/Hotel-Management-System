package System_UI;

import java.util.Scanner;
import System_Utility.InputUtility;
import System_Control.BookingControl;


public class BookingUI {
    
        public void bookingMenu() {
        int choice;
        do {
            InputUtility.clearScreen();
            System.out.println(" ");
            System.out.println("=== Booking Management ===");
            System.out.println("1. Add Booking");
            System.out.println("2. Cancel Booking");
            System.out.println("3. Search Booking");
            System.out.println("4. Display All Booking");
            System.out.println("5. Update Booking Status"); 
            System.out.println("0. Back");
            System.out.print("\nEnter choice: ");

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
                    updateBooking();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    InputUtility.pressEnterToContinue();
            }
        } while (choice != 0);
    }
}

