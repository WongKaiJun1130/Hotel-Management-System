/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Control;

import System_Entity.LoyaltyRecord;
import System_Entity.RedemptionRecord;
import System_adt.DoublyLinkedList;

import java.time.LocalDate;
import java.util.Comparator;

/*
 * @author User
 */
public class LoyaltyControl {

    private DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList;
    private DoublyLinkedList.ArrayList<RedemptionRecord> redemptionList;

    // ==================================================
    // Constructor
    // ==================================================
    public LoyaltyControl() {
        loyaltyList = new DoublyLinkedList.ArrayList<>();
        redemptionList = new DoublyLinkedList.ArrayList<>();
    }

    public LoyaltyControl(DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList) {
        this.loyaltyList = loyaltyList;
        this.redemptionList = new DoublyLinkedList.ArrayList<>();
    }

    // ==================================================
    // 1. Display All Tier Guests
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>getGuestsByTier(String tier) {      
        DoublyLinkedList.ArrayList<LoyaltyRecord> result = new DoublyLinkedList.ArrayList<>();

        if (tier == null) {
            return result;
        }

        for (int i = 1; i <= loyaltyList.getNumberOfEntries();i++){
            LoyaltyRecord record = loyaltyList.getEntry(i);

            if (tier.equalsIgnoreCase("All") || record.getLoyaltyTier().equalsIgnoreCase(tier)) {
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

        for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = loyaltyList.getEntry(i);
            
            //remove space that user input
            if (record.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return record;  
            }
        }
        return null;
    }

    // ==================================================
    // 3. Add Points
    // Called after booking confirmation.
    // ==================================================
    public boolean addPoints(
            String guestID,
            String roomType
    ) {

        LoyaltyRecord record =
                searchGuest(guestID);

        if (record == null) {
            return false;
        }

        int points =
                calculatePointsByRoomType(roomType);

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

    private int calculatePointsByRoomType(
            String roomType
    ) {

        if (roomType == null) {
            return 0;
        }

        switch (roomType.toLowerCase()) {

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
    // 4. Redeem Points
    // ==================================================
    public boolean redeemPoints(
            String guestID,
            String rewardName
    ) {

        LoyaltyRecord record =
                searchGuest(guestID);

        if (record == null || rewardName == null) {
            return false;
        }

        int requiredPoints =
                getRewardPoints(
                        record.getLoyaltyTier(),
                        rewardName
                );

        if (requiredPoints <= 0) {
            return false;
        }

        if (record.getAvailablePoints()
                < requiredPoints) {

            return false;
        }

        record.setAvailablePoints(
                record.getAvailablePoints()
                        - requiredPoints
        );

        RedemptionRecord redemption =
                new RedemptionRecord(
                        record.getGuestID(),
                        record.getGuestName(),
                        record.getLoyaltyTier(),
                        rewardName,
                        requiredPoints,
                        LocalDate.now()
                );

        redemptionList.add(redemption);

        return true;
    }

    private int getRewardPoints(
            String tier,
            String rewardName
    ) {

        if (tier == null || rewardName == null) {
            return -1;
        }

        if (tier.equalsIgnoreCase("Standard")) {

            if (rewardName.equalsIgnoreCase(
                    "Free Welcome Drink")) {
                return 200;
            }

            if (rewardName.equalsIgnoreCase(
                    "10% Cafe Discount")) {
                return 300;
            }
        }

        if (tier.equalsIgnoreCase("Platinum")) {

            if (rewardName.equalsIgnoreCase(
                    "Free Breakfast")) {
                return 500;
            }

            if (rewardName.equalsIgnoreCase(
                    "Late Check-out")) {
                return 600;
            }
        }

        if (tier.equalsIgnoreCase("Diamond")) {

            if (rewardName.equalsIgnoreCase(
                    "Room Type Upgrade")) {
                return 1000;
            }

            if (rewardName.equalsIgnoreCase(
                    "Free Spa Voucher")) {
                return 1200;
            }
        }

        if (tier.equalsIgnoreCase("Elite")) {

            if (rewardName.equalsIgnoreCase(
                    "Executive Lounge Access")) {
                return 1500;
            }

            if (rewardName.equalsIgnoreCase(
                    "One Free Night Stay")) {
                return 2500;
            }
        }

        return -1;
    }

    public String[] getRewardsByTier(String tier) {

        if (tier == null) {
            return new String[0];
        }

        if (tier.equalsIgnoreCase("Standard")) {
            return new String[]{
                "Free Welcome Drink - 200 points",
                "10% Cafe Discount - 300 points"
            };
        }

        if (tier.equalsIgnoreCase("Platinum")) {
            return new String[]{
                "Free Breakfast - 500 points",
                "Late Check-out - 600 points"
            };
        }

        if (tier.equalsIgnoreCase("Diamond")) {
            return new String[]{
                "Room Type Upgrade - 1000 points",
                "Free Spa Voucher - 1200 points"
            };
        }

        if (tier.equalsIgnoreCase("Elite")) {
            return new String[]{
                "Executive Lounge Access - 1500 points",
                "One Free Night Stay - 2500 points"
            };
        }

        return new String[0];
    }

    // ==================================================
    // 5. Update Tier
    // Automatically called by addPoints().
    // ==================================================
    private boolean updateTier(
            LoyaltyRecord record
    ) {

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

        if (!oldTier.equalsIgnoreCase(newTier)) {
            record.setLoyaltyTier(newTier);
            return true;
        }

        return false;
    }

    // ==================================================
    // 6. Redemption Analysis Report
    // Filter by tier and date range.
    // Sort by points used, highest first.
    // ==================================================
    public DoublyLinkedList.ArrayList<RedemptionRecord>
            getRedemptionAnalysis(
                    String tier,
                    LocalDate startDate,
                    LocalDate endDate
            ) {

        DoublyLinkedList.ArrayList<RedemptionRecord> result =
                new DoublyLinkedList.ArrayList<>();

        for (int i = 1;
             i <= redemptionList.getNumberOfEntries();
             i++) {

            RedemptionRecord redemption =
                    redemptionList.getEntry(i);

            boolean tierMatch =
                    tier == null
                    || tier.equalsIgnoreCase("All")
                    || redemption.getLoyaltyTier()
                            .equalsIgnoreCase(tier);

            boolean startDateMatch =
                    startDate == null
                    || !redemption.getRedemptionDate()
                            .isBefore(startDate);

            boolean endDateMatch =
                    endDate == null
                    || !redemption.getRedemptionDate()
                            .isAfter(endDate);

            if (tierMatch
                    && startDateMatch
                    && endDateMatch) {

                result.add(redemption);
            }
        }

        result.sort(
                new Comparator<RedemptionRecord>() {

                    @Override
                    public int compare(
                            RedemptionRecord first,
                            RedemptionRecord second
                    ) {
                        return Integer.compare(
                                second.getPointsUsed(),
                                first.getPointsUsed()
                        );
                    }
                }
        );

        return result;
    }

    // ==================================================
    // 7. Top 5 Guests for Selected Tier Report
    // Filter by tier.
    // Sort by lifetime points, highest first.
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            getTopFiveGuestsByTier(String tier) {

        DoublyLinkedList.ArrayList<LoyaltyRecord>
                filteredList =
                getGuestsByTier(tier);

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
    // Other List Access Methods
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
            getAllLoyaltyRecords() {

        return loyaltyList;
    }

    public DoublyLinkedList.ArrayList<RedemptionRecord>
            getAllRedemptions() {

        return redemptionList;
    }

    public void setLoyaltyList(
            DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList
    ) {
        this.loyaltyList = loyaltyList;
    }
}
