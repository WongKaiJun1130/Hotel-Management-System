/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_UI;

import System_Utility.Navigation;
import System_Utility.Utility;
import System_adt.StackInterface;
import System_Control.RoomTracker;

/**
 *
 * @author USER
 */
public class HouseKeepingUI {
    
    private static RoomTracker room101 = new RoomTracker("101");
    
    public static void menu() {
        Navigation.stack.push(HouseKeepingMenu);

        boolean exit = false;

        while (!Navigation.stack.isEmpty() && !exit) {
            Runnable currentMenu = Navigation.stack.peek();
            if (currentMenu != null) {
                currentMenu.run();
            } else {
                exit = true;
            }
        }
        System.out.println("Returning to main menu...");
    
    } // end HouseKeeping main menu //
    
 
    
    
    private static final Runnable manageRoom = () -> {
    String[] options = {
        "1. Update Status",
        "2. Rollback Last Status",
        "3. View Current Status",
        "0. Back"
    };
    Runnable[] actions = {
        () -> room101.updateStatus("Cleaning In Progress"), // replace with actual input prompt
        () -> room101.rollbackStatus(),
        () -> System.out.println("Current status: " + room101.getCurrentStatus()),
        () -> Navigation.stack.pop()
    };
    Utility.customMenu(options, "Manage Room 101", "Enter your choice: ", actions);
};

    private static final Runnable manageSRoomStatus = () -> {
        System.out.println("Manage Doctor Schedule - coming soon...");
    };

    private static final Runnable report1 = () -> {
        System.out.println("report 111111.. .. . . ");
    };
    private static final Runnable report2 = () -> {
        System.out.println("report 222222 . .. .. ");
    };


private static final Runnable HouseKeepingMenu = () -> {
        String[] options = {
            "1. Show Room Status",
            "2. Change Room Status",
            "3. Report 1",
            "4. Report 2",
            "0. Back to Main Menu"
        };

        Runnable[] actions = {
            () -> Navigation.stack.push(manageRoom),
            () -> Navigation.stack.push(manageSRoomStatus),
            () -> Navigation.stack.push(report1),
            () -> Navigation.stack.push(report2),
            () -> Navigation.stack.pop()
        }; 
       

        Utility.customMenu(options, "Housekeeping Menu", "Enter your choice: ", actions);
    };
}