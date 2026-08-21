/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility; 

/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

public class Utility {
    //==========================================================
    // UI CONSTANTS
    //==========================================================
    public static final String MENU_BORDER = "+----------------------------------------------------------+";

    public static final String DETAIL_BORDER = "+----------------------------------------------------------+";
    
    public static final String HOTEL_NAME = "TARUMT RESORTS";
    
    public static void customMenu(String[] options, String menuTitle, String customMessage, Runnable[] actions) {
        boolean resume;
        do {
            resume = false;
            InputUtility.clearScreen();
            System.out.println(MENU_BORDER);
            printBoxTitle(menuTitle);
            System.out.println(MENU_BORDER);
            for (String option : options) {
                printMenuItem(option);
            }

            System.out.println(MENU_BORDER);
            System.out.print(customMessage);
            String input = InputUtility.getStringInput().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                InputUtility.pressEnterToContinue();
                resume = true;
                continue;
            }

            //==================================================
            // 0 = Last action
            //==================================================
            if (choice == 0) {
                actions[options.length - 1].run();
                return;
            }


            //==================================================
            // Valid Choice
            //==================================================
            if (choice >= 1 && choice <= options.length - 1) {
                actions[choice - 1].run();
                resume = true;
            } else {
                showInvalidChoiceMessage();
                InputUtility.pressEnterToContinue();
                resume = true;
            }
        } while (resume);
    }


    //==========================================================
    // Box Title
    //==========================================================
    public static void printBoxTitle(String title) {
        System.out.printf("|%-58s|%n",centerText(title, 58));
    }


    //==========================================================
    // Menu Item
    //==========================================================
    public static void printMenuItem(String text) {
        System.out.printf("| %-56s |%n", limitText(text, 56));
    }

    //==========================================================
    // Header
    //==========================================================
    public static void printHeader(String title) {
        System.out.println();
        System.out.println(MENU_BORDER);
        printBoxTitle(title);
        System.out.println(MENU_BORDER);
    }

    //==========================================================
    // Footer
    //==========================================================
    public static void printFooter() {
        System.out.println(MENU_BORDER);
    }


    //==========================================================
    // Message Box
    //==========================================================
    public static void printMessageBox(String message) {
        System.out.println(MENU_BORDER);
        System.out.printf("| %-56s |%n", limitText(message, 56));
        System.out.println(MENU_BORDER);
    }

    //==========================================================
    // Label + Value
    //==========================================================
    public static void printDetail(String label, String value) {
        String text = String.format("%-18s : %s", label, value);
        System.out.printf("| %-56s |%n", limitText(text, 56));
    }

    //==========================================================
    // Invalid Choice
    //==========================================================
    public static void showInvalidChoiceMessage() {
        System.out.println("Invalid choice! Try again."
        );
    }


    //==========================================================
    // Limit Text
    //==========================================================
    public static String limitText(String text, int width) {
        if (text == null) {
            return "";
        }
        text = text.trim();
        if (text.length() <= width) {
            return text;
        }
        if (width <= 3) {
            return text.substring(0, width);
        }
        return text.substring(0, width - 3) + "...";
    }

    //==========================================================
    // Center Text
    //==========================================================
    public static String centerText(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= width) {
            return text;
        }
        int totalPadding = width - text.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return repeatCharacter(' ', leftPadding) + text + repeatCharacter(' ', rightPadding);
    }


    //==========================================================
    // Repeat Character
    //==========================================================
    private static String repeatCharacter(char character, int amount) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            result.append(character);
        }
        return result.toString();
    }
}