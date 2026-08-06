package System_Entity;

import java.io.Serializable;
import java.time.LocalDate;

/*
 * Author: CHUNWAI
 */
public class LoyaltyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Guest guest;
    private int availablePoints;
    private int lifetimePoints;
    private LocalDate expiryDate;

    public LoyaltyRecord(
            Guest guest,
            int availablePoints,
            int lifetimePoints,
            LocalDate expiryDate
    ) {
        this.guest = guest;
        this.availablePoints = availablePoints;
        this.lifetimePoints = lifetimePoints;
        this.expiryDate = expiryDate;
    }

    public Guest getGuest() {
        return guest;
    }

    public String getGuestID() {
        return guest.getGuestID();
    }

    public String getGuestName() {
        return guest.getGuestName();
    }

    public String getLoyaltyTier() {
        return guest.getLoyaltyTier();
    }

    public int getAvailablePoints() {
        return availablePoints;
    }

    public int getLifetimePoints() {
        return lifetimePoints;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setAvailablePoints(int availablePoints) {
        this.availablePoints = availablePoints;
    }

    public void setLifetimePoints(int lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        guest.setLoyaltyTier(loyaltyTier);
    }

    @Override
    public String toString() {
        return "Guest ID: " + getGuestID()
                + "\nGuest Name       : " + getGuestName()
                + "\nLoyalty Tier     : " + getLoyaltyTier()
                + "\nAvailable Points : " + availablePoints
                + "\nLifetime Points  : " + lifetimePoints
                + "\nExpiry Date      : " + expiryDate;
    }
}