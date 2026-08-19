package control;

import entity.LoyaltyRecord;
import entity.RedemptionRecord;
import entity.Guest;
import adt.DoublyLinkedList;
import dao.LoyaltyDatabase;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoyaltyControl {

    private DoublyLinkedList<LoyaltyRecord> loyaltyList;
    private DoublyLinkedList<RedemptionRecord> redemptionList;
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

    //==================================================
    // Constructor
    //==================================================
    public LoyaltyControl() {

        loyaltyDatabase = new LoyaltyDatabase();

        loyaltyList = loyaltyDatabase.retrieveFromFile();

        if (loyaltyList == null) {
            loyaltyList = new DoublyLinkedList<>();
        }

        redemptionList = new DoublyLinkedList<>();

        lastMessage = "";
    }

    public LoyaltyControl(DoublyLinkedList<LoyaltyRecord> loyaltyList) {

        loyaltyDatabase = new LoyaltyDatabase();

        if (loyaltyList == null) {
            this.loyaltyList = new DoublyLinkedList<>();
        } else {
            this.loyaltyList = loyaltyList;
        }

        redemptionList = new DoublyLinkedList<>();

        lastMessage = "";
    }

    //==================================================
    // Refresh Loyalty Data
    //
    // Important:
    // VIP Allocation Module and Loyalty Module may use
    // different LoyaltyControl objects.
    //
    // This method reloads the latest shared
    // LoyaltyDatabase data.
    //==================================================
    private void refreshLoyaltyData() {

        if (loyaltyDatabase == null) {
            loyaltyDatabase = new LoyaltyDatabase();
        }

        DoublyLinkedList<LoyaltyRecord> latestList
                = loyaltyDatabase.retrieveFromFile();

        if (latestList == null) {
            loyaltyList = new DoublyLinkedList<>();
        } else {
            loyaltyList = latestList;
        }
    }

    //==================================================
    // 1. Display Loyalty Members
    //==================================================
    public DoublyLinkedList<LoyaltyRecord> getGuestsByTier(String tier) {

        DoublyLinkedList<LoyaltyRecord> result
                = new DoublyLinkedList<>();

        if (tier == null) {
            return result;
        }

        // Get latest data
        refreshLoyaltyData();

        for (int i = 1; i <= loyaltyList.getSize(); i++) {

            LoyaltyRecord record
                    = loyaltyList.getEntry(i);

            if (record == null) {
                continue;
            }

            if (tier.equalsIgnoreCase("All")
                    || record.getLoyaltyTier().equalsIgnoreCase(tier)) {

                result.add(record);
            }
        }

        return result;
    }

    //==================================================
    // 2. Search Loyalty Member
    //==================================================
    public LoyaltyRecord searchGuest(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        //==================================================
        // IMPORTANT
        //
        // Reload latest LoyaltyDatabase before searching.
        //
        // Example:
        // VIP Module creates R0021
        // Loyalty Module can immediately search R0021.
        //==================================================
        refreshLoyaltyData();

        for (int i = 1; i <= loyaltyList.getSize(); i++) {

            LoyaltyRecord record
                    = loyaltyList.getEntry(i);

            if (record == null
                    || record.getGuestID() == null) {

                continue;
            }

            if (record.getGuestID()
                    .equalsIgnoreCase(guestID.trim())) {

                return record;
            }
        }

        return null;
    }

    //==================================================
    // Create Loyalty Member
    //
    // OTHER MODULE FUNCTION
    // Used By VIP Allocation Module
    //==================================================
    public boolean createLoyaltyMember(
            Guest guest,
            int lifetimePoints
    ) {

        if (guest == null
                || guest.getGuestID() == null
                || guest.getGuestID().trim().isEmpty()) {

            lastMessage = "Invalid guest information.";

            return false;
        }

        if (lifetimePoints < 0) {

            lastMessage = "Lifetime points cannot be negative.";

            return false;
        }

        //==================================================
        // Search latest LoyaltyDatabase
        //==================================================
        LoyaltyRecord existingRecord
                = searchGuest(guest.getGuestID());

        //==================================================
        // Existing Loyalty Member
        //==================================================
        if (existingRecord != null) {

            existingRecord.setLifetimePoints(
                    lifetimePoints
            );

            // Automatically set correct tier
            updateTier(existingRecord);

            existingRecord.setExpiryDate(
                    LocalDate.now().plusYears(1)
            );

            saveLoyaltyData();

            lastMessage
                    = "Loyalty member updated successfully.";

            return true;
        }

        //==================================================
        // New Loyalty Member
        //
        // Available Points = 0
        //
        // Lifetime Points:
        // Standard = 0
        // Platinum = 2000
        // Diamond  = 4000
        // Elite    = 6000
        //==================================================
        LoyaltyRecord newRecord
                = new LoyaltyRecord(
                        guest,
                        0,
                        lifetimePoints,
                        LocalDate.now().plusYears(1)
                );

        // Automatically set correct Tier
        updateTier(newRecord);

        // Add into latest Loyalty List
        loyaltyList.add(newRecord);

        // Save shared LoyaltyDatabase
        saveLoyaltyData();

        lastMessage
                = "Loyalty member created successfully.";

        return true;
    }

    //==================================================
    // 3. Add Points
    //==================================================
    public boolean addPoints(
            String guestID,
            String roomType
    ) {

        // searchGuest() automatically refreshes
        // latest LoyaltyDatabase
        LoyaltyRecord record
                = searchGuest(guestID);

        if (record == null) {

            lastMessage
                    = "Loyalty member not found.";

            return false;
        }

        int points
                = calculatePointsByRoomType(
                        roomType
                );

        if (points == 0) {

            lastMessage
                    = "Invalid room type.";

            return false;
        }

        String oldTier
                = record.getLoyaltyTier();

        //==================================================
        // Add Available Points
        //==================================================
        record.setAvailablePoints(
                record.getAvailablePoints()
                + points
        );

        //==================================================
        // Add Lifetime Points
        //==================================================
        record.setLifetimePoints(
                record.getLifetimePoints()
                + points
        );

        //==================================================
        // Extend Expiry Date
        //==================================================
        record.setExpiryDate(
                LocalDate.now().plusYears(1)
        );

        //==================================================
        // Automatically Check VIP / Loyalty Tier Upgrade
        //==================================================
        updateTier(record);

        saveLoyaltyData();

        String newTier
                = record.getLoyaltyTier();

        if (!oldTier.equalsIgnoreCase(newTier)) {

            lastMessage
                    = points
                    + " points added successfully."
                    + "\nCongratulations, "
                    + record.getGuestName()
                    + "!"
                    + "\nYour loyalty tier has been upgraded from "
                    + oldTier
                    + " to "
                    + newTier
                    + ".";

        } else {

            lastMessage
                    = points
                    + " points added successfully."
                    + "\nAvailable Points: "
                    + record.getAvailablePoints()
                    + "\nExpiry Date: "
                    + record.getExpiryDate();
        }

        return true;
    }

    //==================================================
    // Calculate Points By Room Type
    //==================================================
    private int calculatePointsByRoomType(
            String roomType
    ) {

        if (roomType == null) {
            return 0;
        }

        switch (roomType.trim().toLowerCase()) {

            case "single":
            case "single room":
            case "small room":
                return 100;

            case "medium":
            case "medium room":
            case "middle room":
                return 200;

            case "large":
            case "large room":
            case "big room":
                return 300;

            default:
                return 0;
        }
    }

    //==================================================
    // 4. Update Tier
    //==================================================
    private void updateTier(
            LoyaltyRecord record
    ) {

        if (record == null) {
            return;
        }

        int lifetimePoints
                = record.getLifetimePoints();

        if (lifetimePoints >= 6000) {

            record.setLoyaltyTier(
                    "Elite"
            );

        } else if (lifetimePoints >= 4000) {

            record.setLoyaltyTier(
                    "Diamond"
            );

        } else if (lifetimePoints >= 2000) {

            record.setLoyaltyTier(
                    "Platinum"
            );

        } else {

            record.setLoyaltyTier(
                    "Standard"
            );
        }
    }

    //==================================================
    // 5. Reward Methods
    //==================================================
    public String[] getRewardsByTier(
            String tier
    ) {

        if (tier == null) {
            return new String[0];
        }

        if (tier.equalsIgnoreCase(
                "Standard")) {

            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER
            };
        }

        if (tier.equalsIgnoreCase(
                "Platinum")) {

            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER,
                FREE_MEAL,
                LATE_CHECK_OUT
            };
        }

        if (tier.equalsIgnoreCase(
                "Diamond")) {

            return new String[]{
                WELCOME_DRINK,
                FOOD_VOUCHER,
                FREE_MEAL,
                LATE_CHECK_OUT,
                ROOM_UPGRADE,
                SPA_VOUCHER
            };
        }

        if (tier.equalsIgnoreCase(
                "Elite")) {

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

    //==================================================
    // Get All Reward Names
    //==================================================
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

    //==================================================
    // Get Reward Points
    //==================================================
    public int getRewardPoints(
            String rewardName
    ) {

        if (rewardName == null) {
            return -1;
        }

        if (rewardName.equalsIgnoreCase(
                WELCOME_DRINK)) {

            return 200;
        }

        if (rewardName.equalsIgnoreCase(
                FOOD_VOUCHER)) {

            return 300;
        }

        if (rewardName.equalsIgnoreCase(
                FREE_MEAL)) {

            return 500;
        }

        if (rewardName.equalsIgnoreCase(
                LATE_CHECK_OUT)) {

            return 700;
        }

        if (rewardName.equalsIgnoreCase(
                ROOM_UPGRADE)) {

            return 1000;
        }

        if (rewardName.equalsIgnoreCase(
                SPA_VOUCHER)) {

            return 1200;
        }

        if (rewardName.equalsIgnoreCase(
                LOUNGE_ACCESS)) {

            return 1500;
        }

        if (rewardName.equalsIgnoreCase(
                FREE_NIGHT_STAY)) {

            return 2500;
        }

        return -1;
    }

    //==================================================
    // Check Reward Permission
    //==================================================
    public boolean canTierRedeemReward(
            String tier,
            String rewardName
    ) {

        String[] rewards
                = getRewardsByTier(
                        tier
                );

        for (String reward : rewards) {

            if (reward.equalsIgnoreCase(
                    rewardName)) {

                return true;
            }
        }

        return false;
    }

    //==================================================
    // 6. Redeem Reward
    //==================================================
    public boolean redeemReward(
            String guestID,
            String rewardName
    ) {

        // Get latest LoyaltyRecord
        LoyaltyRecord record
                = searchGuest(guestID);

        if (record == null) {

            lastMessage
                    = "Loyalty member not found.";

            return false;
        }

        int requiredPoints
                = getRewardPoints(
                        rewardName
                );

        if (requiredPoints <= 0) {

            lastMessage
                    = "Invalid reward selected.";

            return false;
        }

        if (!canTierRedeemReward(
                record.getLoyaltyTier(),
                rewardName)) {

            lastMessage
                    = "This reward is not available for the guest's tier.";

            return false;
        }

        if (record.getAvailablePoints()
                < requiredPoints) {

            lastMessage
                    = "Insufficient available points."
                    + "\nRequired Points: "
                    + requiredPoints
                    + "\nAvailable Points: "
                    + record.getAvailablePoints();

            return false;
        }

        record.setAvailablePoints(
                record.getAvailablePoints()
                - requiredPoints
        );

        RedemptionRecord redemption
                = new RedemptionRecord(
                        record.getGuest(),
                        rewardName,
                        LocalDate.now()
                );

        redemptionList.add(
                redemption
        );

        saveLoyaltyData();

        lastMessage
                = "Reward redeemed successfully!"
                + "\nGuest ID         : "
                + record.getGuestID()
                + "\nGuest Name       : "
                + record.getGuestName()
                + "\nReward           : "
                + rewardName
                + "\nPoints Used      : "
                + requiredPoints
                + "\nRemaining Points : "
                + record.getAvailablePoints()
                + "\nRedemption Date  : "
                + LocalDate.now();

        return true;
    }

    //==================================================
    // 7. Points Expiring Within 30 Days
    //==================================================
    public DoublyLinkedList<LoyaltyRecord>
            getPointsExpiringWithin30Days() {

        // Get latest Loyalty data
        refreshLoyaltyData();

        DoublyLinkedList<LoyaltyRecord> result
                = new DoublyLinkedList<>();

        LocalDate today
                = LocalDate.now();

        LocalDate endDate
                = today.plusDays(30);

        for (int i = 1;
                i <= loyaltyList.getSize();
                i++) {

            LoyaltyRecord record
                    = loyaltyList.getEntry(i);

            if (record == null) {
                continue;
            }

            LocalDate expiryDate
                    = record.getExpiryDate();

            if (expiryDate == null) {
                continue;
            }

            boolean notExpired
                    = !expiryDate.isBefore(
                            today
                    );

            boolean within30Days
                    = !expiryDate.isAfter(
                            endDate
                    );

            boolean hasPoints
                    = record.getAvailablePoints()
                    > 0;

            if (notExpired
                    && within30Days
                    && hasPoints) {

                result.add(record);
            }
        }

        return result;
    }

    //==================================================
    // 8. Get Expired Points Guests
    //==================================================
    public DoublyLinkedList<LoyaltyRecord>
            getExpiredPointsGuests() {

        // Get latest Loyalty data
        refreshLoyaltyData();

        DoublyLinkedList<LoyaltyRecord> result
                = new DoublyLinkedList<>();

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= loyaltyList.getSize();
                i++) {

            LoyaltyRecord record
                    = loyaltyList.getEntry(i);

            if (record == null) {
                continue;
            }

            LocalDate expiryDate
                    = record.getExpiryDate();

            if (expiryDate == null) {
                continue;
            }

            if (expiryDate.isBefore(today)
                    && record.getAvailablePoints()
                    > 0) {

                result.add(record);
            }
        }

        return result;
    }

    //==================================================
    // 9. Process Expired Points
    //==================================================
    public int processExpiredPoints() {

        DoublyLinkedList<LoyaltyRecord>
                expiredList
                = getExpiredPointsGuests();

        int totalProcessed
                = expiredList.getSize();

        for (int i = 1;
                i <= expiredList.getSize();
                i++) {

            LoyaltyRecord record
                    = expiredList.getEntry(i);

            if (record != null) {

                record.setAvailablePoints(
                        0
                );
            }
        }

        if (totalProcessed > 0) {

            saveLoyaltyData();

            lastMessage
                    = totalProcessed
                    + " expired loyalty record(s) processed.";

        } else {

            lastMessage
                    = "No expired points found.";
        }

        return totalProcessed;
    }

    //==================================================
    // Get Days Remaining
    //==================================================
    public long getDaysRemaining(
            LoyaltyRecord record
    ) {

        if (record == null
                || record.getExpiryDate()
                        == null) {

            return 0;
        }

        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                record.getExpiryDate()
        );
    }

    //==================================================
    // 10. Daily Reward Redemption Report
    //==================================================
    public DoublyLinkedList<RedemptionRecord>
            getTodayRedemptions(
                    String rewardName
            ) {

        DoublyLinkedList<RedemptionRecord> result
                = new DoublyLinkedList<>();

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= redemptionList.getSize();
                i++) {

            RedemptionRecord redemption
                    = redemptionList.getEntry(i);

            if (redemption == null
                    || redemption
                            .getRedemptionDate()
                            == null) {

                continue;
            }

            boolean sameDate
                    = redemption
                            .getRedemptionDate()
                            .equals(today);

            boolean sameReward
                    = rewardName == null
                    || rewardName.equalsIgnoreCase(
                            ALL_REWARDS
                    )
                    || redemption
                            .getRewardName()
                            .equalsIgnoreCase(
                                    rewardName
                            );

            if (sameDate
                    && sameReward) {

                result.add(redemption);
            }
        }

        return result;
    }

    //==================================================
    // 11. Count Tier And Reward Redemptions
    //==================================================
    public int countTodayRedemptions(
            String tier,
            String rewardName
    ) {

        int count = 0;

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= redemptionList.getSize();
                i++) {

            RedemptionRecord redemption
                    = redemptionList.getEntry(i);

            if (redemption == null) {
                continue;
            }

            boolean sameDate
                    = redemption
                            .getRedemptionDate()
                            .equals(today);

            boolean sameTier
                    = redemption
                            .getLoyaltyTier()
                            .equalsIgnoreCase(
                                    tier
                            );

            boolean sameReward
                    = redemption
                            .getRewardName()
                            .equalsIgnoreCase(
                                    rewardName
                            );

            if (sameDate
                    && sameTier
                    && sameReward) {

                count++;
            }
        }

        return count;
    }

    //==================================================
    // Count Today Redemptions By Tier
    //==================================================
    public int countTodayRedemptionsByTier(
            String tier
    ) {

        int count = 0;

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= redemptionList.getSize();
                i++) {

            RedemptionRecord redemption
                    = redemptionList.getEntry(i);

            if (redemption == null) {
                continue;
            }

            if (redemption
                    .getRedemptionDate()
                    .equals(today)
                    && redemption
                            .getLoyaltyTier()
                            .equalsIgnoreCase(
                                    tier
                            )) {

                count++;
            }
        }

        return count;
    }

    //==================================================
    // Count Today Redemptions By Reward
    //==================================================
    public int countTodayRedemptionsByReward(
            String rewardName
    ) {

        int count = 0;

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= redemptionList.getSize();
                i++) {

            RedemptionRecord redemption
                    = redemptionList.getEntry(i);

            if (redemption == null) {
                continue;
            }

            if (redemption
                    .getRedemptionDate()
                    .equals(today)
                    && redemption
                            .getRewardName()
                            .equalsIgnoreCase(
                                    rewardName
                            )) {

                count++;
            }
        }

        return count;
    }

    //==================================================
    // 12. Calculate Redemption Points
    //==================================================
    public int calculateTodayPointsUsedByReward(
            String rewardName
    ) {

        int totalRedemptions
                = countTodayRedemptionsByReward(
                        rewardName
                );

        int rewardPoints
                = getRewardPoints(
                        rewardName
                );

        if (rewardPoints < 0) {
            return 0;
        }

        return totalRedemptions
                * rewardPoints;
    }

    //==================================================
    // Calculate Today Total Points Used
    //==================================================
    public int calculateTodayTotalPointsUsed() {

        int totalPoints = 0;

        LocalDate today
                = LocalDate.now();

        for (int i = 1;
                i <= redemptionList.getSize();
                i++) {

            RedemptionRecord redemption
                    = redemptionList.getEntry(i);

            if (redemption == null) {
                continue;
            }

            if (redemption
                    .getRedemptionDate()
                    .equals(today)) {

                int points
                        = getRewardPoints(
                                redemption
                                        .getRewardName()
                        );

                if (points > 0) {

                    totalPoints += points;
                }
            }
        }

        return totalPoints;
    }

    //==================================================
    // 13. Top 5 Loyalty Members Report
    //==================================================
    public DoublyLinkedList<LoyaltyRecord>
            getTopFiveMembersByPointsUsed(
                    String selectedTier
            ) {

        // Get latest Loyalty data
        refreshLoyaltyData();

        DoublyLinkedList<LoyaltyRecord> filteredList
                = new DoublyLinkedList<>();

        DoublyLinkedList<LoyaltyRecord> topFiveList
                = new DoublyLinkedList<>();

        if (selectedTier == null) {
            return topFiveList;
        }

        for (int i = 1;
                i <= loyaltyList.getSize();
                i++) {

            LoyaltyRecord record
                    = loyaltyList.getEntry(i);

            if (record == null) {
                continue;
            }

            if (selectedTier
                    .equalsIgnoreCase("All")
                    || record
                            .getLoyaltyTier()
                            .equalsIgnoreCase(
                                    selectedTier
                            )) {

                filteredList.add(record);
            }
        }

        while (!filteredList.isEmpty()
                && topFiveList.getSize() < 5) {

            int highestPosition = 1;

            LoyaltyRecord highestRecord
                    = filteredList.getEntry(1);

            int highestPointsUsed
                    = highestRecord
                            .getLifetimePoints()
                    - highestRecord
                            .getAvailablePoints();

            for (int i = 2;
                    i <= filteredList.getSize();
                    i++) {

                LoyaltyRecord currentRecord
                        = filteredList
                                .getEntry(i);

                int currentPointsUsed
                        = currentRecord
                                .getLifetimePoints()
                        - currentRecord
                                .getAvailablePoints();

                if (currentPointsUsed
                        > highestPointsUsed) {

                    highestPointsUsed
                            = currentPointsUsed;

                    highestRecord
                            = currentRecord;

                    highestPosition
                            = i;
                }
            }

            topFiveList.add(
                    highestRecord
            );

            filteredList.remove(
                    highestPosition
            );
        }

        return topFiveList;
    }

    //==================================================
    // Save Loyalty Data
    //==================================================
    private void saveLoyaltyData() {

        if (loyaltyDatabase != null) {

            loyaltyDatabase.saveToFile(
                    loyaltyList
            );
        }
    }

    //==================================================
    // Getter Methods
    //==================================================
    public DoublyLinkedList<LoyaltyRecord>
            getAllLoyaltyRecords() {

        // Always get latest data
        refreshLoyaltyData();

        return loyaltyList;
    }

    public DoublyLinkedList<RedemptionRecord>
            getAllRedemptions() {

        return redemptionList;
    }

    public String getLastMessage() {

        return lastMessage;
    }

    //==================================================
    // Setter Methods
    //==================================================
    public void setLoyaltyList(
            DoublyLinkedList<LoyaltyRecord> loyaltyList
    ) {

        if (loyaltyList == null) {

            this.loyaltyList
                    = new DoublyLinkedList<>();

        } else {

            this.loyaltyList
                    = loyaltyList;
        }
    }
}