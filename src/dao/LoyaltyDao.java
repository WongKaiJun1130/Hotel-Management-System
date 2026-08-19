package dao;

import entity.Guest;
import entity.LoyaltyRecord;
import adt.DoublyLinkedList;
import java.time.LocalDate;

public class LoyaltyDao {

    private static DoublyLinkedList<LoyaltyRecord> loyaltyData = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Loyalty Data
    //====================================================
    public static void createLoyaltyData() {

        GuestDao guestDatabase = new GuestDao();
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

        Guest guest1 = guestDatabase.searchGuestByID("R0001");
        Guest guest2 = guestDatabase.searchGuestByID("R0002");
        Guest guest3 = guestDatabase.searchGuestByID("R0003");
        Guest guest4 = guestDatabase.searchGuestByID("R0004");
        Guest guest5 = guestDatabase.searchGuestByID("R0005");
        Guest guest6 = guestDatabase.searchGuestByID("R0006");
        Guest guest7 = guestDatabase.searchGuestByID("R0007");
        Guest guest8 = guestDatabase.searchGuestByID("R0008");
        Guest guest9 = guestDatabase.searchGuestByID("R0009");
        Guest guest10 = guestDatabase.searchGuestByID("R0010");
        Guest guest11 = guestDatabase.searchGuestByID("R0011");
        Guest guest12 = guestDatabase.searchGuestByID("R0012");
        Guest guest13 = guestDatabase.searchGuestByID("R0013");
        Guest guest14 = guestDatabase.searchGuestByID("R0014");
        Guest guest15 = guestDatabase.searchGuestByID("R0015");
        Guest guest16 = guestDatabase.searchGuestByID("R0016");
        Guest guest17 = guestDatabase.searchGuestByID("R0017");
        Guest guest18 = guestDatabase.searchGuestByID("R0018");
        Guest guest19 = guestDatabase.searchGuestByID("R0019");
        Guest guest20 = guestDatabase.searchGuestByID("R0020");


        loyaltyRecords.add(new LoyaltyRecord(guest1, 2200, 6500, LocalDate.of(2026, 12, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest2, 1800, 4800, LocalDate.of(2026, 11, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest3, 1200, 3200, LocalDate.of(2027, 1, 15)));
        loyaltyRecords.add(new LoyaltyRecord(guest4, 2500, 7200, LocalDate.of(2027, 2, 28)));
        loyaltyRecords.add(new LoyaltyRecord(guest5, 600, 600, LocalDate.of(2026, 8, 31)));

        loyaltyRecords.add(new LoyaltyRecord(guest6, 1600, 4500, LocalDate.of(2026, 10, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest7, 1900, 2800, LocalDate.of(2026, 9, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest8, 2100, 6800, LocalDate.of(2027, 3, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest9, 400, 400, LocalDate.of(2026, 8, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest10, 1400, 4200, LocalDate.of(2026, 12, 15)));

        loyaltyRecords.add(new LoyaltyRecord(guest11, 1700, 4500, LocalDate.of(2027, 1, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest12, 1300, 3300, LocalDate.of(2027, 2, 28)));
        loyaltyRecords.add(new LoyaltyRecord(guest13, 2600, 6500, LocalDate.of(2027, 3, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest14, 700, 1500, LocalDate.of(2026, 11, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest15, 1900, 4900, LocalDate.of(2027, 4, 30)));

        loyaltyRecords.add(new LoyaltyRecord(guest16, 1500, 3500, LocalDate.of(2027, 5, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest17, 2400, 6200, LocalDate.of(2027, 6, 30)));
        loyaltyRecords.add(new LoyaltyRecord(guest18, 800, 1800, LocalDate.of(2027, 7, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest19, 2000, 4700, LocalDate.of(2027, 8, 31)));
        loyaltyRecords.add(new LoyaltyRecord(guest20, 1100, 3100, LocalDate.of(2027, 9, 30)));

        LoyaltyDao loyaltyDatabase = new LoyaltyDao();
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