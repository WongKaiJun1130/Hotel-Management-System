/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_UI;

import System_Control.LoyaltyControl;
import System_Entity.LoyaltyRecord;
import System_Entity.RedemptionRecord;
import System_Utility.InputUtility;
import System_adt.DoublyLinkedList;
import java.time.LocalDate;

public class LoyaltyAndRewardsUI {

    private LoyaltyControl loyaltyControl;

    // ==================================================
    // Constructor
    // ==================================================
    public LoyaltyAndRewardsUI() {
        loyaltyControl = new LoyaltyControl();
    }

    public LoyaltyAndRewardsUI(LoyaltyControl loyaltyControl) {
        this.loyaltyControl = loyaltyControl;
    }

    // ==================================================
    // Loyalty And Rewards Menu
    // ==================================================
    public void loyaltyMenu() {
        int choice;

        do {
            InputUtility.clearScreen();

            System.out.println("+------------------------------------------------+");
            System.out.println("|           LOYALTY AND REWARDS MENU             |");
            System.out.println("+------------------------------------------------+");
            System.out.println("| 1. Display Loyalty Members                     |");
            System.out.println("| 2. Search Loyalty Member                       |");
            System.out.println("| 3. Redeem Reward                               |");
            System.out.println("| 4. Points Expiry Management                    |");
            System.out.println("| 5. Daily Reward Redemption Report              |");
            System.out.println("| 6. Top Loyalty Members Report                  |");
            System.out.println("| 0. Back                                        |");
            System.out.println("+------------------------------------------------+");
            System.out.print("Enter Choice: ");

            choice = InputUtility.getIntInput();

            switch (choice) {
                case 1:
                    displayLoyaltyMembers();
                    break;

                case 2:
                    searchLoyaltyMember();
                    break;

                case 3:
                    redeemReward();
                    break;

                case 4:
                    pointsExpiryManagement();
                    break;

                case 5:
                    dailyRewardRedemptionReport();
                    break;

                case 6:
                    topLoyaltyMembersReport();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("\nInvalid choice.");
                    InputUtility.pressEnterToContinue();
            }

        } while (choice != 0);
    }

    // ==================================================
    // 1. Display Loyalty Members
    // ==================================================
    private void displayLoyaltyMembers() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("            DISPLAY LOYALTY MEMBERS");
        System.out.println("==================================================");

        String tier = selectTier();
        if (tier == null) return;

        DoublyLinkedList.ArrayList<LoyaltyRecord> result = loyaltyControl.getGuestsByTier(tier);

        System.out.println("\nSelected Tier: " + tier);
        displayLoyaltyResult(result, "LOYALTY MEMBER LIST");

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // 2. Search Loyalty Member
    // ==================================================
    private void searchLoyaltyMember() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("             SEARCH LOYALTY MEMBER");
        System.out.println("==================================================");
        System.out.print("Enter Guest ID: ");

        String guestID = InputUtility.getStringInput();
        LoyaltyRecord record = loyaltyControl.searchGuest(guestID);

        if (record == null) {
            System.out.println("\nLoyalty member not found.");
        } else {
            System.out.println("\n==================================================");
            System.out.println(record);
            System.out.println("==================================================");
        }

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // 3. Redeem Reward
    // ==================================================
    private void redeemReward() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("                  REDEEM REWARD");
        System.out.println("==================================================");
        System.out.print("Enter Guest ID: ");

        String guestID = InputUtility.getStringInput();
        LoyaltyRecord record = loyaltyControl.searchGuest(guestID);

