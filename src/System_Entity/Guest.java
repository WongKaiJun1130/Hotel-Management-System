/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package System_Entity;

/**
 *
 * @author user
 */

public class Guest {

    private String guestID;
    private String guestName;
    private String loyaltyTier;
    private String roomType;
    private String roomStatus;
    private String checkInDate;

    //Constructor
    public Guest(String guestID, String guestName,
                 String loyaltyTier, String roomType,
                 String roomStatus, String checkInDate) {

        
        this.guestID = guestID;
        this.guestName = guestName;
        this.loyaltyTier = loyaltyTier;
        this.roomType = roomType;
        this.roomStatus = roomStatus;
        this.checkInDate = checkInDate;
    }

    // =========================
    // Getter Methods
    // =========================

    public String getGuestID() {
        return guestID;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    // =========================
    // Setter Methods
    // =========================

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    // =========================
    // Priority
    // =========================

    public int getPriority() {

        switch (loyaltyTier.toLowerCase()) {

            case "elite":
                return 4;

            case "diamond":
                return 3;

            case "platinum":
                return 2;

            default:
                return 1;   // Standard
        }
    }

    @Override
    public String toString() {

        return "Guest ID      : " + guestID +
               "\nGuest Name    : " + guestName +
               "\nLoyalty Tier  : " + loyaltyTier +
               "\nPriority      : " + getPriority() +
               "\nRoom Type     : " + roomType +
               "\nRoom Status   : " + roomStatus +
               "\nCheck-In Date : " + checkInDate;
    }
}