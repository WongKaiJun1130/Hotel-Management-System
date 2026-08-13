/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package UI;

import Utility.Utility;

/**
 *
 * @author USER
 */
public class MainUI { 
    
    private static final BookingUI bookingUI = new BookingUI();
    private static VIPAllocationUI allocationUI = new VIPAllocationUI();
    private static final LoyaltyAndRewardsUI loyaltyUI = new LoyaltyAndRewardsUI();

    public static void MainUI() {

        // Display Hotel Logo
        displayLogo();
        
        Utility.customMenu(
            new String[]{
                "1.Booking",
                "2.HouseKeeping",
                "3.Loyalty & Prize Service",
                "4.VIP & Loyalty Tier Priority Room Allocation",
                "0.Exit"
            },
                "Main Menu",
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