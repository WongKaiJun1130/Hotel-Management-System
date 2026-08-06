/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Control;

import System_Entity.LoyaltyRecord;
import System_Entity.RedemptionRecord;
import System_adt.DoublyLinkedList;
import dao.LoyaltyDatabase;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class LoyaltyControl {

    private DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList;
    private DoublyLinkedList.ArrayList<RedemptionRecord> redemptionList;
    private LoyaltyDatabase loyaltyDatabase;
    private String lastMessage;

    public static final String WELCOME_DRINK = "Free Welcome Drink";
    public static final String FOOD_VOUCHER = "RM10 Food Voucher";
    public static final String FREE_MEAL = "Free One Meal";
    public static final String LATE_CHECK_OUT = "Late Check-Out";
    public static final String ROOM_UPGRADE = "Free Room Upgrade";
    public static final String SPA_VOUCHER = "RM50 Spa Voucher";
    public static final String LOUNGE_ACCESS = "Free Private Lounge Access";
    public static final String FREE_NIGHT_STAY = "Free One-Night Stay";
    public static final String ALL_REWARDS = "All Rewards";

    // ==================================================
    // Constructor
    // ==================================================
    public LoyaltyControl() {
        loyaltyDatabase = new LoyaltyDatabase();
        loyaltyList = loyaltyDatabase.retrieveFromFile();

        if (loyaltyList == null) loyaltyList = new DoublyLinkedList.ArrayList<>();

        redemptionList = new DoublyLinkedList.ArrayList<>();
        lastMessage = "";
    }

    public LoyaltyControl(DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList) {
        loyaltyDatabase = new LoyaltyDatabase();

        if (loyaltyList == null) {
            this.loyaltyList = new DoublyLinkedList.ArrayList<>();
        } else {
            this.loyaltyList = loyaltyList;
        }

        redemptionList = new DoublyLinkedList.ArrayList<>();
        lastMessage = "";
    }

    // ==================================================
    // 1. Display Loyalty Members
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord> getGuestsByTier(String tier) {
        DoublyLinkedList.ArrayList<LoyaltyRecord> result = new DoublyLinkedList.ArrayList<>();

        if (tier == null) return result;

        for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = loyaltyList.getEntry(i);

            if (tier.equalsIgnoreCase("All")
                    || record.getLoyaltyTier().equalsIgnoreCase(tier)) {
                result.add(record);
            }
        }

        return result;
    }

    // ==================================================
    // 2. Search Loyalty Member
    // ==================================================
    public LoyaltyRecord searchGuest(String guestID) {
        if (guestID == null || guestID.trim().isEmpty()) return null;

        for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = loyaltyList.getEntry(i);

            if (record.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return record;
            }
        }

        return null;
    }

    // ==================================================
    // 3. Add Points
    // ==================================================
    public boolean addPoints(String guestID, String roomType) {
        LoyaltyRecord record = searchGuest(guestID);

        if (record == null) {
            lastMessage = "Loyalty member not found.";
            return false;
        }

        int points = calculatePointsByRoomType(roomType);

        if (points == 0) {
            lastMessage = "Invalid room type.";
            return false;
        }

        String oldTier = record.getLoyaltyTier();

        record.setAvailablePoints(record.getAvailablePoints() + points);
        record.setLifetimePoints(record.getLifetimePoints() + points);
        record.setExpiryDate(LocalDate.now().plusYears(1));

        updateTier(record);
        saveLoyaltyData();

        String newTier = record.getLoyaltyTier();

        if (!oldTier.equalsIgnoreCase(newTier)) {
            lastMessage = points + " points added successfully."
                    + "\nCongratulations, " + record.getGuestName() + "!"
                    + "\nYour loyalty tier has been upgraded from "
                    + oldTier + " to " + newTier + ".";
        } else {
            lastMessage = points + " points added successfully."
                    + "\nAvailable Points: " + record.getAvailablePoints()
                    + "\nExpiry Date: " + record.getExpiryDate();
        }

        return true;
    }

    private int calculatePointsByRoomType(String roomType) {
        if (roomType == null) return 0;

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
    // ==================================================
    private void updateTier(LoyaltyRecord record) {
        int lifetimePoints = record.getLifetimePoints();

        if (lifetimePoints >= 6000) {
            record.setLoyaltyTier("Elite");
        } else if (lifetimePoints >= 4000) {
            record.setLoyaltyTier("Diamond");
        } else if (lifetimePoints >= 2000) {
            record.setLoyaltyTier("Platinum");
        } else {
            record.setLoyaltyTier("Standard");
        }
    }

    // ==================================================
    // 5. Reward Methods
    // ==================================================
    public String[] getRewardsByTier(String tier) {
        if (tier == null) return new String[0];

        if (tier.equalsIgnoreCase("Standard")) {
            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER
            };
        }

        if (tier.equalsIgnoreCase("Platinum")) {
            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER,
                FREE_MEAL,
                LATE_CHECK_OUT
            };
        }

        if (tier.equalsIgnoreCase("Diamond")) {
            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER,
                FREE_MEAL,
                LATE_CHECK_OUT,
                ROOM_UPGRADE,
                SPA_VOUCHER
            };
        }

        if (tier.equalsIgnoreCase("Elite")) {
            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER,
                FREE_MEAL,
                LATE_CHECK_OUT,
                ROOM_UPGRADE,
                SPA_VOUCHER,
                LOUNGE_ACCESS,
                FREE_NIGHT_STAY
            };
        }

        return new String[0];
    }

    public String[] getAllRewardNames() {
        return new String[]{
            WELCOME_DRINK,
            FOOD_VOUCHER,
            FREE_MEAL,
            LATE_CHECK_OUT,
            ROOM_UPGRADE,
            SPA_VOUCHER,
            LOUNGE_ACCESS,
            FREE_NIGHT_STAY
        };
    }

    public int getRewardPoints(String rewardName) {
        if (rewardName == null) return -1;

        if (rewardName.equalsIgnoreCase(WELCOME_DRINK)) return 200;
        if (rewardName.equalsIgnoreCase(FOOD_VOUCHER)) return 300;
        if (rewardName.equalsIgnoreCase(FREE_MEAL)) return 500;
        if (rewardName.equalsIgnoreCase(LATE_CHECK_OUT)) return 700;
        if (rewardName.equalsIgnoreCase(ROOM_UPGRADE)) return 1000;
        if (rewardName.equalsIgnoreCase(SPA_VOUCHER)) return 1200;
        if (rewardName.equalsIgnoreCase(LOUNGE_ACCESS)) return 1500;
        if (rewardName.equalsIgnoreCase(FREE_NIGHT_STAY)) return 2500;

        return -1;
    }

    public boolean canTierRedeemReward(String tier, String rewardName) {
        String[] rewards = getRewardsByTier(tier);

        for (String reward : rewards) {
            if (reward.equalsIgnoreCase(rewardName)) return true;
        }

        return false;
    }

    // ==================================================
    // 6. Redeem Reward
    // ==================================================
    public boolean redeemReward(String guestID, String rewardName) {
        LoyaltyRecord record = searchGuest(guestID);

        if (record == null) {
            lastMessage = "Loyalty member not found.";
            return false;
        }

        int requiredPoints = getRewardPoints(rewardName);

        if (requiredPoints <= 0) {
            lastMessage = "Invalid reward selected.";
            return false;
        }

        if (!canTierRedeemReward(record.getLoyaltyTier(), rewardName)) {
            lastMessage = "This reward is not available for the guest's tier.";
            return false;
        }

        if (record.getAvailablePoints() < requiredPoints) {
            lastMessage = "Insufficient available points."
                    + "\nRequired Points: " + requiredPoints
                    + "\nAvailable Points: " + record.getAvailablePoints();
            return false;
        }

        record.setAvailablePoints(record.getAvailablePoints() - requiredPoints);

        RedemptionRecord redemption = new RedemptionRecord(
                record.getGuest(),
                rewardName,
                LocalDate.now()
        );

        redemptionList.add(redemption);
        saveLoyaltyData();

        lastMessage = "Reward redeemed successfully!"
                + "\nGuest ID         : " + record.getGuestID()
                + "\nGuest Name       : " + record.getGuestName()
                + "\nReward           : " + rewardName
                + "\nPoints Used      : " + requiredPoints
                + "\nRemaining Points : " + record.getAvailablePoints()
                + "\nRedemption Date  : " + LocalDate.now();

        return true;
    }

    // ==================================================
    // 7. View Points Expiring Within 30 Days
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord> getPointsExpiringWithin30Days() {
        DoublyLinkedList.ArrayList<LoyaltyRecord> result = new DoublyLinkedList.ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = loyaltyList.getEntry(i);
            LocalDate expiryDate = record.getExpiryDate();

            if (expiryDate == null) continue;

            boolean notExpired = !expiryDate.isBefore(today);
            boolean within30Days = !expiryDate.isAfter(endDate);
            boolean hasPoints = record.getAvailablePoints() > 0;

            if (notExpired && within30Days && hasPoints) result.add(record);
        }

        return result;
    }

    // ==================================================
    // 8. Get Expired Points Guests
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord> getExpiredPointsGuests() {
        DoublyLinkedList.ArrayList<LoyaltyRecord> result = new DoublyLinkedList.ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = loyaltyList.getEntry(i);
            LocalDate expiryDate = record.getExpiryDate();

            if (expiryDate == null) continue;

            if (expiryDate.isBefore(today) && record.getAvailablePoints() > 0) {
                result.add(record);
            }
        }

        return result;
    }

    // ==================================================
    // 9. Process Expired Points
    // ==================================================
    public int processExpiredPoints() {
        DoublyLinkedList.ArrayList<LoyaltyRecord> expiredList = getExpiredPointsGuests();
        int totalProcessed = expiredList.getNumberOfEntries();

        for (int i = 1; i <= expiredList.getNumberOfEntries(); i++) {
            LoyaltyRecord record = expiredList.getEntry(i);
            record.setAvailablePoints(0);
        }

        if (totalProcessed > 0) {
            saveLoyaltyData();
            lastMessage = totalProcessed + " expired loyalty record(s) processed.";
        } else {
            lastMessage = "No expired points found.";
        }

        return totalProcessed;
    }

    public long getDaysRemaining(LoyaltyRecord record) {
        if (record == null || record.getExpiryDate() == null) return 0;

        return ChronoUnit.DAYS.between(LocalDate.now(), record.getExpiryDate());
    }

    // ==================================================
    // 10. Daily Reward Redemption Report
    // ==================================================
    public DoublyLinkedList.ArrayList<RedemptionRecord> getTodayRedemptions(String rewardName) {
        DoublyLinkedList.ArrayList<RedemptionRecord> result = new DoublyLinkedList.ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= redemptionList.getNumberOfEntries(); i++) {
            RedemptionRecord redemption = redemptionList.getEntry(i);

            boolean sameDate = redemption.getRedemptionDate().equals(today);
            boolean sameReward = rewardName == null
                    || rewardName.equalsIgnoreCase(ALL_REWARDS)
                    || redemption.getRewardName().equalsIgnoreCase(rewardName);

            if (sameDate && sameReward) result.add(redemption);
        }

        return result;
    }

    // ==================================================
    // 11. Count Tier And Reward Redemptions
    // ==================================================
    public int countTodayRedemptions(String tier, String rewardName) {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= redemptionList.getNumberOfEntries(); i++) {
            RedemptionRecord redemption = redemptionList.getEntry(i);

            boolean sameDate = redemption.getRedemptionDate().equals(today);
            boolean sameTier = redemption.getLoyaltyTier().equalsIgnoreCase(tier);
            boolean sameReward = redemption.getRewardName().equalsIgnoreCase(rewardName);

            if (sameDate && sameTier && sameReward) count++;
        }

        return count;
    }

    public int countTodayRedemptionsByTier(String tier) {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= redemptionList.getNumberOfEntries(); i++) {
            RedemptionRecord redemption = redemptionList.getEntry(i);

            if (redemption.getRedemptionDate().equals(today)
                    && redemption.getLoyaltyTier().equalsIgnoreCase(tier)) {
                count++;
            }
        }

        return count;
    }

    public int countTodayRedemptionsByReward(String rewardName) {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= redemptionList.getNumberOfEntries(); i++) {
            RedemptionRecord redemption = redemptionList.getEntry(i);

            if (redemption.getRedemptionDate().equals(today)
                    && redemption.getRewardName().equalsIgnoreCase(rewardName)) {
                count++;
            }
        }

        return count;
    }

    // ==================================================
    // 12. Calculate Redemption Points
    // ==================================================
    public int calculateTodayPointsUsedByReward(String rewardName) {
        int totalRedemptions = countTodayRedemptionsByReward(rewardName);
        int rewardPoints = getRewardPoints(rewardName);

        if (rewardPoints < 0) return 0;

        return totalRedemptions * rewardPoints;
    }

    public int calculateTodayTotalPointsUsed() {
        int totalPoints = 0;
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= redemptionList.getNumberOfEntries(); i++) {
            RedemptionRecord redemption = redemptionList.getEntry(i);

            if (redemption.getRedemptionDate().equals(today)) {
                totalPoints += getRewardPoints(redemption.getRewardName());
            }
        }

        return totalPoints;
    }

    // ==================================================
    // 13. Top 5 Loyalty Members Report
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord>
                getTopFiveMembersByPointsUsed(String selectedTier) {

            DoublyLinkedList.ArrayList<LoyaltyRecord> filteredList
                    = new DoublyLinkedList.ArrayList<>();

            // Filter members by selected tier
            for (int i = 1; i <= loyaltyList.getNumberOfEntries(); i++) {
                LoyaltyRecord record = loyaltyList.getEntry(i);

                if (selectedTier.equalsIgnoreCase("All")
                        || record.getLoyaltyTier()
                                .equalsIgnoreCase(selectedTier)) {

                    filteredList.add(record);
                }
            }

            // Sort by points used from highest to lowest
            filteredList.sort((record1, record2) -> {
                int pointsUsed1
                        = record1.getLifetimePoints()
                        - record1.getAvailablePoints();

                int pointsUsed2
                        = record2.getLifetimePoints()
                        - record2.getAvailablePoints();

                return Integer.compare(pointsUsed2, pointsUsed1);
            });

            // Get top 5 members
            DoublyLinkedList.ArrayList<LoyaltyRecord> topFiveList
                    = new DoublyLinkedList.ArrayList<>();

            int limit = Math.min(
                    5,
                    filteredList.getNumberOfEntries()
            );

            for (int i = 1; i <= limit; i++) {
                topFiveList.add(filteredList.getEntry(i));
            }

            return topFiveList;
        }

    // ==================================================
    // Save Loyalty Data
    // ==================================================
    private void saveLoyaltyData() {
        loyaltyDatabase.saveToFile(loyaltyList);
    }

    // ==================================================
    // Getter Methods
    // ==================================================
    public DoublyLinkedList.ArrayList<LoyaltyRecord> getAllLoyaltyRecords() {
        return loyaltyList;
    }

    public DoublyLinkedList.ArrayList<RedemptionRecord> getAllRedemptions() {
        return redemptionList;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    // ==================================================
    // Setter Methods
    // ==================================================
    public void setLoyaltyList(DoublyLinkedList.ArrayList<LoyaltyRecord> loyaltyList) {
        if (loyaltyList == null) {
            this.loyaltyList = new DoublyLinkedList.ArrayList<>();
        } else {
            this.loyaltyList = loyaltyList;
        }
    }
}