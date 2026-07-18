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


    public Guest(String guestID, String guestName, String loyaltyTier) {
        this.guestID = guestID;
        this.guestName = guestName;
        this.loyaltyTier = loyaltyTier;
    }


    public String getGuestID() {
        return guestID;
    }


    public String getGuestName() {
        return guestName;
    }


    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
     public void setLoyaltyTier(String newTier){

        this.loyaltyTier = newTier;

    }



    // Priority calculation
    public int getPriority() {
        if(loyaltyTier.equalsIgnoreCase("Elite")) {
            return 4;
        }
        else if(loyaltyTier.equalsIgnoreCase("Diamond")) {
            return 3;
        }
        else if(loyaltyTier.equalsIgnoreCase("Platinum")) {
            return 2;
        }
        else {
            return 1;
        }

    }


    public String toString() {
        return "Guest ID: " + guestID +
               "\nGuest Name: " + guestName +
               "\nLoyalty Tier: " + loyaltyTier;
    }

}
