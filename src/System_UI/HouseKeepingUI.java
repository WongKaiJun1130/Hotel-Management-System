/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_UI;

import System_Utility.Navigation;
import System_Utility.Utility;
import System_adt.StackInterface;

/**
 *
 * @author USER
 */
public class HouseKeepingUI {
    
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
    
    private static final Runnable HouseKeepingMenu = () -> {
        String[] options = {
            "1. Manage Room",
            "2. manageScheduleMenu",
            "3. View Status",
            "0. Back to Main Menu"
        };

        Runnable[] actions = {
//            () -> Navigation.stack.push(manageRoom),
//            () -> Navigation.stack.push(manageScheduleMenu),
//            () -> Navigation.stack.push(StatusMenu),
              () -> Navigation.stack.pop()
        }; 

        Utility.customMenu(options, "Housekeeping Management", "Enter your choice: ", actions);
    };
    
    private static final Runnable manageRoom = () -> {
        System.out.println("Manage Room - coming soon...");
    };

    private static final Runnable manageScheduleMenu = () -> {
        System.out.println("Manage Doctor Schedule - coming soon...");
    };

    private static final Runnable StatusMenu = () -> {
        System.out.println("View Status - coming soon...");
    };
}

