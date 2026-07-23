/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

import System_Entity.Guest;
import System_Utility.GuestDatabase;
import System_adt.ArrayList;

public class ImportGuestData {

    public static void main(String[] args) {

        ArrayList<Guest> guests = new ArrayList<>();

        guests.add(new Guest("R0001","John Tan","Elite","Big Room","Waiting","20/07/2026"));
        guests.add(new Guest("R0002","Wong Lee","Diamond","Middle Room","Waiting","21/07/2026"));
        guests.add(new Guest("R0003","Alice Lim","Platinum","Small Room","Waiting","22/07/2026"));
        guests.add(new Guest("R0004","David Wong","Elite","Big Room","Waiting","23/07/2026"));
        guests.add(new Guest("R0005","Jason Lee","Standard","Small Room","Waiting","24/07/2026"));
        guests.add(new Guest("R0006","Sarah Tan","Diamond","Middle Room","Waiting","25/07/2026"));
        guests.add(new Guest("R0007","Michael Chen","Platinum","Middle Room","Waiting","26/07/2026"));
        guests.add(new Guest("R0008","Emily Wong","Elite","Big Room","Waiting","27/07/2026"));
        guests.add(new Guest("R0009","Kevin Lim","Standard","Small Room","Waiting","28/07/2026"));
        guests.add(new Guest("R0010","Jessica Ng","Diamond","Middle Room","Waiting","29/07/2026"));
        GuestDatabase.saveGuests(guests);
        
        
        System.out.println( guests.getNumberOfEntries() + " Guests Saved!" );
    }

}
