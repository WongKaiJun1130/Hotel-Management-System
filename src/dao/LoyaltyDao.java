package dao;

import Entity.Guest;
import Entity.LoyaltyRecord;
import Adt.DoublyLinkedList;
import java.time.LocalDate;

public class LoyaltyDao {

    private static DoublyLinkedList.ArrayList<LoyaltyRecord>
            loyaltyData =
            new DoublyLinkedList.ArrayList<>();

    //====================================================
    // Create Initial Loyalty Data
    //====================================================
    public static void createLoyaltyData() {

        GuestDao guestDao = new GuestDao();

        DoublyLinkedList.ArrayList<Guest> guests =
                guestDao.retrieveFromFile();

        loyaltyData =
                new DoublyLinkedList.ArrayList<>();

        if (guests == null || guests.isEmpty()) {

            System.out.println(
                    "No guest records found."
            );

            return;
        }

        int count =
                Math.min(
                        guests.getNumberOfEntries(),
                        10
                );

        LocalDate[] expiryDates = {

            LocalDate.of(2026, 12, 31),
            LocalDate.of(2026, 11, 30),
            LocalDate.of(2027, 1, 15),
            LocalDate.of(2027, 2, 28),
            LocalDate.of(2026, 8, 31),
            LocalDate.of(2026, 10, 31),
            LocalDate.of(2026, 9, 30),
            LocalDate.of(2027, 3, 31),
            LocalDate.of(2026, 8, 15),
            LocalDate.of(2026, 12, 15)
        };

        int[] points = {
            2200, 1800, 1200, 2500, 600,
            1600, 1900, 2100, 400, 1400
        };

        int[] pointsRequired = {
            6500, 4800, 3200, 7200, 600,
            4500, 2800, 6800, 400, 4200
        };

        for (int i = 1; i <= count; i++) {

            Guest guest = guests.getEntry(i);

            loyaltyData.add(
                    new LoyaltyRecord(
                            guest,
                            points[i - 1],
                            pointsRequired[i - 1],
                            expiryDates[i - 1]
                    )
            );
        }

        System.out.println(
                loyaltyData.getNumberOfEntries()
                + " Loyalty Records Created In Memory!"
        );
    }

    //====================================================
    // Save Loyalty Data In Memory
    //====================================================
    public void saveToFile(
            DoublyLinkedList.ArrayList<LoyaltyRecord>
                    loyaltyRecords) {

        loyaltyData =
                new DoublyLinkedList.ArrayList<>();

        if (loyaltyRecords == null) {
            return;
        }

        for (int i = 1;
                i <= loyaltyRecords.getNumberOfEntries();
                i++) {

            LoyaltyRecord record =
                    loyaltyRecords.getEntry(i);

            if (record != null) {
                loyaltyData.add(record);
            }
        }
    }

    //====================================================
    // Retrieve Loyalty Data From Memory
    //====================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            retrieveFromFile() {

        DoublyLinkedList.ArrayList<LoyaltyRecord>
                result =
                new DoublyLinkedList.ArrayList<>();

        for (int i = 1;
                i <= loyaltyData.getNumberOfEntries();
                i++) {

            LoyaltyRecord record =
                    loyaltyData.getEntry(i);

            if (record != null) {
                result.add(record);
            }
        }

        return result;
    }

    //====================================================
    // Add Loyalty Record
    //====================================================
    public boolean addLoyaltyRecord(
            LoyaltyRecord loyaltyRecord) {

        if (loyaltyRecord == null) {
            return false;
        }

        loyaltyData.add(loyaltyRecord);

        return true;
    }

    //====================================================
    // Get Loyalty Record
    //====================================================
    public LoyaltyRecord getLoyaltyRecord(
            int position) {

        if (position < 1
                || position
                > loyaltyData.getNumberOfEntries()) {

            return null;
        }

        return loyaltyData.getEntry(position);
    }

    //====================================================
    // Remove Loyalty Record
    //====================================================
    public LoyaltyRecord removeLoyaltyRecord(
            int position) {

        if (position < 1
                || position
                > loyaltyData.getNumberOfEntries()) {

            return null;
        }

        return loyaltyData.remove(position);
    }

    //====================================================
    // Get Total Loyalty Records
    //====================================================
    public int getTotalLoyaltyRecords() {

        return loyaltyData.getNumberOfEntries();
    }

    //====================================================
    // Check Loyalty Data Is Empty
    //====================================================
    public boolean isLoyaltyDataEmpty() {

        return loyaltyData.isEmpty();
    }
}