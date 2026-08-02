/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

import System_UI.*;
import dao.GuestDatabase;
import dao.LoyaltyDatabase;
import dao.RoomDao;


public class Hotel_Management_System {

    public static void main(String[] args) {
        GuestDatabase guestDatabase = new GuestDatabase();
       //BookingUI bookingUI = new BookingUI();
        LoyaltyDatabase loyaltyDatabase = new LoyaltyDatabase();
        guestDatabase.createGuestData();
        //bookingUI.bookingMenu();
        loyaltyDatabase.createLoyaltyData();
        RoomDao.createRoomData();
        MainUI.MainUI();
    }
}