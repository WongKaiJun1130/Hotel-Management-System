package dao;

import entity.Guest;
import entity.LoyaltyRecord;
import adt.DoublyLinkedList;
import java.time.LocalDate;

public class LoyaltyDatabase {

    private static DoublyLinkedList<LoyaltyRecord> loyaltyData = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Loyalty Data
    //====================================================
    public static void createLoyaltyData() {

        GuestDatabase guestDatabase = new GuestDatabase();
        DoublyLinkedList<Guest> guests = guestDatabase.retrieveFromFile();

        if (guests == null || guests.isEmpty()) {
            System.out.println("No guest records found.");
            return;
        }

        if (guests.getSize() < 10) {
            System.out.println("Not enough guest records. At least 10 guests are required.");
            return;
        }

        DoublyLinkedList<LoyaltyRecord> loyaltyRecords = new DoublyLinkedList<>();

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

        LoyaltyDatabase loyaltyDatabase = new LoyaltyDatabase();
        loyaltyDatabase.saveToFile(loyaltyRecords);

        System.out.println(loyaltyRecords.getSize() + " Loyalty Records Created In Memory!");
    }

    //====================================================
    // Save Loyalty Data In Memory
    //====================================================
    public void saveToFile(DoublyLinkedList<LoyaltyRecord> loyaltyRecords) {

        loyaltyData = new DoublyLinkedList<>();

        if (loyaltyRecords == null) {
            return;
        }

        for (int i = 1; i <= loyaltyRecords.getSize(); i++) {
            LoyaltyRecord loyaltyRecord = loyaltyRecords.getEntry(i);

            if (loyaltyRecord != null) {
                loyaltyData.add(loyaltyRecord);
            }
        }

        System.out.println("Loyalty Database Updated In Memory!");
    }

    //====================================================
    // Retrieve Loyalty Data From Memory
    //====================================================
    public DoublyLinkedList<LoyaltyRecord> retrieveFromFile() {

        DoublyLinkedList<LoyaltyRecord> copiedLoyaltyRecords = new DoublyLinkedList<>();

        for (int i = 1; i <= loyaltyData.getSize(); i++) {
            LoyaltyRecord loyaltyRecord = loyaltyData.getEntry(i);

            if (loyaltyRecord != null) {
                copiedLoyaltyRecords.add(loyaltyRecord);
            }
        }

        return copiedLoyaltyRecords;
    }

    //====================================================
    // Add Loyalty Record
    //====================================================
    public boolean addLoyaltyRecord(LoyaltyRecord loyaltyRecord) {

        if (loyaltyRecord == null) {
            return false;
        }

        loyaltyData.add(loyaltyRecord);
        return true;
    }

    //====================================================
    // Get Loyalty Record
    //====================================================
    public LoyaltyRecord getLoyaltyRecord(int position) {

        if (position < 1 || position > loyaltyData.getSize()) {
            return null;
        }

        return loyaltyData.getEntry(position);
    }

    //====================================================
    // Remove Loyalty Record
    //====================================================
    public LoyaltyRecord removeLoyaltyRecord(int position) {

        if (position < 1 || position > loyaltyData.getSize()) {
            return null;
        }

        return loyaltyData.remove(position);
    }

    //====================================================
    // Get Total Loyalty Records
    //====================================================
    public int getTotalLoyaltyRecords() {
        return loyaltyData.getSize();
    }

    //====================================================
    // Check Loyalty Data Is Empty
    //====================================================
    public boolean isLoyaltyDataEmpty() {
        return loyaltyData.isEmpty();
    }
}