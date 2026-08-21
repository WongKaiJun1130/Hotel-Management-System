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
        
        Utility.customMenu(
            new String[]{
                "1. Standard Booking Management",

                "2. Housekeeping Management",

                "3. Loyalty & Rewards Management",

                "4. VIP & Loyalty Room Allocation",

                "0. Exit"
            },
                "TARUMT RESORT - MANAGEMENT SYSTEM",
                "Select option: ",
                new Runnable[]{
                () -> bookingUI.bookingMenu(),
                () -> HouseKeepingUI.menu(),
                () -> loyaltyUI.loyaltyMenu(),
                () -> allocationUI.allocationMenu(),
                () -> {
                    System.out.println("THANKS FOR USING");
                    System.exit(0);
                }
            }
        );
    }


}   