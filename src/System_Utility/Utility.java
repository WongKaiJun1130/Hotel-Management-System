/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Utility; 

import java.util.Scanner;

/**
 *
 * @author USER
 */
public class Utility {
    
    public static void customMenu(String[] title, String menuTitle, String customMessage, Runnable[] action) {
        Scanner scanner = new Scanner(System.in);
        Boolean resume;

        do {    
            resume = false;
            System.out.println();
            System.out.println("+----------------------------------------------+");
            if (menuTitle != null && !menuTitle.isEmpty()) {
                System.out.printf("|%46s|\n",String.format("%-" + ((46 + menuTitle.length()) / 2) + "s", menuTitle));
            }
            System.out.println("+----------------------------------------------+");

            for (String titleItem : title) {
                System.out.printf("|%46s|\n", String.format("%-" + ((46 + titleItem.length()) / 2) + "s", titleItem));
            }
            System.out.println("+----------------------------------------------+");

            System.out.print(customMessage);
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                resume = true;
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            if (choice == 0) {
                action[title.length - 1].run();
                return; 
            } else if (choice >= 1 && choice <= title.length - 1) {
                action[choice - 1].run();
                resume = true;
            } else {
                resume = true;
                showInvalidChoiceMessage();
            }
        } while (resume);
    } // end customer menu
    
    public static void showInvalidChoiceMessage() {
        System.out.println("Invalid choice! Try again.");
    }
    
}