        if (record == null) {
            System.out.println("\nLoyalty member not found.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println("\n==================================================");
        System.out.println(record);
        System.out.println("==================================================");

        String[] rewards = loyaltyControl.getRewardsByTier(record.getLoyaltyTier());

        if (rewards.length == 0) {
            System.out.println("\nNo rewards available.");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.println("\nAvailable Rewards");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < rewards.length; i++) {
            int points = loyaltyControl.getRewardPoints(rewards[i]);
            System.out.printf("%d. %-32s %,d points%n", i + 1, rewards[i], points);
        }

        System.out.println("0. Cancel");
        System.out.print("\nSelect Reward: ");

        int rewardChoice = InputUtility.getIntInput();

        if (rewardChoice == 0) return;

        if (rewardChoice < 1 || rewardChoice > rewards.length) {
            System.out.println("\nInvalid reward choice.");
            InputUtility.pressEnterToContinue();
            return;
        }

        String selectedReward = rewards[rewardChoice - 1];
        int requiredPoints = loyaltyControl.getRewardPoints(selectedReward);

        System.out.println("\n==================================================");
        System.out.println("Guest Name       : " + record.getGuestName());
        System.out.println("Loyalty Tier     : " + record.getLoyaltyTier());
        System.out.println("Selected Reward  : " + selectedReward);
        System.out.println("Required Points  : " + requiredPoints);
        System.out.println("Available Points : " + record.getAvailablePoints());
        System.out.println("==================================================");
        System.out.println("1. Confirm Redemption");
        System.out.println("2. Cancel");
        System.out.print("\nEnter Choice: ");

        int confirm = InputUtility.getIntInput();

        if (confirm == 1) {
            loyaltyControl.redeemReward(guestID, selectedReward);
            System.out.println("\n" + loyaltyControl.getLastMessage());
        } else {
            System.out.println("\nRedemption cancelled.");
        }

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // 4. Points Expiry Management
    // ==================================================
    private void pointsExpiryManagement() {
        int choice;

        do {
            InputUtility.clearScreen();

            System.out.println("==================================================");
            System.out.println("          POINTS EXPIRY MANAGEMENT");
            System.out.println("==================================================");
            System.out.println("1. View Points Expiring Within 30 Days");
            System.out.println("2. Process Expired Points");
            System.out.println("0. Back");
            System.out.print("\nEnter Choice: ");

            choice = InputUtility.getIntInput();

            switch (choice) {
                case 1:
                    viewPointsExpiringWithin30Days();
                    break;

                case 2:
                    processExpiredPoints();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("\nInvalid choice.");
                    InputUtility.pressEnterToContinue();
            }

        } while (choice != 0);
    }

    // ==================================================
    // View Points Expiring Within 30 Days
    // ==================================================
    private void viewPointsExpiringWithin30Days() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("       POINTS EXPIRING WITHIN 30 DAYS");
        System.out.println("==================================================");

        DoublyLinkedList.ArrayList<LoyaltyRecord> result =
                loyaltyControl.getPointsExpiringWithin30Days();

        if (result.isEmpty()) {
            System.out.println("\nNo points will expire within the next 30 days.");
        } else if (result.getNumberOfEntries() == 1) {
            LoyaltyRecord record = result.getEntry(1);

            System.out.println("\n" + record);
            System.out.println("Days Remaining: " + loyaltyControl.getDaysRemaining(record));
        } else {
            displayExpiringPointsTable(result);
        }

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // Process Expired Points
    // ==================================================
    private void processExpiredPoints() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("             PROCESS EXPIRED POINTS");
        System.out.println("==================================================");

        DoublyLinkedList.ArrayList<LoyaltyRecord> expiredList =
                loyaltyControl.getExpiredPointsGuests();

        if (expiredList.isEmpty()) {
            System.out.println("\nNo expired points found.");
            InputUtility.pressEnterToContinue();
            return;
        }

        if (expiredList.getNumberOfEntries() == 1) {
            System.out.println("\n" + expiredList.getEntry(1));
        } else {
            displayExpiredPointsTable(expiredList);
        }

        System.out.println("\n" + expiredList.getNumberOfEntries()
                + " guest(s) have expired available points.");
        System.out.println("1. Clear Expired Points");
        System.out.println("2. Cancel");
        System.out.print("\nEnter Choice: ");

        int confirm = InputUtility.getIntInput();

        if (confirm == 1) {
            loyaltyControl.processExpiredPoints();
            System.out.println("\n" + loyaltyControl.getLastMessage());
        } else {
            System.out.println("\nExpired points processing cancelled.");
        }

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // 5. Daily Reward Redemption Report
    // ==================================================
    private void dailyRewardRedemptionReport() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("       DAILY REWARD REDEMPTION REPORT");
        System.out.println("==================================================");

        String[] rewards = loyaltyControl.getAllRewardNames();

        for (int i = 0; i < rewards.length; i++) {
            System.out.println((i + 1) + ". " + rewards[i]);
        }

        System.out.println((rewards.length + 1) + ". All Rewards");
        System.out.println("0. Back");
        System.out.print("\nSelect Reward: ");

        int choice = InputUtility.getIntInput();

        if (choice == 0) return;

        if (choice < 1 || choice > rewards.length + 1) {
            System.out.println("\nInvalid reward choice.");
            InputUtility.pressEnterToContinue();
            return;
        }

        if (choice == rewards.length + 1) {
            displayAllRewardsReport();
        } else {
            displaySingleRewardReport(rewards[choice - 1]);
        }

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // Single Reward Report
    // ==================================================
    private void displaySingleRewardReport(String rewardName) {
        DoublyLinkedList.ArrayList<RedemptionRecord> result =
                loyaltyControl.getTodayRedemptions(rewardName);

        int pointsEach = loyaltyControl.getRewardPoints(rewardName);

        InputUtility.clearScreen();

        System.out.println("================================================================================");
        System.out.println("                    DAILY REWARD REDEMPTION REPORT");
        System.out.println("================================================================================");
        System.out.println("Date        : " + LocalDate.now());
        System.out.println("Reward      : " + rewardName);
        System.out.println("Points Each : " + pointsEach);
        System.out.println("--------------------------------------------------------------------------------");

        if (result.isEmpty()) {
            System.out.println("No redemption records found.");
            System.out.println("================================================================================");
            System.out.println("Total Redemptions : 0");
            System.out.println("Total Points Used : 0");
            System.out.println("================================================================================");
            return;
        }

        System.out.printf("%-4s %-10s %-25s %-12s%n",
                "No.", "Guest ID", "Guest Name", "Tier");
        System.out.println("--------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            RedemptionRecord record = result.getEntry(i);

            System.out.printf("%-4d %-10s %-25s %-12s%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 25),
                    record.getLoyaltyTier());
        }

        int standard = countTierInResult(result, "Standard");
        int platinum = countTierInResult(result, "Platinum");
        int diamond = countTierInResult(result, "Diamond");
        int elite = countTierInResult(result, "Elite");
        int total = result.getNumberOfEntries();
        int totalPoints = loyaltyControl.calculateTodayPointsUsedByReward(rewardName);

        System.out.println("================================================================================");
        System.out.println("TIER SUMMARY");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-20s %d%n", "Standard:", standard);
        System.out.printf("%-20s %d%n", "Platinum:", platinum);
        System.out.printf("%-20s %d%n", "Diamond:", diamond);
        System.out.printf("%-20s %d%n", "Elite:", elite);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-20s %d%n", "Total Redemptions:", total);
        System.out.printf("%-20s %,d%n", "Total Points Used:", totalPoints);
        System.out.println("================================================================================");
    }

    // ==================================================
    // All Rewards Report
    // ==================================================
    private void displayAllRewardsReport() {
        DoublyLinkedList.ArrayList<RedemptionRecord> result =
                loyaltyControl.getTodayRedemptions(LoyaltyControl.ALL_REWARDS);

        InputUtility.clearScreen();

        System.out.println("==================================================================================================================");
        System.out.println("                                      DAILY REWARD REDEMPTION REPORT");
        System.out.println("==================================================================================================================");
        System.out.println("Date   : " + LocalDate.now());
        System.out.println("Filter : All Rewards");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        if (result.isEmpty()) {
            System.out.println("No redemption records found.");
            System.out.println("==================================================================================================================");
            return;
        }

        System.out.printf("%-4s %-10s %-22s %-11s %-35s%n",
                "No.", "Guest ID", "Guest Name", "Tier", "Reward Name");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            RedemptionRecord record = result.getEntry(i);

            System.out.printf("%-4d %-10s %-22s %-11s %-35s%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 22),
                    record.getLoyaltyTier(),
                    limitText(record.getRewardName(), 35));
        }

        displayRewardTierMatrix();
    }

    // ==================================================
    // Reward And Tier Matrix Summary
    // ==================================================
    private void displayRewardTierMatrix() {
        String[] tiers = {"Standard", "Platinum", "Diamond", "Elite"};
        String[] rewards = loyaltyControl.getAllRewardNames();

        System.out.println("==================================================================================================================");
        System.out.println("                                        REWARD AND TIER SUMMARY");
        System.out.println("==================================================================================================================");

        System.out.printf("%-11s", "Tier");

        for (int i = 0; i < rewards.length; i++) {
            System.out.printf(" %-9s", getRewardShortName(i));
        }

        System.out.printf(" %-7s%n", "Total");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (String tier : tiers) {
            int tierTotal = 0;

            System.out.printf("%-11s", tier);

            for (String reward : rewards) {
                int count = loyaltyControl.countTodayRedemptions(tier, reward);
                tierTotal += count;
                System.out.printf(" %-9d", count);
            }

            System.out.printf(" %-7d%n", tierTotal);
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-11s", "Total");

        int overallTotal = 0;

        for (String reward : rewards) {
            int rewardTotal = loyaltyControl.countTodayRedemptionsByReward(reward);
            overallTotal += rewardTotal;
            System.out.printf(" %-9d", rewardTotal);
        }

        System.out.printf(" %-7d%n", overallTotal);
        System.out.println("==================================================================================================================");
        System.out.println("Reward Column Reference");
        System.out.println("WD = Welcome Drink          FV = RM10 Food Voucher");
        System.out.println("FM = Free One Meal          LC = Late Check-Out");
        System.out.println("RU = Room Upgrade           SV = RM50 Spa Voucher");
        System.out.println("LA = Lounge Access          FS = Free One-Night Stay");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Redemptions : " + overallTotal);
        System.out.println("Total Points Used : " + loyaltyControl.calculateTodayTotalPointsUsed());
        System.out.println("==================================================================================================================");
    }

    // ==================================================
    // 6. Top Loyalty Members Report
    // ==================================================
    private void topLoyaltyMembersReport() {
        InputUtility.clearScreen();

        System.out.println("==================================================");
        System.out.println("          TOP LOYALTY MEMBERS REPORT");
        System.out.println("==================================================");

        String tier = selectTier();
        if (tier == null) return;

        DoublyLinkedList.ArrayList<LoyaltyRecord> result =
                loyaltyControl.getTopFiveGuestsByTier(tier);

        InputUtility.clearScreen();

        System.out.println("======================================================================================");
        System.out.println("                         TOP LOYALTY MEMBERS REPORT");
        System.out.println("======================================================================================");
        System.out.println("Tier Filter: " + tier);
        System.out.println("--------------------------------------------------------------------------------------");

        if (result.isEmpty()) {
            System.out.println("No loyalty members found.");
            System.out.println("======================================================================================");
            InputUtility.pressEnterToContinue();
            return;
        }

        System.out.printf("%-6s %-10s %-23s %-12s %-17s %-17s%n",
                "Rank", "Guest ID", "Guest Name", "Tier",
                "Lifetime Points", "Available Points");

        System.out.println("--------------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            LoyaltyRecord record = result.getEntry(i);

            System.out.printf("%-6d %-10s %-23s %-12s %-17d %-17d%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 23),
                    record.getLoyaltyTier(),
                    record.getLifetimePoints(),
                    record.getAvailablePoints());
        }

        System.out.println("======================================================================================");
        System.out.println("Total Members Displayed: " + result.getNumberOfEntries());

        InputUtility.pressEnterToContinue();
    }

    // ==================================================
    // Select Tier
    // ==================================================
    private String selectTier() {
        System.out.println("\n1. Standard");
        System.out.println("2. Platinum");
        System.out.println("3. Diamond");
        System.out.println("4. Elite");
        System.out.println("5. All");
        System.out.println("0. Back");
        System.out.print("\nSelect Tier: ");

        int choice = InputUtility.getIntInput();

        switch (choice) {
            case 1:
                return "Standard";

            case 2:
                return "Platinum";

            case 3:
                return "Diamond";

            case 4:
                return "Elite";

            case 5:
                return "All";

            default:
                return null;
        }
    }

    // ==================================================
    // Display Loyalty Result
    // ==================================================
    private void displayLoyaltyResult(
            DoublyLinkedList.ArrayList<LoyaltyRecord> result,
            String title
    ) {
        if (result.isEmpty()) {
            System.out.println("\nNo loyalty members found.");
            return;
        }

        if (result.getNumberOfEntries() == 1) {
            System.out.println("\n==================================================");
            System.out.println(result.getEntry(1));
            System.out.println("==================================================");
            return;
        }

        System.out.println("\n====================================================================================================");
        System.out.println("                                      " + title);
        System.out.println("====================================================================================================");
        System.out.printf("%-4s %-10s %-23s %-12s %-17s %-17s %-12s%n",
                "No.", "Guest ID", "Guest Name", "Tier",
                "Available Points", "Lifetime Points", "Expiry Date");
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            LoyaltyRecord record = result.getEntry(i);

            System.out.printf("%-4d %-10s %-23s %-12s %-17d %-17d %-12s%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 23),
                    record.getLoyaltyTier(),
                    record.getAvailablePoints(),
                    record.getLifetimePoints(),
                    record.getExpiryDate());
        }

        System.out.println("====================================================================================================");
        System.out.println("Total Members: " + result.getNumberOfEntries());
    }

    // ==================================================
    // Display Expiring Points Table
    // ==================================================
    private void displayExpiringPointsTable(
            DoublyLinkedList.ArrayList<LoyaltyRecord> result
    ) {
        System.out.println("\n================================================================================================");
        System.out.println("                                  EXPIRING POINTS MEMBER LIST");
        System.out.println("================================================================================================");
        System.out.printf("%-4s %-10s %-22s %-12s %-17s %-12s %-15s%n",
                "No.", "Guest ID", "Guest Name", "Tier",
                "Available Points", "Expiry Date", "Days Remaining");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            LoyaltyRecord record = result.getEntry(i);

            System.out.printf("%-4d %-10s %-22s %-12s %-17d %-12s %-15d%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 22),
                    record.getLoyaltyTier(),
                    record.getAvailablePoints(),
                    record.getExpiryDate(),
                    loyaltyControl.getDaysRemaining(record));
        }

        System.out.println("================================================================================================");
        System.out.println("Total Members: " + result.getNumberOfEntries());
    }

