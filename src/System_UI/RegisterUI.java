/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_UI;

import System_Utility.Navigation;

/**
 *
 * @author USER
 */
public class RegisterUI {
    
    // Main menu //
    public static void menu() {
        Navigation.stack.push(CustomerMainMenu);

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
    
    } // end register main menu //
    
    private static final Runnable CustomerMainMenu = () -> {
        String[] options = {
            "1. Customer Management",
            "2. Register",
            "0. Back"
        };
    
    };
}