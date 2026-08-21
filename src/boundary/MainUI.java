package boundary;

/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

import utility.Utility;

public class MainUI { 
    
    private static final BookingUI bookingUI = new BookingUI();
    private static VIPAllocationUI allocationUI = new VIPAllocationUI();
    private static final LoyaltyAndRewardsUI loyaltyUI = new LoyaltyAndRewardsUI();

    public static void MainUI() {

        // Display Hotel Logo
        displayLogo();
        Utility.customMenu(
            new String[]{
                "1. Standard Booking Management",

                "2. Housekeeping Management",

                "3. Loyalty & Rewards Management",

                "4. VIP & Loyalty Room Allocation",

                "0. Exit"
            },
                "HOTEL MANAGEMENT SYSTEM",
                "Select option: ",
                new Runnable[]{
                () -> bookingUI.bookingMenu(),
                () -> HouseKeepingUI.menu(),
                () -> loyaltyUI.loyaltyMenu(),
                () -> allocationUI.allocationMenu(),
                () -> System.exit(0)
            }
        );
    }

    // Hotel Management System Logo
    private static void displayLogo(){
        System.out.println();
        System.out.println("|==============================================|");
        System.out.println("|                                              |");
        System.out.println("|        *  HOTEL MANAGEMENT SYSTEM  *         |");
        System.out.println("|                                              |");
        System.out.println("|        Guest | Room | VIP | Loyalty          |");
        System.out.println("|                                              |");
        System.out.println("|==============================================|");
        System.out.println();
    }

}   