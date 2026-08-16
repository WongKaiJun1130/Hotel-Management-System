package boundary;

import control.LoyaltyControl;
import entity.LoyaltyRecord;
import entity.RedemptionRecord;
import utility.InputUtility;
import adt.DoublyLinkedList;
import java.time.LocalDate;

public class LoyaltyAndRewardsUI {

    private LoyaltyControl loyaltyControl;

    //====================================================
    // Constructor
    //====================================================
    public LoyaltyAndRewardsUI() {
        loyaltyControl = new LoyaltyControl();
    }

    public LoyaltyAndRewardsUI(LoyaltyControl loyaltyControl) {
        this.loyaltyControl = loyaltyControl;
    }

    //====================================================
    // Loyalty And Rewards Menu
    //====================================================
    public void loyaltyMenu() {

        int choice;

        InputUtility.clearScreen();

        do {
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
            System.out.print("Please enter your choice: ");

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
                    InputUtility.clearScreen();
                    System.out.println("\nInvalid choice. Try again!");
                    break;
            }

        } while (choice != 0);
    }

    //====================================================
    // 1. Display Loyalty Members
    //====================================================
    private void displayLoyaltyMembers() {

        InputUtility.clearScreen();

        System.out.println("+------------------------------------------------+");
        System.out.println("|            DISPLAY LOYALTY MEMBERS             |");
        System.out.println("+------------------------------------------------+");
        System.out.println("| 1. Standard                                    |");
        System.out.println("| 2. Platinum                                    |");
        System.out.println("| 3. Diamond                                     |");
        System.out.println("| 4. Elite                                       |");
        System.out.println("| 5. All                                         |");
        System.out.println("| 0. Back                                        |");
        System.out.println("+------------------------------------------------+");
        System.out.print("Please Enter your choice : ");

        String tier = selectTier();

        if (tier == null) {
            return;
        }

        DoublyLinkedList<LoyaltyRecord> result
                = loyaltyControl.getGuestsByTier(tier);

        InputUtility.clearScreen();

        System.out.printf("Selected Tier: %s%n", tier);

        displayLoyaltyResult(
                result,
                "LOYALTY MEMBER LIST"
        );

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // Select Tier
    //====================================================
    private String selectTier() {

        while (true) {

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

                case 0:
                    return null;

                default:
                    System.out.println(
                            "\nInvalid choice. Please try again."
                    );

                    System.out.print(
                            "Please Enter your choice (0-5) : "
                    );

                    break;
            }
        }
    }

    //====================================================
    // Display Loyalty Result
    //====================================================
    private void displayLoyaltyResult(
            DoublyLinkedList<LoyaltyRecord> result,
            String title
    ) {

        if (result == null || result.isEmpty()) {
            System.out.println("\nNo loyalty members found.");
            return;
        }

        String fullBorder
                = "+-------------------------------------------------------------------------------------------------------------------+";

        String columnBorder
                = "+------+------------+-------------------------+--------------+-------------------+-------------------+--------------+";

        int insideWidth = fullBorder.length() - 2;

        System.out.println();
        System.out.println(fullBorder);

        String safeTitle = limitText(
                title,
                insideWidth
        );

        int leftSpace
                = (insideWidth - safeTitle.length()) / 2;

        int rightSpace
                = insideWidth
                - safeTitle.length()
                - leftSpace;

        System.out.println(
                "|"
                + " ".repeat(leftSpace)
                + safeTitle
                + " ".repeat(rightSpace)
                + "|"
        );

        System.out.println(columnBorder);

        System.out.printf(
                "| %-4s | %-10s | %-23s | %-12s | %-17s | %-17s | %-12s |%n",
                "No.",
                "Guest ID",
                "Guest Name",
                "Tier",
                "Available Points",
                "Lifetime Points",
                "Expiry Date"
        );

        System.out.println(columnBorder);

        for (int i = 1; i <= result.getSize(); i++) {

            LoyaltyRecord record = result.getEntry(i);

            if (record == null) {
                continue;
            }

            System.out.printf(
                    "| %-4d | %-10s | %-23s | %-12s | %-17d | %-17d | %-12s |%n",
                    i,
                    limitText(
                            String.valueOf(record.getGuestID()),
                            10
                    ),
                    limitText(
                            String.valueOf(record.getGuestName()),
                            23
                    ),
                    limitText(
                            String.valueOf(record.getLoyaltyTier()),
                            12
                    ),
                    record.getAvailablePoints(),
                    record.getLifetimePoints(),
                    limitText(
                            String.valueOf(record.getExpiryDate()),
                            12
                    )
            );
        }

        System.out.println(columnBorder);

        String totalText
                = "Total Members: "
                + result.getSize();

        System.out.printf(
                "| %-"
                + (insideWidth - 2)
                + "s |%n",
                totalText
        );

        System.out.println(fullBorder);
    }

    //====================================================
    // 2. Search Loyalty Member
    //====================================================
    private void searchLoyaltyMember() {

        InputUtility.clearScreen();

        String fullBorder
                = "+------------------------------------------------+";

        System.out.println(fullBorder);
        System.out.println("|             SEARCH LOYALTY MEMBER              |");
        System.out.println(fullBorder);
        System.out.print("Please enter Guest ID (RXXXX) : ");

        String guestID = InputUtility.getStringInput();

        LoyaltyRecord record
                = loyaltyControl.searchGuest(guestID);

        if (record == null) {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("| Loyalty member not found.                      |");
            System.out.println(fullBorder);

        } else {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("|            LOYALTY MEMBER DETAILS              |");
            System.out.println(fullBorder);

            System.out.printf(
                    "| %-18s : %-25s |%n",
                    "Guest ID",
                    record.getGuestID()
            );

            System.out.printf(
                    "| %-18s : %-25s |%n",
                    "Guest Name",
                    limitText(
                            record.getGuestName(),
                            25
                    )
            );

            System.out.printf(
                    "| %-18s : %-25s |%n",
                    "Loyalty Tier",
                    record.getLoyaltyTier()
            );

            System.out.printf(
                    "| %-18s : %-25d |%n",
                    "Available Points",
                    record.getAvailablePoints()
            );

            System.out.printf(
                    "| %-18s : %-25d |%n",
                    "Lifetime Points",
                    record.getLifetimePoints()
            );

            System.out.printf(
                    "| %-18s : %-25s |%n",
                    "Expiry Date",
                    record.getExpiryDate()
            );

            System.out.println(fullBorder);
        }

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // 3. Redeem Reward
    //====================================================
    private void redeemReward() {

        InputUtility.clearScreen();

        String fullBorder
                = "+------------------------------------------------+";

        String detailBorder
                = "+--------------------+---------------------------+";

        String rewardBorder
                = "+------+----------------------------------+------------+";

        System.out.println(fullBorder);
        System.out.println("|                 REDEEM REWARD                  |");
        System.out.println(fullBorder);
        System.out.print("Please Enter Guest ID (RXXXX): ");

        String guestID = InputUtility.getStringInput();

        LoyaltyRecord record
                = loyaltyControl.searchGuest(guestID);

        if (record == null) {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("| Loyalty member not found.                      |");
            System.out.println(fullBorder);

            InputUtility.pressEnterToContinue();
            InputUtility.clearScreen();

            return;
        }

        System.out.println();
        System.out.println(fullBorder);
        System.out.println("|            LOYALTY MEMBER DETAILS              |");
        System.out.println(detailBorder);

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Guest ID",
                record.getGuestID()
        );

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Guest Name",
                limitText(
                        record.getGuestName(),
                        25
                )
        );

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Loyalty Tier",
                record.getLoyaltyTier()
        );

        System.out.printf(
                "| %-18s : %-25d |%n",
                "Available Points",
                record.getAvailablePoints()
        );

        System.out.printf(
                "| %-18s : %-25d |%n",
                "Lifetime Points",
                record.getLifetimePoints()
        );

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Expiry Date",
                record.getExpiryDate()
        );

        System.out.println(detailBorder);

        String[] rewards
                = loyaltyControl.getRewardsByTier(
                        record.getLoyaltyTier()
                );

        if (rewards.length == 0) {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("| No rewards available.                          |");
            System.out.println(fullBorder);

            InputUtility.pressEnterToContinue();
            InputUtility.clearScreen();

            return;
        }

        System.out.println();
        System.out.println(rewardBorder);
        System.out.println("|                  AVAILABLE REWARDS                   |");
        System.out.println(rewardBorder);

        System.out.printf(
                "| %-4s | %-32s | %-10s |%n",
                "No.",
                "Reward",
                "Points"
        );

        System.out.println(rewardBorder);

        for (int i = 0; i < rewards.length; i++) {

            int points
                    = loyaltyControl.getRewardPoints(
                            rewards[i]
                    );

            System.out.printf(
                    "| %-4d | %-32s | %-10s |%n",
                    i + 1,
                    limitText(
                            rewards[i],
                            32
                    ),
                    String.format(
                            "%,d",
                            points
                    )
            );
        }

        System.out.printf(
                "| %-4d | %-32s | %-10s |%n",
                0,
                "Cancel",
                "-"
        );

        System.out.println(rewardBorder);
        System.out.print("Select Reward: ");

        int rewardChoice
                = InputUtility.getIntInput();

        while (rewardChoice < 0
                || rewardChoice > rewards.length) {

            System.out.println(
                    "Invalid reward choice. Please enter again."
            );

            System.out.print("Select Reward: ");

            rewardChoice
                    = InputUtility.getIntInput();
        }

        if (rewardChoice == 0) {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("| Redemption cancelled.                          |");
            System.out.println(fullBorder);

            InputUtility.pressEnterToContinue();
            InputUtility.clearScreen();

            return;
        }

        String selectedReward
                = rewards[rewardChoice - 1];

        int requiredPoints
                = loyaltyControl.getRewardPoints(
                        selectedReward
                );

        System.out.println();
        System.out.println(fullBorder);
        System.out.println("|           REDEMPTION CONFIRMATION              |");
        System.out.println(detailBorder);

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Guest Name",
                limitText(
                        record.getGuestName(),
                        25
                )
        );

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Loyalty Tier",
                record.getLoyaltyTier()
        );

        System.out.printf(
                "| %-18s : %-25s |%n",
                "Selected Reward",
                limitText(
                        selectedReward,
                        25
                )
        );

        System.out.printf(
                "| %-18s : %-25d |%n",
                "Required Points",
                requiredPoints
        );

        System.out.printf(
                "| %-18s : %-25d |%n",
                "Available Points",
                record.getAvailablePoints()
        );

        System.out.println(detailBorder);
        System.out.println("| 1. Confirm Redemption                          |");
        System.out.println("| 2. Cancel                                      |");
        System.out.println(fullBorder);
        System.out.print("Enter Choice: ");

        int confirm
                = InputUtility.getIntInput();

        while (confirm != 1 && confirm != 2) {

            System.out.println(
                    "Invalid choice. Please enter 1 or 2."
            );

            System.out.print("Enter Choice: ");

            confirm
                    = InputUtility.getIntInput();
        }

        if (confirm == 1) {

            loyaltyControl.redeemReward(
                    guestID,
                    selectedReward
            );

            System.out.println();
            System.out.println(fullBorder);

            String[] messageLines
                    = loyaltyControl
                            .getLastMessage()
                            .split("\\R");

            for (String line : messageLines) {

                System.out.printf(
                        "| %-46s |%n",
                        limitText(
                                line,
                                46
                        )
                );
            }

            System.out.println(fullBorder);

        } else {

            System.out.println();
            System.out.println(fullBorder);
            System.out.println("| Redemption cancelled.                          |");
            System.out.println(fullBorder);
        }

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // 4. Points Expiry Management
    //====================================================
    private void pointsExpiryManagement() {

        int choice;

        InputUtility.clearScreen();

        do {
            String border
                    = "+------------------------------------------------+";

            System.out.println(border);
            System.out.println("|          POINTS EXPIRY MANAGEMENT              |");
            System.out.println(border);
            System.out.println("| 1. View Points Expiring Within 30 Days         |");
            System.out.println("| 2. Process Expired Points                      |");
            System.out.println("| 0. Back to Loyalty Main Menu                   |");
            System.out.println(border);
            System.out.print("Please enter your choice: ");

            choice = InputUtility.getIntInput();

            switch (choice) {

                case 1:
                    viewPointsExpiringWithin30Days();
                    break;

                case 2:
                    processExpiredPoints();
                    break;

                case 0:
                    InputUtility.clearScreen();
                    break;

                default:
                    System.out.println(
                            "\nInvalid choice. Please try again."
                    );
                    break;
            }

        } while (choice != 0);
    }

    //====================================================
    // View Points Expiring Within 30 Days
    //====================================================
    private void viewPointsExpiringWithin30Days() {

        InputUtility.clearScreen();

        String border
                = "+------------------------------------------------+";

        String detailBorder
                = "+--------------------+---------------------------+";

        System.out.println(border);
        System.out.println("|       POINTS EXPIRING WITHIN 30 DAYS           |");
        System.out.println(border);

        DoublyLinkedList<LoyaltyRecord> result
                = loyaltyControl
                        .getPointsExpiringWithin30Days();

        if (result == null || result.isEmpty()) {

            System.out.println(
                    "| No points will expire within the next 30 days. |"
            );

            System.out.println(border);

        } else if (result.getSize() == 1) {

            LoyaltyRecord record
                    = result.getEntry(1);

            System.out.println();
            System.out.println(border);
            System.out.println("|              MEMBER DETAILS                    |");
            System.out.println(detailBorder);

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Guest ID",
                    record.getGuestID()
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Guest Name",
                    limitText(
                            record.getGuestName(),
                            25
                    )
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Loyalty Tier",
                    record.getLoyaltyTier()
            );

            System.out.printf(
                    "| %-18s | %-25d |%n",
                    "Available Points",
                    record.getAvailablePoints()
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Expiry Date",
                    record.getExpiryDate()
            );

            System.out.printf(
                    "| %-18s | %-25d |%n",
                    "Days Remaining",
                    loyaltyControl.getDaysRemaining(
                            record
                    )
            );

            System.out.println(detailBorder);

        } else {

            displayExpiringPointsTable(result);
        }

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // Process Expired Points
    //====================================================
    private void processExpiredPoints() {

        InputUtility.clearScreen();

        String border
                = "+------------------------------------------------+";

        String detailBorder
                = "+--------------------+---------------------------+";

        System.out.println(border);
        System.out.println("|             PROCESS EXPIRED POINTS             |");
        System.out.println(border);

        DoublyLinkedList<LoyaltyRecord> expiredList
                = loyaltyControl.getExpiredPointsGuests();

        if (expiredList == null
                || expiredList.isEmpty()) {

            System.out.println(
                    "| No expired points found.                       |"
            );

            System.out.println(border);

            InputUtility.pressEnterToContinue();
            InputUtility.clearScreen();

            return;
        }

        if (expiredList.getSize() == 1) {

            LoyaltyRecord record
                    = expiredList.getEntry(1);

            System.out.println();
            System.out.println(border);
            System.out.println("|          EXPIRED MEMBER DETAILS                |");
            System.out.println(detailBorder);

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Guest ID",
                    record.getGuestID()
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Guest Name",
                    limitText(
                            record.getGuestName(),
                            25
                    )
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Loyalty Tier",
                    record.getLoyaltyTier()
            );

            System.out.printf(
                    "| %-18s | %-25d |%n",
                    "Expired Points",
                    record.getAvailablePoints()
            );

            System.out.printf(
                    "| %-18s | %-25s |%n",
                    "Expiry Date",
                    record.getExpiryDate()
            );

            System.out.println(detailBorder);

        } else {

            displayExpiredPointsTable(expiredList);
        }

        System.out.println();
        System.out.println(border);

        System.out.printf(
                "| %-46s |%n",
                expiredList.getSize()
                + " guest(s) have expired available points."
        );

        System.out.println(border);
        System.out.println("| 1. Clear Expired Points                        |");
        System.out.println("| 2. Cancel                                      |");
        System.out.println(border);
        System.out.print("Enter Choice: ");

        int confirm
                = InputUtility.getIntInput();

        if (confirm == 1) {

            loyaltyControl.processExpiredPoints();

            System.out.println();
            System.out.println(border);

            String[] messageLines
                    = loyaltyControl
                            .getLastMessage()
                            .split("\\R");

            for (String line : messageLines) {

                if (line.contains(":")) {

                    String[] parts
                            = line.split(
                                    ":",
                                    2
                            );

                    String label
                            = parts[0].trim();

                    String value
                            = parts[1].trim();

                    System.out.printf(
                            "| %-18s : %-25s |%n",
                            limitText(
                                    label,
                                    18
                            ),
                            limitText(
                                    value,
                                    25
                            )
                    );

                } else {

                    System.out.printf(
                            "| %-46s |%n",
                            limitText(
                                    line,
                                    46
                            )
                    );
                }
            }

            System.out.println(border);

        } else {

            System.out.println();
            System.out.println(border);
            System.out.println("| Expired points processing cancelled.           |");
            System.out.println(border);
        }

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // Display Expiring Points Table
    //====================================================
    private void displayExpiringPointsTable(
            DoublyLinkedList<LoyaltyRecord> result
    ) {

        String fullBorder
                = "+----------------------------------------------------------------------------------------------------------------+";

        String columnBorder
                = "+------+------------+------------------------+--------------+-------------------+--------------+-----------------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println();
        System.out.println(fullBorder);

        String title
                = "EXPIRING POINTS MEMBER LIST";

        int leftSpace
                = (insideWidth - title.length()) / 2;

        int rightSpace
                = insideWidth
                - title.length()
                - leftSpace;

        System.out.println(
                "|"
                + " ".repeat(leftSpace)
                + title
                + " ".repeat(rightSpace)
                + "|"
        );

        System.out.println(columnBorder);

        System.out.printf(
                "| %-4s | %-10s | %-22s | %-12s | %-17s | %-12s | %-15s |%n",
                "No.",
                "Guest ID",
                "Guest Name",
                "Tier",
                "Available Points",
                "Expiry Date",
                "Days Remaining"
        );

        System.out.println(columnBorder);

        for (int i = 1; i <= result.getSize(); i++) {

            LoyaltyRecord record
                    = result.getEntry(i);

            if (record == null) {
                continue;
            }

            System.out.printf(
                    "| %-4d | %-10s | %-22s | %-12s | %-17d | %-12s | %-15d |%n",
                    i,
                    limitText(
                            String.valueOf(record.getGuestID()),
                            10
                    ),
                    limitText(
                            record.getGuestName(),
                            22
                    ),
                    limitText(
                            record.getLoyaltyTier(),
                            12
                    ),
                    record.getAvailablePoints(),
                    limitText(
                            String.valueOf(record.getExpiryDate()),
                            12
                    ),
                    loyaltyControl.getDaysRemaining(
                            record
                    )
            );
        }

        System.out.println(columnBorder);

        System.out.printf(
                "| %-"
                + (insideWidth - 2)
                + "s |%n",
                "Total Members: "
                + result.getSize()
        );

        System.out.println(fullBorder);
    }

    //====================================================
    // Display Expired Points Table
    //====================================================
    private void displayExpiredPointsTable(
            DoublyLinkedList<LoyaltyRecord> result
    ) {

        String fullBorder
                = "+----------------------------------------------------------------------------------------------+";

        String columnBorder
                = "+------+------------+-------------------------+--------------+-------------------+--------------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println();
        System.out.println(fullBorder);

        String title
                = "EXPIRED POINTS MEMBER LIST";

        int leftSpace
                = (insideWidth - title.length()) / 2;

        int rightSpace
                = insideWidth
                - title.length()
                - leftSpace;

        System.out.println(
                "|"
                + " ".repeat(leftSpace)
                + title
                + " ".repeat(rightSpace)
                + "|"
        );

        System.out.println(columnBorder);

        System.out.printf(
                "| %-4s | %-10s | %-23s | %-12s | %-17s | %-12s |%n",
                "No.",
                "Guest ID",
                "Guest Name",
                "Tier",
                "Expired Points",
                "Expiry Date"
        );

        System.out.println(columnBorder);

        for (int i = 1; i <= result.getSize(); i++) {

            LoyaltyRecord record
                    = result.getEntry(i);

            if (record == null) {
                continue;
            }

            System.out.printf(
                    "| %-4d | %-10s | %-23s | %-12s | %-17d | %-12s |%n",
                    i,
                    limitText(
                            String.valueOf(record.getGuestID()),
                            10
                    ),
                    limitText(
                            record.getGuestName(),
                            23
                    ),
                    limitText(
                            record.getLoyaltyTier(),
                            12
                    ),
                    record.getAvailablePoints(),
                    limitText(
                            String.valueOf(record.getExpiryDate()),
                            12
                    )
            );
        }

        System.out.println(columnBorder);

        System.out.printf(
                "| %-"
                + (insideWidth - 2)
                + "s |%n",
                "Total Expired Members: "
                + result.getSize()
        );

        System.out.println(fullBorder);
    }

    //====================================================
    // 5. Daily Reward Redemption Report
    //====================================================
    private void dailyRewardRedemptionReport() {

        InputUtility.clearScreen();

        String border
                = "+---------------------------------------------+";

        System.out.println(border);
        System.out.println("|       DAILY REWARD REDEMPTION REPORT        |");
        System.out.println(border);

        String[] rewards
                = loyaltyControl.getAllRewardNames();

        for (int i = 0; i < rewards.length; i++) {

            System.out.printf(
                    "| %d. %-40s |%n",
                    i + 1,
                    limitText(
                            rewards[i],
                            40
                    )
            );
        }

        System.out.printf(
                "| %d. %-40s |%n",
                rewards.length + 1,
                "All Rewards"
        );

        System.out.println("| 0. Back                                     |");
        System.out.println(border);
        System.out.print("Select Reward: ");

        int choice
                = InputUtility.getIntInput();

        if (choice == 0) {
            return;
        }

        if (choice < 1
                || choice > rewards.length + 1) {

            System.out.println();
            System.out.println(border);
            System.out.println("| Invalid reward choice.                      |");
            System.out.println(border);

            InputUtility.pressEnterToContinue();

            return;
        }

        if (choice == rewards.length + 1) {

            displayAllRewardsReport();

        } else {

            displaySingleRewardReport(
                    rewards[choice - 1]
            );
        }

        InputUtility.pressEnterToContinue();
    }

    //====================================================
    // Single Reward Report
    //====================================================
    private void displaySingleRewardReport(
            String rewardName
    ) {

        DoublyLinkedList<RedemptionRecord> result
                = loyaltyControl.getTodayRedemptions(
                        rewardName
                );

        int pointsEach
                = loyaltyControl.getRewardPoints(
                        rewardName
                );

        InputUtility.clearScreen();

        String fullBorder
                = "+---------------------------------------------------------+";

        String detailBorder
                = "+----------------------+----------------------------------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println(fullBorder);

        printCenteredText(
                "DAILY REWARD REDEMPTION REPORT",
                insideWidth
        );

        System.out.println(fullBorder);

        System.out.printf(
                "| %-20s : %-32s |%n",
                "Date",
                LocalDate.now()
        );

        System.out.printf(
                "| %-20s : %-32s |%n",
                "Reward",
                limitText(
                        rewardName,
                        32
                )
        );

        System.out.printf(
                "| %-20s : %-32s |%n",
                "Points Each",
                String.format(
                        "%,d",
                        pointsEach
                )
        );

        if (result == null || result.isEmpty()) {

            System.out.println(fullBorder);

            System.out.printf(
                    "| %-55s |%n",
                    "No redemption records found."
            );

            System.out.println(fullBorder);

            System.out.printf(
                    "| %-20s : %-32s |%n",
                    "Total Redemptions",
                    "0"
            );

            System.out.printf(
                    "| %-20s : %-32s |%n",
                    "Total Points Used",
                    "0"
            );

            System.out.println(fullBorder);

            return;
        }

        int standard
                = countTierInResult(
                        result,
                        "Standard"
                );

        int platinum
                = countTierInResult(
                        result,
                        "Platinum"
                );

        int diamond
                = countTierInResult(
                        result,
                        "Diamond"
                );

        int elite
                = countTierInResult(
                        result,
                        "Elite"
                );

        int total
                = result.getSize();

        int totalPoints
                = loyaltyControl
                        .calculateTodayPointsUsedByReward(
                                rewardName
                        );

        System.out.println(fullBorder);

        printCenteredText(
                "TIER SUMMARY",
                insideWidth
        );

        System.out.println(detailBorder);

        System.out.printf(
                "| %-20s | %-32d |%n",
                "Standard",
                standard
        );

        System.out.printf(
                "| %-20s | %-32d |%n",
                "Platinum",
                platinum
        );

        System.out.printf(
                "| %-20s | %-32d |%n",
                "Diamond",
                diamond
        );

        System.out.printf(
                "| %-20s | %-32d |%n",
                "Elite",
                elite
        );

        System.out.println(detailBorder);

        System.out.printf(
                "| %-20s | %-32d |%n",
                "Total Redemptions",
                total
        );

        System.out.printf(
                "| %-20s | %-32s |%n",
                "Total Points Used",
                String.format(
                        "%,d",
                        totalPoints
                )
        );

        System.out.println(detailBorder);
    }

    //====================================================
    // All Rewards Report
    //====================================================
    private void displayAllRewardsReport() {

        DoublyLinkedList<RedemptionRecord> result
                = loyaltyControl.getTodayRedemptions(
                        LoyaltyControl.ALL_REWARDS
                );

        InputUtility.clearScreen();

        String fullBorder
                = "+-----------------------------------------------------------------------------------------------------------------------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println(fullBorder);

        printCenteredText(
                "DAILY REWARD REDEMPTION REPORT",
                insideWidth
        );

        System.out.println(fullBorder);

        System.out.printf(
                "| %-11s : %-103s |%n",
                "Date",
                LocalDate.now()
        );

        System.out.printf(
                "| %-11s : %-103s |%n",
                "Filter",
                "All Rewards"
        );

        if (result == null || result.isEmpty()) {

            System.out.printf(
                    "| %-117s |%n",
                    "No redemption records found."
            );

            System.out.println(fullBorder);

            return;
        }

        displayRewardTierMatrix();
    }

    //====================================================
    // Reward And Tier Matrix Summary
    //====================================================
    private void displayRewardTierMatrix() {

        String[] tiers = {
            "Standard",
            "Platinum",
            "Diamond",
            "Elite"
        };

        String[] rewards
                = loyaltyControl.getAllRewardNames();

        String fullBorder
                = "+-----------------------------------------------------------------------------------------------------------------------+";

        String columnBorder
                = "+-------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+---------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println(columnBorder);

        System.out.printf(
                "| %-11s",
                "Tier"
        );

        for (int i = 0; i < rewards.length; i++) {

            System.out.printf(
                    " | %-9s",
                    getRewardShortName(i)
            );
        }

        System.out.printf(
                " | %-7s |%n",
                "Total"
        );

        System.out.println(columnBorder);

        for (String tier : tiers) {

            int tierTotal = 0;

            System.out.printf(
                    "| %-11s",
                    tier
            );

            for (String reward : rewards) {

                int count
                        = loyaltyControl
                                .countTodayRedemptions(
                                        tier,
                                        reward
                                );

                tierTotal += count;

                System.out.printf(
                        " | %-9d",
                        count
                );
            }

            System.out.printf(
                    " | %-7d |%n",
                    tierTotal
            );
        }

        System.out.println(columnBorder);

        System.out.printf(
                "| %-11s",
                "Total"
        );

        int overallTotal = 0;

        for (String reward : rewards) {

            int rewardTotal
                    = loyaltyControl
                            .countTodayRedemptionsByReward(
                                    reward
                            );

            overallTotal += rewardTotal;

            System.out.printf(
                    " | %-9d",
                    rewardTotal
            );
        }

        System.out.printf(
                " | %-7d |%n",
                overallTotal
        );

        System.out.println(columnBorder);

        System.out.printf(
                "| %-25s : %-89d |%n",
                "Total Redemptions",
                overallTotal
        );

        System.out.printf(
                "| %-25s : %-89s |%n",
                "Total Points Used",
                String.format(
                        "%,d",
                        loyaltyControl
                                .calculateTodayTotalPointsUsed()
                )
        );

        System.out.println(fullBorder);
        System.out.println();
        System.out.println(fullBorder);

        printCenteredText(
                "REWARD COLUMN REFERENCE",
                insideWidth
        );

        System.out.println(fullBorder);

        System.out.printf("| %-57s | %-57s |%n", "WD = Welcome Drink", "FV = RM10 Food Voucher");
        System.out.printf("| %-57s | %-57s |%n", "FM = Free One Meal", "LC = Late Check-Out");
        System.out.printf("| %-57s | %-57s |%n", "RU = Room Upgrade", "SV = RM50 Spa Voucher");
        System.out.printf("| %-57s | %-57s |%n", "LA = Lounge Access", "FS = Free One-Night Stay");

        System.out.println(fullBorder);
    }

    //====================================================
    // 6. Top Loyalty Members Report
    //====================================================
    private void topLoyaltyMembersReport() {

        InputUtility.clearScreen();

        String menuBorder
                = "+------------------------------------------------+";

        System.out.println(menuBorder);
        System.out.println("|       TOP 5 POINTS USED MEMBERS REPORT         |");
        System.out.println(menuBorder);
        System.out.println("| 1. Standard                                    |");
        System.out.println("| 2. Platinum                                    |");
        System.out.println("| 3. Diamond                                     |");
        System.out.println("| 4. Elite                                       |");
        System.out.println("| 5. All                                         |");
        System.out.println("| 0. Back                                        |");
        System.out.println(menuBorder);
        System.out.print("Select Tier: ");

        String tier = selectTier();

        if (tier == null) {
            return;
        }

        InputUtility.clearScreen();

        DoublyLinkedList<LoyaltyRecord> result
                = loyaltyControl
                        .getTopFiveMembersByPointsUsed(
                                tier
                        );

        String fullBorder
                = "+-------------------------------------------------------------------------------------------------+";

        String columnBorder
                = "+--------+------------+-------------------------+--------------+-------------------+--------------+";

        int insideWidth
                = fullBorder.length() - 2;

        System.out.println(fullBorder);

        printCenteredText(
                "TOP 5 POINTS USED MEMBERS REPORT",
                insideWidth
        );

        System.out.println(fullBorder);

        System.out.printf(
                "| %-14s : %-78s |%n",
                "Tier Filter",
                tier
        );

        System.out.println(columnBorder);

        System.out.printf(
                "| %-6s | %-10s | %-23s | %-12s | %-17s | %-12s |%n",
                "Rank",
                "Guest ID",
                "Guest Name",
                "Tier",
                "Lifetime Points",
                "Points Used"
        );

        System.out.println(columnBorder);

        if (result == null || result.isEmpty()) {

            System.out.printf(
                    "| %-92s |%n",
                    "No loyalty members found."
            );

            System.out.println(fullBorder);

            InputUtility.pressEnterToContinue();

            return;
        }

        int totalPointsUsed = 0;

        for (int i = 1; i <= result.getSize(); i++) {

            LoyaltyRecord record
                    = result.getEntry(i);

            if (record == null) {
                continue;
            }

            int pointsUsed
                    = record.getLifetimePoints()
                    - record.getAvailablePoints();

            totalPointsUsed += pointsUsed;

            System.out.printf(
                    "| %-6d | %-10s | %-23s | %-12s | %-17s | %-12s |%n",
                    i,
                    limitText(
                            String.valueOf(record.getGuestID()),
                            10
                    ),
                    limitText(
                            record.getGuestName(),
                            23
                    ),
                    limitText(
                            record.getLoyaltyTier(),
                            12
                    ),
                    String.format(
                            "%,d",
                            record.getLifetimePoints()
                    ),
                    String.format(
                            "%,d",
                            pointsUsed
                    )
            );
        }

        System.out.println(columnBorder);

        System.out.printf(
                "| %-28s : %-64s |%n",
                "Total Points Used by Top 5",
                String.format(
                        "%,d",
                        totalPointsUsed
                )
        );

        System.out.printf(
                "| %-28s : %-64d |%n",
                "Members Displayed",
                result.getSize()
        );

        System.out.println(fullBorder);

        InputUtility.pressEnterToContinue();
        InputUtility.clearScreen();
    }

    //====================================================
    // Count Tier Inside Selected Report Result
    //====================================================
    private int countTierInResult(
            DoublyLinkedList<RedemptionRecord> result,
            String tier
    ) {

        int count = 0;

        if (result == null || tier == null) {
            return count;
        }

        for (int i = 1; i <= result.getSize(); i++) {

            RedemptionRecord record
                    = result.getEntry(i);

            if (record != null
                    && record.getLoyaltyTier() != null
                    && record.getLoyaltyTier()
                            .equalsIgnoreCase(tier)) {

                count++;
            }
        }

        return count;
    }

    //====================================================
    // Reward Short Name
    //====================================================
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

    //====================================================
    // Limit Long Text For Table
    //====================================================
    private String limitText(
            String text,
            int length
    ) {

        if (text == null) {
            return "";
        }

        if (text.length() <= length) {
            return text;
        }

        if (length <= 3) {
            return text.substring(0, length);
        }

        return text.substring(
                0,
                length - 3
        ) + "...";
    }

    //====================================================
    // Print Centered Text
    //====================================================
    private void printCenteredText(
            String text,
            int insideWidth
    ) {

        String safeText
                = limitText(
                        text,
                        insideWidth
                );

        int leftSpace
                = (insideWidth - safeText.length()) / 2;

        int rightSpace
                = insideWidth
                - safeText.length()
                - leftSpace;

        System.out.println(
                "|"
                + " ".repeat(leftSpace)
                + safeText
                + " ".repeat(rightSpace)
                + "|"
        );
    }
}