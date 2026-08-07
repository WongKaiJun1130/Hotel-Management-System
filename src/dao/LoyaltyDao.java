/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Entity.Guest;
import Entity.LoyaltyRecord;
import Adt.DoublyLinkedList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

/*
 * @author User
 */
public class LoyaltyDao {

    private final String fileName = "LoyaltyDatabase.dat";

    // ==================================================
    // Create Initial Loyalty Data
    // ==================================================
    public static void createLoyaltyData() {

        GuestDao guestDatabase = new GuestDao();

        DoublyLinkedList.ArrayList<Guest> guests =
                guestDatabase.retrieveFromFile();

        if (guests == null || guests.isEmpty()) {
            System.out.println("No guest records found.");
            return;
        }

        if (guests.getNumberOfEntries() < 10) {
            System.out.println(
                    "Not enough guest records. At least 10 guests are required."
            );
            return;
        }

        DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyRecords =
                new DoublyLinkedList.ArrayList<>();

        Guest guest1 = guests.getEntry(1);
        Guest guest2 = guests.getEntry(2);
        Guest guest3 = guests.getEntry(3);
        Guest guest4 = guests.getEntry(4);
        Guest guest5 = guests.getEntry(5);
        Guest guest6 = guests.getEntry(6);
        Guest guest7 = guests.getEntry(7);
        Guest guest8 = guests.getEntry(8);
        Guest guest9 = guests.getEntry(9);
        Guest guest10 = guests.getEntry(10);

        loyaltyRecords.add(new LoyaltyRecord(guest1, 2200, 6500, LocalDate.of(2026, 12, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest2, 1800, 4800, LocalDate.of(2026, 11, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest3, 1200, 3200, LocalDate.of(2027, 1, 15)));
        loyaltyRecords.add(new LoyaltyRecord(guest4, 2500, 7200, LocalDate.of(2027, 2, 28)));
        loyaltyRecords.add(new LoyaltyRecord(guest5, 600, 600, LocalDate.of(2026, 8, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest6, 1600, 4500, LocalDate.of(2026, 10, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest7, 1900, 2800, LocalDate.of(2026, 9, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest8, 2100, 6800, LocalDate.of(2027, 3, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest9, 400, 400, LocalDate.of(2026, 8, 15)));
        loyaltyRecords.add(new LoyaltyRecord(guest10, 1400, 4200, LocalDate.of(2026, 12, 15)));

        LoyaltyDao loyaltyDatabase =
                new LoyaltyDao();

        loyaltyDatabase.saveToFile(loyaltyRecords);

        System.out.println(
                loyaltyRecords.getNumberOfEntries()
                        + " Loyalty Records Saved!"
        );
    }

    // ==================================================
    // Save Loyalty Data
    // ==================================================
    public void saveToFile(
            DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyRecords
    ) {

        File file = new File(fileName);

        System.out.println(
                "Saving loyalty file to: "
                        + file.getAbsolutePath()
        );

        try (ObjectOutputStream outputStream =
                new ObjectOutputStream(
                        new FileOutputStream(file)
                )) {

            outputStream.writeObject(loyaltyRecords);

            System.out.println(
                    "Loyalty Database Saved Successfully!"
            );

        } catch (FileNotFoundException ex) {

            System.out.println("Loyalty database file not found.");

        } catch (IOException ex) {

            System.out.println(
                    "Cannot save loyalty database."
            );

            ex.printStackTrace();
        }
    }

    // ==================================================
    // Retrieve Loyalty Data
    // ==================================================
    @SuppressWarnings("unchecked")
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            retrieveFromFile() {

        File file = new File(fileName);

        DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyRecords =
                new DoublyLinkedList.ArrayList<>();

        try (ObjectInputStream inputStream =
                new ObjectInputStream(
                        new FileInputStream(file)
                )) {

            loyaltyRecords =
                    (DoublyLinkedList.ArrayList<LoyaltyRecord>)
                    inputStream.readObject();

        } catch (FileNotFoundException ex) {

            System.out.println(
                    "No Loyalty Database Found."
            );

        } catch (IOException ex) {

            System.out.println(
                    "Cannot read loyalty database."
            );

            ex.printStackTrace();

        } catch (ClassNotFoundException ex) {

            System.out.println(
                    "LoyaltyRecord class not found."
            );
        }

        return loyaltyRecords;
    }
}