    // ==================================================
    // Display Expired Points Table
    // ==================================================
    private void displayExpiredPointsTable(
            DoublyLinkedList.ArrayList<LoyaltyRecord> result
    ) {
        System.out.println("\n========================================================================================");
        System.out.println("                              EXPIRED POINTS MEMBER LIST");
        System.out.println("========================================================================================");
        System.out.printf("%-4s %-10s %-23s %-12s %-17s %-12s%n",
                "No.", "Guest ID", "Guest Name", "Tier",
                "Expired Points", "Expiry Date");
        System.out.println("----------------------------------------------------------------------------------------");

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            LoyaltyRecord record = result.getEntry(i);

            System.out.printf("%-4d %-10s %-23s %-12s %-17d %-12s%n",
                    i,
                    record.getGuestID(),
                    limitText(record.getGuestName(), 23),
                    record.getLoyaltyTier(),
                    record.getAvailablePoints(),
                    record.getExpiryDate());
        }

        System.out.println("========================================================================================");
        System.out.println("Total Expired Members: " + result.getNumberOfEntries());
    }

    // ==================================================
    // Count Tier Inside Selected Report Result
    // ==================================================
    private int countTierInResult(
            DoublyLinkedList.ArrayList<RedemptionRecord> result,
            String tier
    ) {
        int count = 0;

        for (int i = 1; i <= result.getNumberOfEntries(); i++) {
            if (result.getEntry(i).getLoyaltyTier().equalsIgnoreCase(tier)) count++;
        }

        return count;
    }

    // ==================================================
    // Reward Short Name
    // ==================================================
    private String getRewardShortName(int index) {
        switch (index) {
            case 0:
                return "WD";

            case 1:
                return "FV";

            case 2:
                return "FM";

            case 3:
                return "LC";

            case 4:
                return "RU";

            case 5:
                return "SV";

            case 6:
                return "LA";

            case 7:
                return "FS";

            default:
                return "";
        }
    }

    // ==================================================
    // Limit Long Text For Table
    // ==================================================
    private String limitText(String text, int length) {
        if (text == null) return "";

        if (text.length() <= length) return text;

        return text.substring(0, length - 3) + "...";
    }
}