package utility;
/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;


import java.util.Scanner;

public class InputUtility {
    private static Scanner scanner = new Scanner(System.in);
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    
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
            try {
                LocalDate.parse(date, FORMATTER);
                if (LocalDate.parse(date, FORMATTER).isBefore(LocalDate.now())) {
                    System.out.print("Date cannot be before today. Enter again: ");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.print(
                    "Invalid date. Use DD-MM-YYYY format (e.g. 25-12-2026): "
                );
            }
        }
    }
    
    public static String getCheckOutDate(String checkInDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate, FORMATTER);

        while (true) {
            System.out.print("Check-Out Date (DD-MM-YYYY) : ");
            String checkOutDate = getDateInput();
            LocalDate checkOut = LocalDate.parse(checkOutDate, FORMATTER);
            if (checkOut.isAfter(checkIn)) {
                return checkOutDate;
            }
            System.out.println("Check-Out Date must be later than Check-In Date.");
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
    
    public static String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        text = text.toLowerCase();
        return text.substring(0, 1).toUpperCase() + text.substring(1);
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

