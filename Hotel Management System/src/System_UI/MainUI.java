/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_UI;

import System_Utility.Utility;

/**
 *
 * @author USER
 */
public class MainUI { 

    public static void MainUI() {

        System.out.println("====================================");
        System.out.println("     HOTEL MANAGEMENT SYSTEM");
        System.out.println("====================================");

        System.out.println();

        Utility.customMenu(
                new String[]{
                    "1.Register",
                    "2.HouseKeeping",
                    "3.Loyalty & Prize Service",
                    "4.VIP & Loyalty Tier Priority Room Allocation",
                    "0.Exit"
                },
                "Main Menu",
                "Select option: ",
                new Runnable[]{
                    () -> RegisterUI.menu(),
                    () -> HouseKeepingUI.menu(),
                    () -> System.out.println("Loyalty & Prize Service coming soon..."),
                    () -> System.out.println("VIP Allocation coming soon..."),
                    () -> System.exit(0)
                }
        );
        
        System.out.println("Please select an option: ");

    }
}
    
