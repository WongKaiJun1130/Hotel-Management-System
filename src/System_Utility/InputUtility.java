package System_Utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import java.util.Scanner;

public class InputUtility {
    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public static String getStringInput() {
        return scanner.nextLine();
    }

    public static double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a decimal number: ");
            }
        }
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void clearScreen() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
    public static int getPositiveIntInput() {
    while (true) {
        int value = getIntInput();
        if (value > 0) {
            return value;
        }
        System.out.print("Invalid input. Please enter a positive number: ");
    }
}

    public static String getPhoneInput() {
        while (true) {
            String phone = getStringInput();
            if (phone.matches("[0-9]+") && phone.length() >= 7 && phone.length() <= 15) {
                return phone;
            }
            System.out.print("Invalid phone. Enter numbers only (7-15 digits): ");
        }
    }

    public static String getDateInput() {
        while (true) {
            String date = getStringInput();
            if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = date.split("-");
                int day   = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year  = Integer.parseInt(parts[2]);
                if (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 2000) {
                    return date;
                }
            }
            System.out.print("Invalid date. Use DD-MM-YYYY format (e.g. 25-12-2025): ");
        }
    }

    public static String getTimeInput() {
        while (true) {
            String time = getStringInput();
            if (time.matches("\\d{2}:\\d{2}")) {
                int hour   = Integer.parseInt(time.substring(0, 2));
                int minute = Integer.parseInt(time.substring(3, 5));
                if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                    return time;
                }
            }
            System.out.print("Invalid time. Use HH:MM format (e.g. 09:30): ");
        }
    }

    public static String getNonEmptyInput() {
        while (true) {
            String value = getStringInput();
            if (!value.trim().isEmpty()) {
                return value.trim();
            }
            System.out.print("Input cannot be empty. Please try again: ");
        }
    }
    
    public static String getValidName() {
        while (true) {
            String name = getStringInput();

            //match letters,spaces,Hyphen(-),Apostrophe(')
            if (!name.trim().isEmpty() && name.matches("[a-zA-Z'\\- ]+")) {
                return name.trim();
            }

            System.out.print("Invalid name. Name must contain letters only. Please try again: ");
        }
    }
    
    public static String getValidRoomType() {
        while (true) {
            String roomType = getStringInput().trim();

            if (roomType.equalsIgnoreCase("Single") ||
                roomType.equalsIgnoreCase("Medium") ||
                roomType.equalsIgnoreCase("Large")) {

                return roomType;
            }

            System.out.print("Invalid room type. Please enter Single, Medium, or Large: ");
        }
    }
    
    public static String getYOrNInput() {
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N")) {
                return input;
            } else {
                System.out.print("Invalid input. Please enter Y or N: ");
            }
        }
    }

}

