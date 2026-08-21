
package entity;

/**
 *
 * @author Wong Kai Jun
 */

import java.io.Serializable;
import java.util.Objects;

public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String guestID;
    private String guestName;
    private String phoneNumber;
    private String loyaltyTier;
    private String roomType;
    private String roomStatus;
    private String checkInDate;
    private String arrivalDateTime;

    //==========================================================
    // Constructor
    //==========================================================
    public Guest(String guestID, String guestName, String phoneNumber, String loyaltyTier, String roomType, String roomStatus, String checkInDate, String arrivalDateTime) {

        this.guestID = guestID;
        this.guestName = guestName;
        this.phoneNumber = phoneNumber;
        this.loyaltyTier = loyaltyTier;
        this.roomType = roomType;
        this.roomStatus = roomStatus;
        this.checkInDate = checkInDate;
        this.arrivalDateTime = arrivalDateTime;
    }

    //==========================================================
    // Getter Methods
    //==========================================================
    public String getGuestID() {
        return guestID;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    //==========================================================
    // Setter Methods
    //==========================================================
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    public void setArrivalDateTime(String arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    //==========================================================
    // Get Loyalty Tier Priority
    //==========================================================
    public int getPriority() {

        if (loyaltyTier == null) {
            return 1;
        }

        switch (loyaltyTier.toLowerCase()) {

            case "elite":
                return 4;

            case "diamond":
                return 3;

            case "platinum":
                return 2;

            case "standard":
                return 1;

            default:
                return 1;
        }
    }

    //==========================================================
    // Equals
    //==========================================================
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Guest)) {
            return false;
        }

        Guest guest = (Guest) obj;

        return Objects.equals(guestID, guest.guestID);
    }

    //==========================================================
    // Hash Code
    //==========================================================
    @Override
    public int hashCode() {
        return Objects.hash(guestID);
    }

    //==========================================================
    // To String
    //==========================================================
    @Override
    public String toString() {

        return "Guest {" +
                "guestID='" + guestID + '\'' +
                ", guestName='" + guestName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", loyaltyTier='" + loyaltyTier + '\'' +
                ", priority=" + getPriority() +
                ", roomType='" + roomType + '\'' +
                ", roomStatus='" + roomStatus + '\'' +
                ", checkInDate='" + checkInDate + '\'' +
                ", arrivalDateTime='" + arrivalDateTime + '\'' +
                '}';
    }
}