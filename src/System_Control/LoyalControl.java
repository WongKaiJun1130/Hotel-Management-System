/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Control;

import System_Entity.LoyaltyRecord;
import System_adt.DoublyLinkedList;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Author: CHUNWAI
 */
public class LoyaltyControl {

    private DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList;

    // ==================================================
    // Constructor
    // ==================================================
    public LoyaltyControl() {
        loyaltyList = new DoublyLinkedList.ArrayList<>();
    }

    public LoyaltyControl(
            DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList
    ) {
        this.loyaltyList = loyaltyList;
    }

    // ==================================================
    // 1. Display All Tier Guests
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            getGuestsByTier(String tier) {

        DoublyLinkedList.ArrayList<LoyaltyRecord> result =
                new DoublyLinkedList.ArrayList<>();

        if (tier == null) {
            return result;
        }

        for (int i = 1;
             i <= loyaltyList.getNumberOfEntries();
             i++) {

            LoyaltyRecord record = loyaltyList.getEntry(i);

            if (tier.equalsIgnoreCase("All")
                    || record.getLoyaltyTier()
                            .equalsIgnoreCase(tier)) {

                result.add(record);
            }
        }

        return result;
    }

    // ==================================================
    // 2. Search Guest
    // ==================================================
    public LoyaltyRecord searchGuest(String guestID) {

        if (guestID == null) {
            return null;
        }

        String cleanedGuestID = guestID.trim();

        for (int i = 1;
             i <= loyaltyList.getNumberOfEntries();
             i++) {

            LoyaltyRecord record = loyaltyList.getEntry(i);

            if (record.getGuestID()
                    .equalsIgnoreCase(cleanedGuestID)) {

                return record;
            }
        }

        return null;
    }

    // ==================================================
    // 3. Add Points
    // Called after booking confirmation
    // ==================================================
    public boolean addPoints(
            String guestID,
            String roomType
    ) {

        LoyaltyRecord record = searchGuest(guestID);

        if (record == null) {
            return false;
        }

        int points = calculatePointsByRoomType(roomType);

        if (points <= 0) {
            return false;
        }

        record.setAvailablePoints(
                record.getAvailablePoints() + points
        );

        record.setLifetimePoints(
                record.getLifetimePoints() + points
        );

        record.setExpiryDate(
                LocalDate.now().plusYears(1)
        );

        updateTier(record);

        return true;
    }

    private int calculatePointsByRoomType(String roomType) {

        if (roomType == null) {
            return 0;
        }

        switch (roomType.trim().toLowerCase()) {

            case "small room":
                return 100;

            case "middle room":
            case "medium room":
                return 200;

            case "big room":
                return 300;

            default:
                return 0;
        }
    }

    // ==================================================
    // 4. Update Tier
    // Automatically called by addPoints()
    // ==================================================
    private boolean updateTier(LoyaltyRecord record) {

        if (record == null) {
            return false;
        }

        int lifetimePoints =
                record.getLifetimePoints();

        String oldTier =
                record.getLoyaltyTier();

        String newTier;

        if (lifetimePoints >= 6000) {
            newTier = "Elite";

        } else if (lifetimePoints >= 4000) {
            newTier = "Diamond";

        } else if (lifetimePoints >= 2000) {
            newTier = "Platinum";

        } else {
            newTier = "Standard";
        }

        if (oldTier == null
                || !oldTier.equalsIgnoreCase(newTier)) {

            record.setLoyaltyTier(newTier);

            return true;
        }

        return false;
    }

    // ==================================================
    // 5. Top 5 Guests for Selected Tier Report
    // Sort by lifetime points from highest to lowest
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            getTopFiveGuestsByTier(String tier) {

        DoublyLinkedList.ArrayList<LoyaltyRecord>
                filteredList = getGuestsByTier(tier);

        filteredList.sort(
                new Comparator<LoyaltyRecord>() {

                    @Override
                    public int compare(
                            LoyaltyRecord first,
                            LoyaltyRecord second
                    ) {
                        return Integer.compare(
                                second.getLifetimePoints(),
                                first.getLifetimePoints()
                        );
                    }
                }
        );

        DoublyLinkedList.ArrayList<LoyaltyRecord> topFive =
                new DoublyLinkedList.ArrayList<>();

        int limit = Math.min(
                5,
                filteredList.getNumberOfEntries()
        );

        for (int i = 1; i <= limit; i++) {
            topFive.add(
                    filteredList.getEntry(i)
            );
        }

        return topFive;
    }

    // ==================================================
    // Loyalty List Access Methods
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            getAllLoyaltyRecords() {

        return loyaltyList;
    }

    public void setLoyaltyList(
            DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList
    ) {
        this.loyaltyList = loyaltyList;
    }
}
```

