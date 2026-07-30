/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Entity;

import java.io.Serializable;
import java.time.LocalDate;

public class RedemptionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Guest guest;
    private String rewardName;
    private LocalDate redemptionDate;

    public RedemptionRecord(
            Guest guest,
            String rewardName,
            LocalDate redemptionDate
    ) {
        this.guest = guest;
        this.rewardName = rewardName;
        this.redemptionDate = redemptionDate;
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

    public String getRewardName() {
        return rewardName;
    }

    public LocalDate getRedemptionDate() {
        return redemptionDate;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public void setRedemptionDate(LocalDate redemptionDate) {
        this.redemptionDate = redemptionDate;
    }

    @Override
    public String toString() {
        return "Guest ID: " + getGuestID()
                + "\nGuest Name: " + getGuestName()
                + "\nLoyalty Tier: " + getLoyaltyTier()
                + "\nReward Name: " + rewardName
                + "\nRedemption Date: " + redemptionDate;
    }
}
